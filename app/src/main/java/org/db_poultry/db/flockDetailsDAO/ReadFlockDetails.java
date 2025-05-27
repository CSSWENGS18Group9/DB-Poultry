package org.db_poultry.db.flockDetailsDAO;

import org.db_poultry.pojo.FlockDetails;

import java.sql.*;

public class ReadFlockDetails {

    public static FlockDetails getMostRecent(Connection conn, Date flockDate) {
        String sql = "SELECT fd.flock_details_id, fd.flock_id, fd.fd_date, fd.depleted_count " +
                "FROM Flock_Details fd JOIN Flock f ON fd.Flock_ID = f.Flock_ID " +
                "WHERE f.Starting_Date = ? ORDER BY fd.FD_Date DESC LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, flockDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  // Move cursor to first row if present
                    int flock_details_id = rs.getInt("flock_details_id");
                    int flock_id = rs.getInt("flock_id");
                    Date fd_date = rs.getDate("fd_date");
                    int depleted_count = rs.getInt("depleted_count");

                    return new FlockDetails(flock_details_id, flock_id, fd_date, depleted_count);
                } else {
                    // No matching record found
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

}
