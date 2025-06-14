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
    public static String createSupplyRecord(Connection connect, int supplyTypeID, Date srDate, BigDecimal added,
                                            BigDecimal consumed, boolean retrieved) {

        // verify the supplyType exists
        if (verify_supplyTypeID(connect, supplyTypeID)) {
            generateErrorMessage("Error at `createSupplyRecord()` in `CreateSupplyRecord.", "SupplyType does not " +
                    "exist", "Ensure that the SupplyType ID of the SupplyRecord to be inserted exists", null);
            return null;
        }

        // verify the date is after the date of the most recent supply record
        if (verify_srDateAfterMostRecent(connect, supplyTypeID, srDate)) {
            generateErrorMessage("Error at `createSupplyRecord()` in `CreateSupplyRecord.",
                    "Date of the record to " + "be" + " inserted is after the last inserted record", "Ensure that " +
                            "the date of the SupplyRecord " + "to be " + "inserted is after the last insert", null);
            return null;
        }

        // if retrieved is true set added  to 0.0000 since we don't really care about this value
        // and set the consumed to tbe the total of the added for this range - total of the consumed for this range
        if (retrieved) {
            added = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
            consumed =  ReadSupplyRecord.getCurrentCountForDate(connect, supplyTypeID, srDate);
        } else if (verify_numericValues(added, consumed, supplyTypeID, srDate, connect)) {
            generateErrorMessage("Error at `createSupplyRecord()` in `CreateSupplyRecord`.", "Added and consumed " +
                    "values are invalid.", "Make sure that these values are positive, and consumed does make the " +
                    "currentSupplyNegative.", null);
            return null;
        }

        if (verify_precision(added) || verify_precision(consumed)) {
            generateErrorMessage("Error at `createSupplyRecord()` in CreateSupplyRecord.",
                    "The precision of added " + "and/or consumed is greater than 4.",
                    "Ensure that the precision of " + "added and consumed is at most" + " 4.", null);
            return null;
        }

        try (PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO Supply_Record " +
                "(Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setInt(1, supplyTypeID);
            preparedStatement.setDate(2, srDate);
            preparedStatement.setBigDecimal(3, added);
            preparedStatement.setBigDecimal(4, consumed);
            preparedStatement.setBoolean(5, retrieved);

            preparedStatement.executeUpdate();
            return String.format("INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved)" +
                    " VALUES (%d, '%s', %f, %f, %b);", supplyTypeID, srDate, added, consumed, retrieved);
        } catch (SQLException e) {
            generateErrorMessage("Error in `createSupplyRecord()` in `createSupplyRecord.", "SQL Exception error " +
                    "occurred", "", e);
            return null;
        }
    }

    private static boolean verify_precision(BigDecimal value) {
        return value.stripTrailingZeros().scale() > 4;
    }

    private static boolean verify_supplyTypeID(Connection conn, int supplyTypeID) {
        return ReadSupplyType.getSupplyTypeById(conn, supplyTypeID) == null;
    }

    private static boolean verify_srDateAfterMostRecent(Connection conn, int supplyTypeID, Date srDate) {
        // get the most recent supply record using the date
        SupplyComplete sr = ReadSupplyRecord.getMostRecentFromID(conn, supplyTypeID);

        if (sr == null) {
            return false;
        }

        return srDate.before(sr.getDate());
    }

    private static boolean verify_numericValues(BigDecimal added, BigDecimal consumed, int supplyTypeID, Date srDate,
                                                Connection connection) {
        if (added.compareTo(BigDecimal.ZERO) < 0 || consumed.compareTo(BigDecimal.ZERO) < 0) {
            return true;
        }

        BigDecimal currentCount = ReadSupplyRecord.getCurrentCountForDate(connection, supplyTypeID,
                srDate);

        // Check if consuming more than what's currently available
        return added.add(currentCount).subtract(consumed).compareTo(BigDecimal.ZERO) < 0;
    }

}
