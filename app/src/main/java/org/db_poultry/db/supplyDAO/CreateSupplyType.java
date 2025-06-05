package org.db_poultry.db.supplyDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyType {

    /**
     * Adds a new record into the Flock_Details table
     *
     * @param connect   the Connection thing with SQL
     * @param name      the name of the supply type
     * @param unit      the unit of the supply
     * @return a String which is the query with filled-in values
     */
    public static String createSupplyType(Connection connect, String name, String unit) {

        String completeQuery = "INSERT INTO Supply_Type (Supply_Name, Unit) VALUES (" + name + ", " + unit + ")"; // Query filled in to be returned
        String incompleteQuery = "INSERT INTO Supply_Type (Supply_Name, Unit) VALUES (?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setString(1, name);
            preppedStatement.setString(2, unit);

            preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return completeQuery; // Returns the filled-in query
        } catch (SQLException e) {
            generateErrorMessage("Error in `createSupplyType()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}