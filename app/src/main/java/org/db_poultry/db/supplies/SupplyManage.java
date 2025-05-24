package org.db_poultry.db.supplies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class SupplyManage {

    /**
     * Adds a new record into the Supply table
     *
     * @param connect       the Connection thing with SQL
     * @param supplyPrefix  the prefix of this supply
     * @param supplyID      the ID of this supply
     * @param quantity      the quantity of this supply
     * @param unit          the unit of this supply
     * @return a String which is the query with filled-in values
     */
    public static String addSupply(Connection connect, String supplyPrefix, int supplyID, int quantity, int unit) {
        String completeQuery = "INSERT INTO Supply (Supply_Prefix, Supply_ID, Quantity, Unit) VALUES (" + supplyPrefix + ", " + supplyID + ", " + quantity + ", " + unit + ")"; // Query filled in to be returned
        String incompleteQuery = "INSERT INTO Supply (Supply_Prefix, Supply_ID, Quantity, Unit) VALUES (?, ?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setString(1, supplyPrefix);
            preppedStatement.setInt(2, supplyID);
            preppedStatement.setInt(3, quantity);
            preppedStatement.setInt(4, unit);

            preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return completeQuery; // Returns the copied preppedStatement as a String
        } catch (SQLException e) {
            generateErrorMessage("Error in `addSupply()`.", "SQLException occurred.", "", e);
            return null;
        }
    }

    /**
     * Updates an existing record in the Supply table
     *
     * @param connect       the Connection thing with SQL
     * @param supplyPrefix  the prefix of this supply
     * @param supplyID      the ID of this supply
     * @param quantity      the quantity of this supply
     * @param unit          the unit of this supply
     * @return a String which is the query with filled-in values
     */
    public static String updateSupply(Connection connect, String supplyPrefix, String supplyID,int quantity, int unit) {
        String query = "UPDATE Supply SET Quantity = ?, unit = ? WHERE Supply_ID = ? AND Supply_Prefix = ?"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be updated
            preppedStatement.setInt(1, quantity);
            preppedStatement.setInt(2, unit);
            preppedStatement.setString(3, supplyID);
            preppedStatement.setString(4, supplyPrefix);

            preppedStatement.executeUpdate(); // Executes query

            String query2 = preppedStatement.toString(); // Stores the value of preppedStatement as a String

            preppedStatement.close(); // Closes preparedStatement

            return query2; // Returns 1 if success
        } catch (SQLException e) {
            generateErrorMessage("Error in `updateSupply()`.", "SQLException occurred.", "", e);
            return null;
        }
    }
}