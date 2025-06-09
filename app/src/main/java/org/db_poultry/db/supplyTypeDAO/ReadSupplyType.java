package org.db_poultry.db.supplyTypeDAO;

import org.db_poultry.pojo.SupplyType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadSupplyType {
    public static ArrayList<SupplyType> getAllSupplyTypes(Connection conn) {
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Supply_Type");

            try (ResultSet rs = pstmt.executeQuery()) {
                ArrayList<SupplyType> supplyTypes = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString("supply_name");
                    String unit = rs.getString("unit");
                    supplyTypes.add(new SupplyType(id, name, unit));
                }

                return supplyTypes;
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `getAllSupplyTypes()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e);
            return null;
        }
    }

    public static class CreateSupplyType {

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
}
