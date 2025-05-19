package org.db_poultry;

import java.sql.*;

public class SupplyManage {

    /**
     * Adds a new record into the Supply table
     *
     * @param connect  the Connection thing with SQL
     * @param supplyID the supply's ID
     * @param quantity the quantity of this supply
     * @param unit     the unit of this supply
     * @param type     the type of this supply
     * @return an int if record was inserted properly
     */
    public static int addSupply(Connection connect, String supplyID, int quantity, int unit, int type) {
        String query = "INSERT INTO Supply (Supply_ID, Quantity, Unit, Type) VALUES (?, ?, ?, ?)"; // Query to be used in preparedStatement

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

            // Sets the values to be added
            preppedStatement.setString(1, supplyID);
            preppedStatement.setInt(2, quantity);
            preppedStatement.setInt(3, unit);
            preppedStatement.setInt(4, type);

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
         * @param connect  the Connection thing with SQL
         * @param supplyID the supply's ID
         * @param quantity the quantity of this supply
         * @param unit     the unit of this supply
         * @param type     the type of this supply
         * @return an int if record was updated properly
         */
        public static int updateSupply (Connection connect, String supplyID,int quantity, int unit, int type) {
            String query = "UPDATE Supply SET Quantity = ?, unit = ?, type = ? WHERE Supply_ID = ?"; // Query to be used in preparedStatement

            try {
                PreparedStatement preppedStatement = connect.prepareStatement(query); // preparedStatement for SQL stuff

                // Sets the values to be updated
                preppedStatement.setInt(1, quantity);
                preppedStatement.setInt(2, unit);
                preppedStatement.setInt(3, type);
                preppedStatement.setString(4, supplyID);

                int success = preppedStatement.executeUpdate(); // Executes query

                preppedStatement.close(); // Closes preparedStatement

                return success; // Returns 1 if success
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return 0;
            }
        }
}