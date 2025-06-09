package org.db_poultry.db.supplyRecordDAO;

import org.db_poultry.pojo.SupplyComplete;

import java.sql.*;
import java.util.ArrayList;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadSupplyRecord {

    private static ArrayList<SupplyComplete> readList(Connection conn, PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            ArrayList<SupplyComplete> records = new ArrayList<>();

            while (rs.next()) {
                int supply_id = rs.getInt("Supply_ID");
                int supply_type_id = rs.getInt("Supply_Type_ID");
                Date srDate = rs.getDate("SR_Date");
                String supply_name = rs.getString("Supply_Name");
                String unit = rs.getString("Unit");
                float added = rs.getFloat("Added");
                float consumed = rs.getFloat("Consumed");
                boolean retrieved = rs.getBoolean("Retrieved");

                records.add(new SupplyComplete(supply_id, supply_type_id, srDate, supply_name, unit, added, consumed, retrieved));
            }
            return records;
        } catch (SQLException e) {
            generateErrorMessage("Error in `readList()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e);
            return null;
        }
    }

    public static ArrayList<SupplyComplete> getFromDate(Connection conn, Date date) {

        String sql = "SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st.Supply_Name, st.Unit, " +
                "sr.Added, sr.Consumed, sr.Retrieved " +
                "FROM Supply_Record sr " +
                "JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID " +
                "WHERE sr.SR_Date = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);

            return readList(conn, pstmt);

        } catch (SQLException e) {
            generateErrorMessage("Error in `getFromDate()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e);
            return null;
        }
    }

    public static ArrayList<SupplyComplete> getFromName(Connection conn, String supplyName) {
        String sql = "SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st.Supply_Name, st.Unit, " +
                "sr.Added, sr.Consumed, sr.Retrieved " +
                "FROM Supply_Record sr " +
                "JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID " +
                "WHERE st.Supply_Name = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplyName);

            return readList(conn, pstmt);
        } catch (SQLException e) {
            generateErrorMessage("Error in `getFromName()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e);
            return null;
        }
    }
}
