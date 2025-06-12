package org.db_poultry.db.supplyTypeDAO;

import org.db_poultry.pojo.SupplyPOJO.SupplyType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadSupplyType {
    public static ArrayList<SupplyType> getAllSupplyTypes(Connection conn) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT supply_type_id, supply_name, unit FROM Supply_Type")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                ArrayList<SupplyType> supplyTypes = new ArrayList<>();

                while (rs.next()) {
                    int id = rs.getInt("supply_type_id");
                    String name = rs.getString("supply_name");
                    String unit = rs.getString("unit");
                    supplyTypes.add(new SupplyType(id, name, unit));
                }

                return supplyTypes;
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `getAllSupplyTypes()` in `ReadSupplyType`.", "SQL Exception error occurred", "", e);
            return null;
        }
    }

    public static SupplyType getSupplyTypeByName(Connection conn, String name) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT supply_type_id, supply_name, unit FROM Supply_Types WHERE supple_name = ?")) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                SupplyType supplyType = null;

                while (rs.next()) {
                    int supplyTypeId = rs.getInt("supply_type_id");
                    String supplyTypeName = rs.getString("supply_name");
                    String unit = rs.getString("unit");
                    supplyType = new SupplyType(supplyTypeId, supplyTypeName, unit);
                }

                return supplyType;
            }

        } catch (SQLException e){
            generateErrorMessage("Error in `getSupplyType()` in `ReadSupplyType`.", "SQL Exception error occurred", "", e);
            return null;
        }
    }
}
