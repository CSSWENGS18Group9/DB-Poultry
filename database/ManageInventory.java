package org.db_poultry;

import java.sql.*;

public class SupplyManage {

    /**
     * Adds a new record into the Supply table
     *
     * @param connect       the Connection thing with SQL
     * @param supplyPrefix  the prefix of this supply
     * @param supplyID      the ID of this supply
     * @param quantity      the quantity of this supply
     * @param unit          the unit of this supply
     * @return an int if record was inserted properly
     */
    public static int addSupply(Connection connect, String supplyPrefix, int supplyID, int quantity, int unit) {
        String query = "INSERT INTO Supply (Supply_Prefix, Supply_ID, Quantity, Unit) VALUES (?, ?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setString(1, supplyPrefix);
            preppedStatement.setInt(2, supplyID);
            preppedStatement.setInt(3, quantity);
            preppedStatement.setInt(4, unit);

            int success = preppedStatement.executeUpdate(); // Executes query

            preppedStatement.close(); // Closes preparedStatement

            return success; // Returns 1 if success
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
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
         * @return an int if record was updated properly
         */
        public static int updateSupply (Connection connect, String supplyPrefix, String supplyID,int quantity, int unit) {
            String query = "UPDATE Supply SET Quantity = ?, unit = ? WHERE Supply_ID = ? AND Supply_Prefix = ?"; // Query to be used in preparedStatement

            try {
                PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

                // Sets the values to be updated
                preppedStatement.setInt(1, quantity);
                preppedStatement.setInt(2, unit);
                preppedStatement.setString(3, supplyID);
                preppedStatement.setString(4, supplyPrefix);

                int success = preppedStatement.executeUpdate(); // Executes query

                preppedStatement.close(); // Closes preparedStatement

                return success; // Returns 1 if success
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return 0;
            }
        }
}