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
     * @param startDate     the starting date of this flock
     * @return a String which is the query with filled-in values
     */
    public static String createFlock(Connection connect, Timestamp startDate) {
        String query = "INSERT INTO Flock (Starting_Date) VALUES (?)"; // Query to be used in preparedStatement

        try {
            // FIXME: @justinching30 Fix this line
            // the complete query should be the (base version) of the string
            String completeQuery = "INSERT INTO Flock (Starting_Date) VALUES ("+ startDate +")";
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setTimestamp(1, startDate);

            preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return completeQuery; // Returns the copied preppedStatement as a String
        } catch (SQLException e) {
            generateErrorMessage("Error in `createFlock()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}
