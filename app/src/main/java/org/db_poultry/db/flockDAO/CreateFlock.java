package org.db_poultry.db.flockDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateFlock {

    /**
     * Adds a new record into the Flock table
     *
     * @param connect    the Connection thing with SQL
     * @param startCount the starting count of this flock
     * @param startDate  the starting date of this flock
     * @return a String which is the query with filled-in values
     */
    public static String createFlock(Connection connect, int startCount, Date startDate) {
        Date actualDate = validate_dateIsValid(connect, startDate);
        // validate the data first, if at least one fails, don't create
        if (validate_startCountPositiveOrZero(startCount)) {
            generateErrorMessage(
                    "Error in `createFlock()` in `CreateFlock`.",
                    "The start count is invalid, must be positive or zero.",
                    "Verify that start count is 0 or a positive integer",
                    null);

            return null;
        }

        if (actualDate == null) {
            generateErrorMessage(
                    "Error in `createFlock()` in `CreateFlock`.",
                    "The start date is invalid",
                    "Verify that startDate is valid",
                    null);

            return null;
        }

        try (PreparedStatement preppedStatement = connect.prepareStatement("""
                    INSERT INTO Flock (Starting_Count, Starting_Date) VALUES (?, ?)
                """)) {

            preppedStatement.setInt(1, startCount);
            preppedStatement.setDate(2, actualDate);
            preppedStatement.executeUpdate(); // Executes query

            return String.format(
                    "INSERT INTO Flock (Starting_Count, Starting_Date) VALUES (%d, '%s')",
                    startCount, actualDate.toString()
            );
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `createFlock()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Validate that startCount is not negative
     *
     * @param startCount the start count
     * @return {startCount} is it meets constraints; {-1} otherwise
     */
    private static boolean validate_startCountPositiveOrZero(int startCount) {
        return startCount < 0;
    }

    /**
     * Validate date is UNIQUE and if it's null then default it to TODAY
     *
     * @param conn      the db connection
     * @param startDate the start date (may be null)
     * @return {startDate} if it meets the criteria, {null} otherwise
     */
    private static Date validate_dateIsValid(Connection conn, Date startDate) {
        // check if the startDate is null, if it is then set it as the default value
        Date actualDate = startDate != null ? startDate : Date.valueOf(LocalDate.now());

        // check if the inserted date is not overlapping with another flock range
        // we defn a flock range as the date range from [Flock.startDate, Flock.(last)Flock Detail.detail_date]
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT COUNT(*) AS overlaps FROM Flock LEFT JOIN (SELECT Flock_ID, MAX(FD_Date) as endDate
                FROM Flock_Details GROUP BY Flock_ID) Details ON Flock.Flock_ID = Details.Flock_ID WHERE ?
                BETWEEN Flock.Starting_Date AND COALESCE(Details.endDate, Flock.Starting_Date)
                """.stripIndent())) {
            pstmt.setDate(1, actualDate);

            // check the number of overlaps (either 0 or 1). if it overlaps then the date is invalid!
            int overlaps = 0;
            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) overlaps = result.getInt("overlaps");
            }

            if (overlaps != 0) return null;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `validate_dateIsValid()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }

        // check if there is a flock at that date already. If there is, return null. If there is NOT, return the date
        return ReadFlock.getFlockFromADate(conn, actualDate) == null ? actualDate : null;
    }
}