package org.db_poultry.db.supplyTypeDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyType {

    public static String createSupplyType(Connection conn, String supplyName, String unit) {
        String validUnit = validation_unitIsValid(unit);
        if (validUnit == null) {
            generateErrorMessage("Error in `createSupplyType()` in `CreateSupplyType`.", "Unit is invalid", "Ensure " +
                    "unit is valid, not empty and length at most 12", null);
            return null;
        }

        if (validation_nameIsUnique(conn, supplyName)) {
            generateErrorMessage("Error in `createSupplyType()` in `CreateSupplyTypr`", "Supply name is not unique",
                    "Make sure the new supply name is unique", null);
            return null;
        }

        try (PreparedStatement psmt = conn.prepareStatement("INSERT INTO supply_type (supply_name, unit) VALUES (?, " +
                "?)")) {
            psmt.setString(1, supplyName);
            psmt.setString(2, unit);
            psmt.executeUpdate();

            return "INSERT INTO supply_type (supply_name, unit) VALUES(" + supplyName + ", " + unit + ")";
        } catch (SQLException e) {
            generateErrorMessage("Error in `createSupplyType()` in `CreateSupplyType`.", "SQL Exception error " +
                    "occurred", "", e);
            return null;
        }
    }

    private static boolean validation_nameIsUnique(Connection conn, String name) {
        return ReadSupplyType.getSupplyTypeByName(conn, name) != null;
    }

    private static String validation_unitIsValid(String unit) {
        if (unit.isEmpty()) return null;

        String removeWhitespace = unit.replaceAll("\\s+", "");

        if (removeWhitespace.isEmpty() || (removeWhitespace.length() > 12)) return null;
        return removeWhitespace;
    }
}
