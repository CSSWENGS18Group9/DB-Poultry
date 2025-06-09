package org.db_poultry.db.supplyRecordDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class UpdateSupplyRecord {
    public static String supplyIsRetrieved(Connection conn, Date supplyDate, String supplyTypeName) {
        String sql = "UPDATE Supply_Record sr SET Retrieved = TRUE " +
                "FROM Supply_Type st " +
                "WHERE sr.Supply_Type_ID = st.Supply_Type_ID " +
                "AND sr.Retrieved = FALSE " +
                "AND sr.SR_Date = ? " +
                "AND st.Supply_Name = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setDate(1, supplyDate);
            pstmt.setString(2, supplyTypeName);
            pstmt.executeUpdate();
            pstmt.close();

            return "UPDATE Supply_Record sr SET Retrieved = TRUE FROM Supply_Type st" +
                    "WHERE sr.Supply_Type_ID = st.Supply_Type_ID AND sr.Retrieved = FALSE" +
                    "AND sr.SR_Date = " + supplyDate + " AND st.Supply_Name = " + supplyTypeName;
        } catch (SQLException e) {
            generateErrorMessage("Error in `supplyIsRetrieved()` in `UpdateSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e);

            return null;
        }
    }
}
