package org.db_poultry.db.supplyTypeDAO;

import org.db_poultry.util.undoSingleton;
import org.db_poultry.util.undoTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyType {

    /**
     * Creates a supply type and does the validation before insertion
     *
     * @param conn       the JDBC connection
     * @param supplyName the name of the supply
     * @param unit       the unit of the supply
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

        // transform the name to a valid name. If its null then the name itself is not unique
        String validName = validation_nameIsUniqueAndValid(conn, supplyName);
        if (validName == null) {
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

            psmt.setString(1, validName);
            psmt.setString(2, validUnit);
            psmt.executeUpdate();

            undoSingleton.INSTANCE.setUndoMode(undoTypes.doUndoSupplyType);

            return String.format("INSERT INTO supply_type (supply_name, unit) VALUES('%s', '%s')", validName,
                    validUnit);
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
    private static String validation_nameIsUniqueAndValid(Connection conn, String name) {
        name = name.strip().toLowerCase();
        return ReadSupplyType.getSupplyTypeByName(conn, name) == null ? name : null;
    }

    /**
     * Validation that the unit is valid (the length of the unit is >12)
     *
     * @param unit the unit
     * @return returns {null} if its invalid/unfixable, {String} the same/fixed unit string
     */
    public static String validation_unitIsValid(String unit) {
        // (1) remove spaces in between, if a space or a list of spaces does exist replace it with a single space
        //  hello###world (denote # as " ") ===> hello#world
        // (2) remove preceding and trailing spaces
        // (3) convert to lower case
        unit = unit.replaceAll("\\s+", " ").strip().toLowerCase();

        return unit.isEmpty() || unit.length() > 12 ? null : unit;
    }
}
