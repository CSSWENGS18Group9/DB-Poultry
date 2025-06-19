package org.db_poultry.db.supplyRecordDAO;

import org.db_poultry.db.supplyTypeDAO.ReadSupplyType;
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyRecord {
    /**
     * Created a supply record in the database and does the data validation before insertion
     *
     * @param connect      the JDBC connection
     * @param supplyTypeID the id of the supply type of this record
     * @param srDate       the date of the supply record (SR)
     * @param added        the number of the supply that was added (a numerical Value of 12 precision, 4 of which are
     *                     decimal places)
     * @param consumed     the number of the supply that was consumed
     * @param retrieved    the retrieved boolean
     * @return {String} if the SQL query was successful, returns the SQL query that was executed. {null} for any
     * other case.
     * <p>
     * MORE ON THE RETRIEVED BOOLEAN:
     * This denotes if the supplies was given back to the other company the client collaborates with. So there's this
     * grey area of consumed and retrieved. Essentially, this is *usually* false and only true if the supplies were
     * returned to the partner company. During this case, the added is 0, while the consumed will be the total
     * remaining supplies (sum of all added - sum of all consumed).
     * <p>
     * This value will be the consumed during a retrieved date.
     * <p>
     * For how insertion works for this data table, see the database wiki page on our github.
     * {@code @zrygan}
     */
    public static String createSupplyRecord(Connection connect, int supplyTypeID, Date srDate, BigDecimal added,
                                            BigDecimal consumed, boolean retrieved) {

        // verify the supplyType exists
        if (verify_supplyTypeID(connect, supplyTypeID)) {
            generateErrorMessage(
                    "Error at `createSupplyRecord()` in `CreateSupplyRecord`.",
                    "SupplyType does not exist",
                    "Ensure that the SupplyType ID of the SupplyRecord to be inserted exists",
                    null
            );

            return null;

        }

        // verify the date is after the date of the most recent supply record
        if (verify_srDateAfterMostRecent(connect, supplyTypeID, srDate)) {
            generateErrorMessage(
                    "Error at `createSupplyRecord()` in `CreateSupplyRecord`.",
                    "Date of the record to be inserted is after the last inserted record",
                    "Ensure that the date of the SupplyRecord to be inserted is after the last insert",
                    null
            );

            return null;
        }

        // if retrieved is true set added  to 0.0000 since we don't really care about this value
        // and set the consumed to tbe the total of the added for this range - total of the consumed for this range
        if (retrieved) {
            added = BigDecimal.ZERO.setScale(4, RoundingMode.DOWN);
            consumed = ReadSupplyRecord.getCurrentCountForDate(connect, supplyTypeID, srDate);

            if (consumed == null) {
                generateErrorMessage(
                        "Error at `createSupplyRecord()` in `CreateSupplyRecord`.",
                        "The consumed value became `null` after calling `getCurrentCountForDate()`.",
                        "Check the logic of `getCurrentCountForDate()`.",
                        null
                );

                return null;
            }
        } else if (verify_numericValues(added, consumed, supplyTypeID, srDate, connect)) {
            // if retrieved is false and the numeric values are INVALID

            generateErrorMessage(
                    "Error at `createSupplyRecord()` in `CreateSupplyRecord`.",
                    "Added and consumed values are invalid.",
                    "Make sure that these values are positive, and consumed does make the currentSupplyNegative.",
                    null
            );

            return null;
        }

        // check the precision, recall that we have 12 total numerical positions, 4 of which are reserved for
        // floating-point values. So (12-4) non-decimal digits, and 4 decimal digits
        // check if this is the case, if at least one is invalid stop
        if (verify_precision(added) || verify_precision(consumed)) {
            generateErrorMessage(
                    "Error at `createSupplyRecord()` in CreateSupplyRecord.",
                    "The precision of added and/or consumed is greater than 4.",
                    "Ensure that the precision of added and consumed is at most 4.",
                    null
            );

            return null;
        }

        // if all tests pass then run the query
        try (PreparedStatement preparedStatement = connect.prepareStatement("""
                INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (?, ?, ?, ?, ?)
                """)) {

            preparedStatement.setInt(1, supplyTypeID);
            preparedStatement.setDate(2, srDate);
            preparedStatement.setBigDecimal(3, added);
            preparedStatement.setBigDecimal(4, consumed);
            preparedStatement.setBoolean(5, retrieved);

            preparedStatement.executeUpdate();
            return String.format(
                            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES " +
                            "(%d, '%s', %.4f, %.4f, %b)",
                    supplyTypeID, srDate, added, consumed, retrieved);
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `createSupplyRecord()` in `createSupplyRecord.",
                    "SQL Exception error occurred",
                    "",
                    e);
            return null;
        }
    }

    /**
     * Validation for the precision of the values, checks if there is MORE 4 decimal places. So, it checks if it's
     * incorrect. Check if there's 4 digits pastthe decimal point.
     *
     * @param value the numeric value (added or consumed)
     * @return {true} if it is wrong, {false} otherwise
     */
    private static boolean verify_precision(BigDecimal value) {
        return value.stripTrailingZeros().scale() > 4;
    }

    /**
     * Validate the supply type does exist.
     * This returns TRUE if it does not exist, FALSE otherwise.
     *
     * @param conn         the JDBC connection
     * @param supplyTypeID the supply type to check
     * @return {true} if it does not exist, {false} otherwise
     */
    private static boolean verify_supplyTypeID(Connection conn, int supplyTypeID) {
        return ReadSupplyType.getSupplyTypeById(conn, supplyTypeID) == null;
    }

    /**
     * Checks if the date of the SR to be inserted is BEFORE the MOST RECENT SR for that Supply Type. Since we want
     * dates that are only after (it checks for the wrong case)
     *
     * @param conn         the JDBC connection
     * @param supplyTypeID the supply type of the sr
     * @param srDate       the date of the sr
     * @return {true} if the most recent is before the srDate, {false} otherwise
     */
    private static boolean verify_srDateAfterMostRecent(Connection conn, int supplyTypeID, Date srDate) {
        // get the most recent supply record using the date
        SupplyComplete sr = ReadSupplyRecord.getMostRecentFromID(conn, supplyTypeID);

        // if the SR is null then return
        if (sr == null) {
            return false;
        }

        return srDate.before(sr.getDate());
    }

    /**
     * Checks if the numerical values are invalid:
     * - Negative
     * - Consumed DOES NOT make sense; that is, it doesn't make the currentCount go negative
     *
     * @param added        the number of added supplies
     * @param consumed     the number of consumed supplied
     * @param supplyTypeID the supply id of the sr
     * @param srDate       the date of the sr
     * @param connection   the JDBC connection
     * @return {true} if the values are invalid, {false} otherwise
     */
    private static boolean verify_numericValues(BigDecimal added, BigDecimal consumed, int supplyTypeID, Date srDate,
                                                Connection connection) {
        // check for negative
        if (added.compareTo(BigDecimal.ZERO) < 0 || consumed.compareTo(BigDecimal.ZERO) < 0) {
            return true;
        }

        BigDecimal currentCount = ReadSupplyRecord.getCurrentCountForDate(connection, supplyTypeID, srDate);

        // Check if consuming more than what's currently available
        return added.add(currentCount).subtract(consumed).compareTo(BigDecimal.ZERO) < 0;
    }

}
