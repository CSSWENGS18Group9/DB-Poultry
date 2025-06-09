package org.db_poultry.db.supplyTypeDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyType {

    public static String createSupplyType(Connection conn, String supplyName, String unit){
        // TODO: remember that the unit is MAXIMUM of 12
        // TODO: also remember that supplyName and unit is not the empty string (lambda; monkaW)

        String query = "INSERT INTO supply_type (supply_name, unit) VALUES (?, ?)";

        try {
            PreparedStatement psmt = conn.prepareStatement(query);

            psmt.setString(1, supplyName);
            psmt.setString(2, unit);

            psmt.executeUpdate();

            psmt.close();

            return "INSERT INTO supply_type (supply_name, unit) VALUES(" + supplyName + ", " + unit + ")";
        } catch (SQLException e){
            generateErrorMessage("Error in `createSupplyType()` in `CreateSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e);

            return null;
        }
    }
}
