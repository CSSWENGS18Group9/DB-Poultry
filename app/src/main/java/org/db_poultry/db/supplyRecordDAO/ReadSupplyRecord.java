package org.db_poultry.db.supplyRecordDAO;

import org.db_poultry.db.supplyTypeDAO.ReadSupplyType;
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadSupplyRecord {

    /**
     * Returns a supply complete object from a result set. Not to be used out of scope
     *
     * @param rs the result set
     * @return the supply complete object from the values of the sr
     */
    private static SupplyComplete getFromRS(ResultSet rs) throws SQLException {
        int supply_id = rs.getInt("Supply_ID");
        int supply_type_id = rs.getInt("Supply_Type_ID");
        Date srDate = rs.getDate("SR_Date");
        String supply_name = rs.getString("Supply_Name");
        String unit = rs.getString("Unit");

        BigDecimal added = rs.getBigDecimal("Added");
        if (added != null) {
            added = added.setScale(4, RoundingMode.DOWN);
        }

        BigDecimal consumed = rs.getBigDecimal("Consumed");
        if (consumed != null) {
            consumed = consumed.setScale(4, RoundingMode.DOWN);
        }

        BigDecimal current = rs.getBigDecimal("Current_Count");

        boolean retrieved = rs.getBoolean("Retrieved");

        return new SupplyComplete(supply_id, supply_type_id, srDate, supply_name, unit, added, consumed, current, retrieved);
    }


    /**
     * Returns an ArrayList of supply complete objects given some prepared statement. Not to be used out of scope
     *
     * @param pstmt the prepared statement
     * @return the array list of supple complete objects
     */
    private static ArrayList<SupplyComplete> readList(PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            ArrayList<SupplyComplete> records = new ArrayList<>();

            while (rs.next()) {
                records.add(getFromRS(rs));
            }
            return records;

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `readList()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Returns an array list of supply objects given some date
     *
     * @param conn the JDBC connection
     * @param date the date
     * @return ArrayList of supply objects, {null} when something is caught
     */
    public static ArrayList<SupplyComplete> getFromDate(Connection conn, Date date) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, st.Supply_Name, st.Unit, sr.Added, 
                sr.Consumed, sr.Current_Count, sr.Retrieved FROM Supply_Record sr JOIN Supply_Type st 
                ON sr.Supply_Type_ID = st.Supply_Type_ID WHERE sr.SR_Date = ?
                """)) {

            pstmt.setDate(1, date);

            ArrayList<SupplyComplete> records = readList(pstmt);
            return Objects.requireNonNull(records).isEmpty() ? null : records;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getFromDate()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Returns an array list of supply objects given some supply name
     *
     * @param conn       the JDBC connection
     * @param supplyName the supply name
     * @return ArrayList of supply objects, {null} when something is caught
     */
    public static ArrayList<SupplyComplete> getFromName(Connection conn, String supplyName) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT
                        sr.Supply_ID,
                        sr.Supply_Type_ID,
                        sr.SR_Date,
                        st.Supply_Name,
                        st.Unit,
                        sr.Added,
                        sr.Consumed,
                        sr.Current_Count,
                        sr.Retrieved
                    FROM Supply_Record sr
                    JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
                    WHERE st.Supply_Name = ?;
                """)) {

            pstmt.setString(1, supplyName);

            ArrayList<SupplyComplete> ret = readList(pstmt);

            return Objects.requireNonNull(ret).isEmpty() ? null : ret;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getFromName()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Returns an array list of supply objects from the latest
     *
     * @param conn       the JDBC connection
     * @param supply_type_id_query the supply name
     * @return ArrayList of supply objects, {null} when something is caught
     */
    public static SupplyComplete getLatest(Connection conn, int supply_type_id_query) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT
                        sr.Supply_ID,
                        sr.Supply_Type_ID,
                        sr.SR_Date,
                        st.Supply_Name,
                        st.Unit,
                        sr.Added,
                        sr.Consumed,
                        sr.Current_Count,
                        sr.Retrieved
                    FROM Supply_Record sr
                    JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
                    WHERE st.Supply_Type_ID = ?
                    ORDER BY sr.SR_Date DESC
                    LIMIT 1;
                """)) {

            pstmt.setInt(1, supply_type_id_query);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  // Move cursor to first row if present
                    int supply_id = rs.getInt("supply_id");
                    int supply_type_id = rs.getInt("supply_type_id");
                    Date sr_date = rs.getDate("sr_date");
                    String supply_name = rs.getString("supply_name");
                    String unit = rs.getString("unit");
                    BigDecimal added = rs.getBigDecimal("added");
                    BigDecimal consumed = rs.getBigDecimal("consumed");
                    BigDecimal current_count = rs.getBigDecimal("current_count");
                    boolean retrieved = rs.getBoolean("retrieved");

                    return new SupplyComplete(supply_id, supply_type_id, sr_date, supply_name, unit, added, consumed, current_count, retrieved);
                }

                return null; // No matching record found
            }

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getLatest()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Gets a single supply complete object (if it exists) given some date and supply name
     *
     * @param conn       the JDBC connection
     * @param date       the date
     * @param supplyName the supply name
     * @return the supply object, {null} if it doesn't exist
     */
    public static SupplyComplete getOneByDateAndName(Connection conn, Date date, String supplyName) {
        try (PreparedStatement pstmt =
                     conn.prepareStatement("""
                             SELECT sr.Supply_ID, sr.Supply_Type_ID, sr.SR_Date, 
                                    st.Supply_Name, st.Unit, sr.Added, sr.Consumed, sr.Current_Count, sr.Retrieved 
                             FROM Supply_Record sr JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID 
                             WHERE st.Supply_Name = ? AND sr.SR_Date = ?
                             """)) {

            pstmt.setString(1, supplyName);
            pstmt.setDate(2, date);

            ArrayList<SupplyComplete> result = readList(pstmt);
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }

            return null;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getOneByDateAndName()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Get the most recent record from a supply name
     *
     * @param conn       the JDBC connection
     * @param supplyName the supple name
     * @return the mist recent supply object, {null} if it doesn't exist
     */
    public static SupplyComplete getMostRecentFromName(Connection conn, String supplyName) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT
                        sr.Supply_ID,
                        sr.Supply_Type_ID,
                        sr.SR_Date,
                        st.Supply_Name,
                        st.Unit,
                        sr.Added,
                        sr.Consumed,
                        sr.Current_Count,
                        sr.Retrieved
                    FROM Supply_Record sr
                    JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
                    WHERE st.Supply_Name = ?
                    ORDER BY sr.SR_Date DESC
                    LIMIT 1
                """)) {

            pstmt.setString(1, supplyName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return getFromRS(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getMostRecentFromName()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Get the most recent record from a supply ID
     *
     * @param conn the JDBC connection
     * @param ID   the supply ID
     * @return the mist recent supply object, {null} if it doesn't exist
     */
    public static SupplyComplete getMostRecentFromID(Connection conn, int ID) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT
                        sr.Supply_ID,
                        sr.Supply_Type_ID,
                        sr.SR_Date,
                        st.Supply_Name,
                        st.Unit,
                        sr.Added,
                        sr.Consumed,
                        sr.Current_Count,
                        sr.Retrieved
                    FROM Supply_Record sr
                    JOIN Supply_Type st ON sr.Supply_Type_ID = st.Supply_Type_ID
                    WHERE sr.Supply_Type_ID = ?
                    ORDER BY sr.SR_Date DESC
                    LIMIT 1
                """)) {

            pstmt.setInt(1, ID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return getFromRS(rs);
                }

                return null;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getMostRecentFromID()` in `ReadSupplyRecord`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }


    /**
     * Gets the current count of a supply given some date and supply type id. More on this on the database wiki
     *
     * @param conn         the JDBC connection
     * @param supplyTypeID the supply type id
     * @param currentDate  the current date
     * @return
     */
    public static BigDecimal getCurrentCountForDate(Connection conn, int supplyTypeID, Date currentDate) {
        if (ReadSupplyType.getSupplyTypeById(conn, supplyTypeID) == null) {
            generateErrorMessage(
                    "Error in `getCurrentCountForDate()` in `ReadSupplyRecord`.",
                    "Supply type ID does not exist.",
                    "Verify that the ID provided exists",
                    null
            );

            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT Current_Count
                FROM Supply_Record
                WHERE Supply_Type_ID = ? AND SR_Date = ?
                """)) {

            pstmt.setInt(1, supplyTypeID);
            pstmt.setDate(2, currentDate);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("Current_Count").setScale(4, RoundingMode.DOWN);
            }

            return null;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getCurrentCountForDate()` in `ReadSupplyRecord`.",
                    "SQL Exception occurred",
                    "",
                    e
            );

            return null;
        }
    }
}
