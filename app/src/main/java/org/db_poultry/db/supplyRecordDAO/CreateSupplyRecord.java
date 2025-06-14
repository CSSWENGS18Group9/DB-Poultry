package org.db_poultry.db.supplyRecordDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyRecord {
    public static String createSupplyRecord(Connection connect, int supplyType, Date srDate, float added,
                                            float consumed, boolean retrieved) {
        if (verify_precision(added) || verify_precision(consumed)) {
            generateErrorMessage("Error at `createSupplyRecord()` in CreateSupplyRecord.", "The precision of added " +
                    "and/or consumed is greater than 4.", "Ensure that the precision of added and consumed is at most" +
                    " 4.", null);
            return null;
        }


        try (PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO Supply_Record " +
                "(Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (?, ?, ?, ?, ?)")) {
            BigDecimal addedDecimal = BigDecimal.valueOf(added);
            BigDecimal consumedDecimal = BigDecimal.valueOf(consumed);

            preparedStatement.setInt(1, supplyType);
            preparedStatement.setDate(2, srDate);
            preparedStatement.setBigDecimal(3, addedDecimal);
            preparedStatement.setBigDecimal(4, consumedDecimal);
            preparedStatement.setBoolean(5, retrieved);

            preparedStatement.executeUpdate();
            return String.format("INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved)" +
                    " " +
                    "VALUES (%d, '%s', %f, %f, %b);", supplyType, srDate, added, consumed, retrieved);
        } catch (SQLException e) {
            generateErrorMessage("Error in `createSupplyRecord()` in `createSupplyRecord.", "SQL Exception error " +
                    "occurred", "", e);
            return null;
        }
    }


    private static boolean verify_precision(float value) {
        BigDecimal lf = new BigDecimal(value);
        lf = lf.stripTrailingZeros();

        // check if the number of decimal places is at most 4
        return lf.scale() > 4;
    }
}
