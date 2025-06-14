package org.db_poultry.db.supplyRecordDAO;

import org.db_poultry.pojo.SupplyPOJO.SupplyComplete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadSupplyRecord {

    private static SupplyComplete getFromRS(ResultSet rs) throws SQLException {
        int supply_id = rs.getInt("Supply_ID");
        int supply_type_id = rs.getInt("Supply_Type_ID");
        Date srDate = rs.getDate("SR_Date");
        String supply_name = rs.getString("Supply_Name");
        String unit = rs.getString("Unit");
        float added = rs.getFloat("Added");
        float consumed = rs.getFloat("Consumed");
        boolean retrieved = rs.getBoolean("Retrieved");

        return new SupplyComplete(supply_id, supply_type_id, srDate, supply_name, unit, added, consumed,
                retrieved);

    }

    private static ArrayList<SupplyComplete> readList(PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            ArrayList<SupplyComplete> records = new ArrayList<>();

            while (rs.next()) {
                records.add(getFromRS(rs));
            }
            return records;
        } catch (SQLException e) {
            generateErrorMessage("Error in `readList()` in `ReadSupplyRecord`.", "SQL Exception error occurred", "", e);
            return null;
        }
    }

    public static ArrayList<SupplyComplete> getFromDate(Connection conn, Date date) {
        try (PreparedStatement pstmt =
                     conn.prepareStatement("SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st" + ".Supply_Name, " +
                             "st.Unit, sr.Added, sr.Consumed, sr.Retrieved FROM Supply_Record sr JOIN Supply_Type st" + " ON sr.Supply_Type_ID = st.Supply_Type_ID WHERE sr.SR_Date = ?")) {
            pstmt.setDate(1, date);

            return readList(pstmt);
        } catch (SQLException e) {
            generateErrorMessage("Error in `getFromDate()` in `ReadSupplyRecord`.", "SQL Exception error occurred",
                    "", e);
            return null;
        }
    }

    public static ArrayList<SupplyComplete> getFromName(Connection conn, String supplyName) {
        try (PreparedStatement pstmt =
                     conn.prepareStatement("SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st" + ".Supply_Name, " +
                             "st.Unit, sr.Added, sr.Consumed, sr.Retrieved FROM Supply_Record sr JOIN Supply_Type st" + " ON sr.Supply_Type_ID = st.Supply_Type_ID WHERE st.Supply_Name = ?;")) {
            pstmt.setString(1, supplyName);

            return readList(pstmt);
        } catch (SQLException e) {
            generateErrorMessage("Error in `getFromName()` in `ReadSupplyRecord`.", "SQL Exception error occurred",
                    "", e);
            return null;
        }
    }

    public static SupplyComplete getOneByDateAndName(Connection conn, Date date, String supplyName) {
        try (PreparedStatement pstmt =
                     conn.prepareStatement("SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st" + ".Supply_Name, " +
                             "st.Unit, sr.Added, sr.Consumed, sr.Retrieved FROM Supply_Record sr JOIN Supply_Type st" + " ON sr.Supply_Type_ID = st.Supply_Type_ID WHERE st.Supply_Name = ? AND sr.SR_Date = ?")) {
            pstmt.setString(1, supplyName);
            pstmt.setDate(2, date);

            ArrayList<SupplyComplete> result = readList(pstmt);
            if (result != null && !result.isEmpty()) {
                return result.getFirst();
            } else {
                return null;
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `getOneByDateAndName()` in `ReadSupplyRecord`.", "SQL Exception error " +
                    "occurred", "", e);
            return null;
        }
    }

    public static SupplyComplete getMostRecentFromName(Connection conn, String supplyName) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st" +
                ".Supply_Name, st.Unit, sr.Added, sr.Consumed, sr.Retrieved FROM Supply_Record sr JOIN" +
                " Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID WHERE sr.Supply_Name = ? ORDER BY sr" +
                ".SR_Date DESC LIMIT 1")) {

            pstmt.setString(1, supplyName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return getFromRS(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `getMostRecentFromName()` in `ReadSupplyRecord`.", "SQL Exception error " +
                    "occurred", "", e);
            return null;
        }
    }

    public static SupplyComplete getMostRecentFromID(Connection conn, int ID) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st" +
                ".Supply_Name, st.Unit, sr.Added, sr.Consumed, sr.Retrieved FROM Supply_Record sr JOIN" +
                " Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID WHERE sr.Supply_Type_ID = ? ORDER BY sr" +
                ".SR_Date DESC LIMIT 1")) {

            pstmt.setInt(1, ID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return getFromRS(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `getMostRecentFromName()` in `ReadSupplyRecord`.", "SQL Exception error " +
                    "occurred", "", e);
            return null;
        }
    }


    public static BigDecimal getCurrentCountForDate(Connection conn, int supplyTypeID, Date currentDate) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                WITH last_retrieved AS (
                    SELECT MAX(SR_Date) AS last_retrieved_date
                    FROM Supply_Record
                    WHERE Supply_Type_ID = ?
                      AND Retrieved = TRUE
                      AND SR_Date <= ?
                ),
                start_date AS (
                    SELECT CASE
                        WHEN (SELECT last_retrieved_date FROM last_retrieved) IS NOT NULL THEN
                            (SELECT MIN(SR_Date)
                             FROM Supply_Record
                             WHERE Supply_Type_ID = ?
                               AND SR_Date > (SELECT last_retrieved_date FROM last_retrieved))
                        ELSE
                            (SELECT MIN(SR_Date)
                             FROM Supply_Record
                             WHERE Supply_Type_ID = ?)
                    END AS first_after_retrieved
                ),
                range_records AS (
                    SELECT *
                    FROM Supply_Record
                    WHERE Supply_Type_ID = ?
                      AND SR_Date BETWEEN (SELECT first_after_retrieved FROM start_date) AND ?
                )
                SELECT COALESCE(SUM(Added) - SUM(Consumed), 0) AS currentCount
                FROM range_records
                """)) {
            pstmt.setInt(1, supplyTypeID);
            pstmt.setDate(2, currentDate);
            pstmt.setInt(3, supplyTypeID);
            pstmt.setInt(4, supplyTypeID);
            pstmt.setInt(5, supplyTypeID);
            pstmt.setDate(6, currentDate);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("currentCount").setScale(4, RoundingMode.HALF_UP);
            } else {
                return null;
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `getCurrentCountForDate()` in `ReadSupplyRecord`.",
                    "SQL Exception occurred", "", e);
            return null;
        }
    }
}
