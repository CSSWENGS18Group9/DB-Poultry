package org.db_poultry.db.supplyRecordDAO;

import org.db_poultry.pojo.SupplyRecord;

import java.sql.*;
import java.util.ArrayList;

public class ReadSupplyRecord {
    public static ArrayList<SupplyRecord> getFromDate(Connection conn, Date date) {
        String sql = "SELECT sr.SR_Date, st.Supply_Name, sr.Added, sr.Consumed, st.Unit," + "sr.Retrieved FROM Supply_Record sr JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID" + "WHERE sr.SR_Date = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                ArrayList<SupplyRecord> records = new ArrayList<>();

                while (rs.next()) {
                }
            }


        } catch (SQLException e) {

        }
    }
}
