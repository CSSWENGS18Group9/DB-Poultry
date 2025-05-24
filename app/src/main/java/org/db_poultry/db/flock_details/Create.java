package org.db_poultry.db.flock_details;

import java.sql.*;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Create {

    /**
     * Adds a new record into the Flock_Details table
     *
     * @param connect       the Connection thing with SQL
     * @param flockID       the flock ID this poultry is connected to
     * @param date          the starting date of this flock
     * @param depleted      the amount of dead chicken
     * @param curCount      the current amount of chickens
     * @return a String which is the query with filled-in values
     */
    public static String createFlockDetails(Connection connect, int flockID, Timestamp date, int depleted, int curCount) {
        String completeQuery = "INSERT INTO Flock_Details (Flock_ID, FD_Date, Current_Count, Depleted_Count) VALUES (" + flockID + ", " + date + ", " + curCount + ", " + depleted + ")"; // Query filled in to be returned
        String incompleteQuery = "INSERT INTO Flock_Details (Flock_ID, FD_Date, Current_Count, Depleted_Count) VALUES (?, ?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setInt(1, flockID);
            preppedStatement.setTimestamp(2, date);
            preppedStatement.setInt(3, curCount);
            preppedStatement.setInt(4, depleted);

            preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return completeQuery; // Returns the filled-in query
        } catch (SQLException e) {
            generateErrorMessage("Error in `createFlockDetails()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}
