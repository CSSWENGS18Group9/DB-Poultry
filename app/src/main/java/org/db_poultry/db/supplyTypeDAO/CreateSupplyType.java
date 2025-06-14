package org.db_poultry.db.supplyTypeDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyType {

    /**
     * Creates a supply type and does the validation before insertion
     *
     * @param conn the JDBC connection
     * @param supplyName the name of the supply
     * @param unit the unit of the supply
     * @return the SQL query as a string if it worked, null otherwise
     */
    public static String createSupplyType(Connection conn, String supplyName, String unit) {
        // transform the unit to a valid unit (no whitespace and stuff). If its null, then the unit is invalid
        String validUnit = validation_unitIsValid(unit);
        if (validUnit == null) {
            generateErrorMessage(
                    "Error in `createSupplyType()` in `CreateSupplyType`.",
                    "Unit is invalid",
                    "Ensure unit is valid, not empty and length at most 12",
                    null
            );

            return null;
        }

        if (validation_nameIsUnique(conn, supplyName)) {
            generateErrorMessage(
                    "Error in `createSupplyType()` in `CreateSupplyType`",
                    "Supply name is not unique",
                    "Make sure the new supply name is unique",
                    null
            );

            return null;

        }
    
        try (PreparedStatement psmt = conn.prepareStatement("""
                INSERT INTO supply_type (supply_name, unit) VALUES (?, ?)
                """)) {

            psmt.setString(1, supplyName);
            psmt.setString(2, unit);
            psmt.executeUpdate();

            return "INSERT INTO supply_type (supply_name, unit) VALUES(" + supplyName + ", " + unit + ")";
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `createSupplyType()` in `CreateSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );
            return null;
        }
    }

    /**
     * Validation that the name is NOT unique (checks for the wrong case)
     *
     * @param conn the JDBC connection
     * @param name the name of the supply
     * @return {true} if it is not unique, {false} otherwise
     */
    private static boolean validation_nameIsUnique(Connection conn, String name) {
        return ReadSupplyType.getSupplyTypeByName(conn, name) != null;
    }

    /**
     * Validation that the unit is valid (the length of the unit is >12)
     * @param unit the unit
     * @return returns {null} if its invalid/unfixable, {String} the same/fixed unit string
     */
    private static String validation_unitIsValid(String unit) {
        if (unit.isEmpty()) return null;

        String removeWhitespace = unit.replaceAll("\\s+", "");

        if (removeWhitespace.isEmpty() || (removeWhitespace.length() > 12)) return null;
        return removeWhitespace;
    }
}
