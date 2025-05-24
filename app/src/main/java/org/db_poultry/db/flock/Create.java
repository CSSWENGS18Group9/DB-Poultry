package org.db_poultry.db.flock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Create {

    /**
     * Adds a new record into the Flock table
     *
     * @param connect       the Connection thing with SQL
     * @param startDate     the starting date of this flock
     * @return a String which is the query with filled-in values
     */
    public static String createFlock(Connection connect, Date startDate) {
        String completeQuery = "INSERT INTO Flock (Starting_Date) VALUES (" + startDate + ")"; // Query filled in to be returned
        String incompleteQuery = "INSERT INTO Flock (Starting_Date) VALUES (?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setDate(1, startDate);

            preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return completeQuery; // Returns the filled-in query
        } catch (SQLException e) {
            generateErrorMessage("Error in `createFlock()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}
