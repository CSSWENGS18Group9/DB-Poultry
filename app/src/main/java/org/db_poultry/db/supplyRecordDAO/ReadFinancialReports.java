package org.db_poultry.db.supplyRecordDAO;

import java.sql.*;
import java.math.BigDecimal;
import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadFinancialReports {
    
    /**
     * Gets the average supply cost for a specific date
     * Let me know if my understanding of daily costs is correct here - Job 
     * 
     * @param conn The database connection
     * @param date The date to get the average for
     * @return Average cost for the day, or null if error occurs
     */
    public static BigDecimal getDailyAverageCost(Connection conn, Date date) {
        String sql = """
            SELECT 
                AVG(sr.Price) as avg_cost
            FROM Supply_Record sr
            JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
            WHERE sr.Price IS NOT NULL 
            AND sr.SR_Date = ?
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("avg_cost") : null;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                "Error in getDailyAverageCost()",
                "Failed to retrieve daily average cost",
                "",
                e
            );
            return null;
        }
    }

    /**
     * Gets the average supply cost for a specific week
     * 
     * @param conn The database connection
     * @param year The year
     * @param week The week number (1-53)
     * @return Average cost for the week, or null if error occurs
     */
    public static BigDecimal getWeeklyAverageCost(Connection conn, int year, int week) {
        String sql = """
            SELECT 
                AVG(sr.Price) as avg_cost
            FROM Supply_Record sr
            JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
            WHERE sr.Price IS NOT NULL 
            AND EXTRACT(YEAR FROM sr.SR_Date) = ?
            AND EXTRACT(WEEK FROM sr.SR_Date) = ?
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, week);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("avg_cost") : null;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                "Error in getWeeklyAverageCost()",
                "Failed to retrieve weekly average cost",
                "",
                e
            );
            return null;
        }
    }

    /**
     * Gets the average supply cost for a specific month
     * 
     * @param conn The database connection
     * @param year The year
     * @param month The month (1-12)
     * @return Average cost for the month, or null if error occurs
     */
    public static BigDecimal getMonthlyAverageCost(Connection conn, int year, int month) {
        String sql = """
            SELECT 
                AVG(sr.Price) as avg_cost
            FROM Supply_Record sr
            JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
            WHERE sr.Price IS NOT NULL 
            AND EXTRACT(YEAR FROM sr.SR_Date) = ?
            AND EXTRACT(MONTH FROM sr.SR_Date) = ?
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("avg_cost") : null;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                "Error in getMonthlyAverageCost()",
                "Failed to retrieve monthly average cost",
                "",
                e
            );
            return null;
        }
    }

    /**
     * Gets the average supply cost for a specific year
     * 
     * @param conn The database connection
     * @param year The year
     * @return Average cost for the year, or null if error occurs
     */
    public static BigDecimal getYearlyAverageCost(Connection conn, int year) {
        String sql = """
            SELECT 
                AVG(sr.Price) as avg_cost
            FROM Supply_Record sr
            JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
            WHERE sr.Price IS NOT NULL 
            AND EXTRACT(YEAR FROM sr.SR_Date) = ?
        """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("avg_cost") : null;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                "Error in getYearlyAverageCost()",
                "Failed to retrieve yearly average cost",
                "",
                e
            );
            return null;
        }
    }
}