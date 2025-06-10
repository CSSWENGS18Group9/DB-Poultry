package org.db_poultry.db.flockDetailsDAO;

import org.db_poultry.pojo.FlockPOJO.Flock;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;

import java.sql.*;
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
        Date actualFlockDate = flockDate != null ? flockDate : Date.valueOf(java.time.LocalDate.now());

        // get the flock this flock detail belongs to, if it is null we cannot create a flock detail
        Flock flock = getFlockFromADate(conn, actualFlockDate);
        if (flock == null) return null;

        // validate the detailDate is possible
        Date actualDetailDate = validate_dateIsValid(conn, flock, detailDate);
        if (actualDetailDate == null) return null;

        // check if the depleted count is valid and makes sense
        if (!validate_depletedCountIsPossible(conn, flock, depleted)) return null;

        int flockID = flock.getFlockId();
        try (PreparedStatement preppedStatement = conn.prepareStatement("INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (?, ?, ?)")) {
            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setDate(2, actualDetailDate);
            preppedStatement.setInt(3, depleted);
            preppedStatement.executeUpdate(); // Executes query

            return "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (" + flockID + ", " + actualDetailDate + ", " + depleted + ")"; // Returns the filled-in query
        } catch (SQLException e) {
            generateErrorMessage("Error in `createFlockDetails()`.", "SQLException occurred.", "", e);
            return null;
        }
    }

    private static boolean validate_depletedCountIsPossible(Connection conn, Flock flock, int depleted) {
        // check first if depleted is 0 or a positive integer
        if (depleted < 0) return false;

        // check the flock's starting count and its pre-existing flock detail depleted count. The starting count
        // and other depleted counts must make sense even after this new depleted count.
        List<FlockDetails> flockDetails = ReadFlockDetails.getFlockDetailsFromFlock(conn, flock.getStartingDate());

        // there seems to be an SQL error that occured
        if (flockDetails == null) return false;

        // get the totalDepleted (from all pre-existing flock details)
        int totalDepleted = 0;
        if (!flockDetails.isEmpty()) {
            for (FlockDetails flockDetail : flockDetails) totalDepleted += flockDetail.getDepletedCount();
        }

        return flock.getStartingCount() - totalDepleted >= depleted;
    }

    private static Date validate_dateIsValid(Connection conn, Flock flock, Date detailDate) {
        Date actualDate = detailDate != null ? detailDate : Date.valueOf(java.time.LocalDate.now());

        // check if the detail date is after the starting date of the flock
        if (actualDate.before(flock.getStartingDate())) return null;

        // first get the next starting date of the "nearest" Flock
        // we will use this to see the total range of a Flock since its range is given by
        // [i_Flock.startingDate, j_Flock.startingDate] where i < j (i comes before j)
        Date nextStartDate = null;

        try (PreparedStatement drStmt = conn.prepareStatement("SELECT MIN(Starting_Date) AS nextStartDate FROM Flock WHERE Starting_Date > ?")) {
            drStmt.setDate(1, flock.getStartingDate());
            try (ResultSet rs = drStmt.executeQuery()) {
                if (rs.next()) {
                    nextStartDate = rs.getDate(1);
                }
            }
        } catch (SQLException e) {
            return null;
        }

        // if the actualDate is out of scope (that is, it is in another Flock not in the Flock that we want)
        // say the Date is invalid
        if (nextStartDate != null && actualDate.after(nextStartDate) || actualDate.equals(nextStartDate)) return null;

        // check if the inserted date is not overlapping with another flock range or another detail
        // we defn a flock range as the date range from [Flock.startDate, Flock.(last)Flock Detail.detail_date]
        String checkOverlapQuery = """
                SELECT COUNT(*) AS overlaps FROM Flock LEFT JOIN (SELECT Flock_ID, MAX(FD_Date) as endDate
                FROM Flock_Details GROUP BY Flock_ID) Details ON Flock.Flock_ID = Details.Flock_ID WHERE ?
                BETWEEN Flock.Starting_Date AND COALESCE(Details.endDate, Flock.Starting_Date)
                """.stripIndent();
        int overlaps = 0;
        try (PreparedStatement coStmt = conn.prepareStatement(checkOverlapQuery)) {
            coStmt.setDate(1, flock.getStartingDate());
            try (ResultSet rs = coStmt.executeQuery()) {
                if (actualDate != flock.getStartingDate()) {
                    while (rs.next()) {
                        overlaps += rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            return null;
        }

        return overlaps == 0 ? null : actualDate;
    }
}