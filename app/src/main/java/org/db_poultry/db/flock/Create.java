package org.db_poultry.db.flock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Create {

    /**
     * Adds a new record into the Flock table
     *
     * @param connect       the Connection thing with SQL
     * @param flockID       the ID of this flock
     * @param startDate     the starting date of this flock
     * @return a String which is the query with filled-in values
     */
    public static String createFlock(Connection connect, int flockID, Timestamp startDate) {
        String query = "INSERT INTO Flock (Flock_ID, Starting_Date) VALUES (?,?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setTimestamp(2, startDate);

            preppedStatement.executeUpdate(); // Executes query

            String query2 = preppedStatement.toString(); // Stores the value of preppedStatement as a String

            preppedStatement.close(); // Closes preparedStatement

            return query2; // Returns the copied preppedStatement as a String
        } catch (SQLException e) {
            generateErrorMessage("Error in `createFlock()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}
