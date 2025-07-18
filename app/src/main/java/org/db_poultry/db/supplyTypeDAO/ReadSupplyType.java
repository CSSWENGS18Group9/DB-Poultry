package org.db_poultry.db.supplyTypeDAO;

import org.db_poultry.pojo.SupplyPOJO.SupplyType;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadSupplyType {

    /**
     * Gets all the supply types in the database
     *
     * @param conn the JDBC connection
     * @return an arraylist containing all the supply types
     * <p>
     * To be used by the UI team for their user views. Not to be used outside of this. This is a costly function!
     * </p>
     */
    public static ArrayList<SupplyType> getAllSupplyTypes(Connection conn) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT supply_type_id, supply_name, unit, image_file_path FROM Supply_Type
                """)) {

            return getSupplyTypeList(pstmt);
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getAllSupplyTypes()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Gets all supply types in database, sorted by alphabetical order
     *
     * @param conn the JDBC connection
     * @return a SORTED array list of all the supply types in alpha order
     * <p>
     * To be used by the UI team for their user views. Not to be used outside of this. This is a costly function!
     * </p>
     */
    public static ArrayList<SupplyType> getSupplyTypeAscending(Connection conn) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT st.supply_type_id, st.supply_name, st.unit, st.image_file_path
                    FROM Supply_Type st
                    ORDER BY st.supply_name ASC
                """)) {
            ArrayList<SupplyType> supplyTypes = getSupplyTypeList(pstmt);
            if (supplyTypes == null) {
                return null;
            }

            return supplyTypes.isEmpty() ? null : supplyTypes;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyTypeAscending()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );
        }
        return null;
    }


    /**
     * Gets all supply types in database, sorted by reverse alphabetical order
     *
     * @param conn the JDBC connection
     * @return a SORTED array list of all the supply types in alpha order
     * <p>
     * To be used by the UI team for their user views. Not to be used outside of this. This is a costly function!
     * </p>
     */
    public static ArrayList<SupplyType> getSupplyTypeDescending(Connection conn) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT st.supply_type_id, st.supply_name, st.unit, st.image_file_path
                    FROM Supply_Type st
                    ORDER BY st.supply_name DESC
                """)) {
            ArrayList<SupplyType> supplyTypes = getSupplyTypeList(pstmt);
            if (supplyTypes == null) {
                return null;
            }

            return supplyTypes.isEmpty() ? null : supplyTypes;

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyTypeDescending()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );
        }

        return null;
    }

    /**
     * Gets all supply types in database, sorted by when they are last updated/used
     *
     * @param conn the JDBC connection
     * @return a SORTED array list of all the supply types in alpha order
     * <p>
     * To be used by the UI team for their user views. Not to be used outside of this. This is a costly function!
     * </p>
     */
    public static ArrayList<SupplyType> getSupplyTypeByLastUpdate(Connection conn) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT st.supply_type_id, st.supply_name, st.unit, st.image_file_path, MAX(sr.sr_date) AS latest_date
                    FROM Supply_Type st
                    LEFT JOIN Supply_Record sr ON st.supply_type_id = sr.supply_type_id
                    GROUP BY st.supply_type_id, st.supply_name, st.unit, st.image_file_path
                    ORDER BY
                        CASE WHEN MAX(sr.sr_date) IS NULL THEN 1 ELSE 0 END, 
                        MAX(sr.sr_date) DESC
                """)) {
            ArrayList<SupplyType> supplyTypes = getSupplyTypeList(pstmt);

            if (supplyTypes == null) {
                return null;
            }

            return supplyTypes.isEmpty() ? null : supplyTypes;

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyTypeByLastUpdate()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );
        }

        return null;
    }

    /**
     * Gets a supply type by the name, used for uniqueness validation
     *
     * @param conn the JDBC connection
     * @param name the name of the
     * @return the supply type POJO (if it exists), {null} otherwise
     */
    public static SupplyType getSupplyTypeByName(Connection conn, String name) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT supply_type_id, supply_name, unit, image_file_path FROM Supply_Type WHERE supply_name = ?"""
        )) {

            pstmt.setString(1, name);

            return getSupplyType(pstmt);
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyType()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;

        }
    }

    /**
     * Gets a supply type by its id.
     *
     * @param conn the JDBC connection
     * @param id   the id of the supply
     * @return the Supply Type object (if it exists), {null} otherwise
     */
    public static SupplyType getSupplyTypeById(Connection conn, int id) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT supply_type_id, supply_name, unit, image_file_path FROM Supply_Type 
                WHERE supply_type_id = ?"""
        )) {

            pstmt.setInt(1, id);

            return getSupplyType(pstmt);
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyTypeByID()` in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Gets the supply type object given some prepared statement. Not to be used outside of scope.
     *
     * @param pstmt the prepared statement
     * @return the supply type object, {null} if it does not exist
     */
    @Nullable
    private static SupplyType getSupplyType(PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int supplyTypeId = rs.getInt("supply_type_id");
                String supplyTypeName = rs.getString("supply_name");
                String unit = rs.getString("unit");
                String imageFilePath = rs.getString("image_file_path");
                return new SupplyType(supplyTypeId, supplyTypeName, unit, imageFilePath);
            }

            return null;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyType() in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Gets LIST of supply type object given some prepared statement. Not to be used outside of scope.
     *
     * @param pstmt the prepared statement
     * @return the supply type object, {null} if it does not exist
     */
    private static ArrayList<SupplyType> getSupplyTypeList(PreparedStatement pstmt) {
        try (ResultSet rs = pstmt.executeQuery()) {
            ArrayList<SupplyType> supplyTypes = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt("supply_type_id");
                String name = rs.getString("supply_name");
                String unit = rs.getString("unit");
                String imageFilePath = rs.getString("image_file_path");
                supplyTypes.add(new SupplyType(id, name, unit, imageFilePath));
            }

            return supplyTypes;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getSupplyType() in `ReadSupplyType`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }
}
