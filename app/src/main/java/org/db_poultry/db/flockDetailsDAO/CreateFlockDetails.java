package org.db_poultry.db.flockDetailsDAO;

import org.db_poultry.pojo.FlockPOJO.Flock;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;
import org.db_poultry.util.undoSingleton;
import org.db_poultry.util.undoTypes;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.db_poultry.db.flockDAO.ReadFlock.getFlockFromADate;
import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateFlockDetails {

    /**
     * Adds a new record into the Flock_Details table
     *
     * @param conn       the Connection thing with SQL
     * @param flockDate  the flock ID this flock detail is connected to
     * @param detailDate the starting date of this flock
     * @param depleted   the amount of dead chickens
     * @return a String which is the query with filled-in values
     */
    public static String createFlockDetails(Connection conn, Date flockDate, Date detailDate, int depleted) {
        // check first if flockDate is empty
        Date actualFlockDate = flockDate != null ? flockDate : Date.valueOf(LocalDate.now());

        // get the flock this flock detail belongs to, if it is null we cannot create a flock detail
        Flock flock = getFlockFromADate(conn, actualFlockDate);
        if (flock == null) {
            generateErrorMessage(
                    "Error in `createFlockDetails()` in `CreateFlockDetails`.",
                    "A flock does not exist using the provided date.",
                    "Ensure the provided date has a pre-existing flock.",
                    null
            );

            return null;
        }

        // validate the detailDate is possible
        Date actualDetailDate = validate_dateIsValid(conn, flock, detailDate);
        if (actualDetailDate == null) {
            generateErrorMessage(
                    "Error in `createFlockDetails()` in `CreateFlockDetails`.",
                    "The detail date selected is invalid.",
                    "Ensure that the detail date is possible.",
                    null
            );

            return null;
        }

        // check if the depleted count is valid and makes sense
        if (validate_depletedCountIsPossible(conn, flock, depleted)) {
            generateErrorMessage(
                    "Error in `createFlockDetails()` in `CreateFlockDetails`.",
                    "The depleted count is invalid.",
                    "Ensure that the depleted count ensures that the TOTAL count is positive.",
                    null
            );

            return null;
        }

        int flockID = flock.getFlockId();
        try (PreparedStatement preppedStatement = conn.prepareStatement("""
                INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (?, ?, ?)
                """)) {

            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setDate(2, actualDetailDate);
            preppedStatement.setInt(3, depleted);
            preppedStatement.executeUpdate(); // Executes query

            DeleteFlockDetail.setFlockDetailsToDelete(flockID, actualDetailDate);

            undoSingleton.INSTANCE.setUndoMode(undoTypes.doUndoFlockDetail);

            return String.format(
                    "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (%d, '%s', %d)",
                    flockID, actualDetailDate.toString(), depleted);
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `createFlockDetails()`.",
                    "SQLException occurred.",
                    "",
                    e);
            return null;
        }
    }

    /**
     * Validation for the depleted count, check if the depleted count makes
     * pre-existing numeric values make sense
     *
     * @param conn     the JDBC connection
     * @param flock    the flock
     * @param depleted the depleted count
     * @return {true} if it is possible, {false} otherwise
     */
    private static boolean validate_depletedCountIsPossible(Connection conn, Flock flock, int depleted) {
        // check first if depleted is 0 or a positive integer
        if (depleted < 0)
            return true;

        // does not really matter if depleted is 0
        if (depleted == 0)
            return false;

        // check the flock's starting count and its pre-existing flock detail depleted
        // count. The starting count
        // and other depleted counts must make sense even after this new depleted count.
        List<FlockDetails> flockDetails = ReadFlockDetails.getFlockDetailsFromFlock(conn, flock.getStartingDate());

        if (flockDetails == null)
            return depleted > flock.getStartingCount();

        // get the totalDepleted (from all pre-existing flock details)
        int totalDepleted = 0;
        for (FlockDetails flockDetail : flockDetails)
            totalDepleted += flockDetail.getDepletedCount();

        return totalDepleted + depleted > flock.getStartingCount();
    }

    /**
     * Validates if the date is valid.
     *
     * @param conn       the JDBC connection
     * @param flock      the flock
     * @param detailDate the date of the flock detail
     * @return returns a valid date or null
     */
    private static Date validate_dateIsValid(Connection conn, Flock flock, Date detailDate) {
        Date actualDate = (detailDate != null) ? detailDate : Date.valueOf(java.time.LocalDate.now());

        // check if the detail date is after the starting date of the flock
        if (actualDate.before(flock.getStartingDate()))
            return null;

        // first get the next starting date of the "nearest" Flock
        // we will use this to see the total range of a Flock since its range is given
        // by
        // [i_Flock.startingDate, j_Flock.startingDate] where i < j (i comes before j)
        Date nextStartDate = null;
        try (PreparedStatement drStmt = conn.prepareStatement("""
                    SELECT MIN(Starting_Date) AS nextStartDate 
                    FROM Flock 
                    WHERE Starting_Date > ?
                """)) {
            drStmt.setDate(1, flock.getStartingDate());
            try (ResultSet rs = drStmt.executeQuery()) {
                if (rs.next()) {
                    nextStartDate = rs.getDate("nextStartDate");
                }
            }
        } catch (SQLException e) {
            return null;
        }

        if (nextStartDate != null && !actualDate.before(nextStartDate))
            return null;

        // check if the inserted date is not overlapping with another flock range or
        // another detail
        // we defn a flock range as the date range from [Flock.startDate,
        // Flock.(last)Flock Detail.detail_date]
        String checkOverlapQuery = """
                SELECT COUNT(*) AS overlaps
                FROM Flock
                LEFT JOIN (
                    SELECT Flock_ID, MAX(FD_Date) AS endDate
                    FROM Flock_Details
                    GROUP BY Flock_ID
                ) Details ON Flock.Flock_ID = Details.Flock_ID
                WHERE Flock.Flock_ID != ?
                  AND ? BETWEEN Flock.Starting_Date AND COALESCE(Details.endDate, Flock.Starting_Date)
                """;

        int overlaps = 0;
        try (PreparedStatement coStmt = conn.prepareStatement(checkOverlapQuery)) {
            coStmt.setInt(1, flock.getFlockId());
            coStmt.setDate(2, actualDate);

            try (ResultSet rs = coStmt.executeQuery()) {
                if (rs.next()) {
                    overlaps = rs.getInt("overlaps");
                }
            }
        } catch (SQLException ex) {
            return null;
        }

        return overlaps > 0 ? null : actualDate;
    }

}