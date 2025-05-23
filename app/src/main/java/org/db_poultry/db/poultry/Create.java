package org.db_poultry.db.poultry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Create {

    /**
     * Adds a new record into the Poultry table
     *
     * @param connect       the Connection thing with SQL
     * @param flockID       the flock ID this poultry is connected to
     * @param date          the starting date of this flock
     * @param depleted      the amount of dead chicken
     * @param curCount      the current amount of chickens
     * @return a String which is the query with filled-in values
     */
    public static String createPoultry(Connection connect, int flockID, Timestamp date, int depleted, int curCount) {
        String query = "INSERT INTO Flock_Details (Flock_ID, FD_Date, Current_Count, Depleted_Count) VALUES (?, ?, ?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setTimestamp(2, date);
            preppedStatement.setInt(3, depleted);
            preppedStatement.setInt(4, curCount);

            preppedStatement.executeUpdate(); // Executes query

            String query2 = preppedStatement.toString(); // Stores the value of preppedStatement as a String

            preppedStatement.close(); // Closes preparedStatement

            return query2; // Returns the copied preppedStatement as a String
        } catch (SQLException e) {
            generateErrorMessage("Error in `createPoultry()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}
