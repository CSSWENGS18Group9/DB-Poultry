package org.db_poultry.db.supplyRecordDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class CreateSupplyRecord {

    public static String createSupplyRecord(Connection connect, int supplyType, Date srDate, float added, float consumed) {
        // TODO: Add checker later on if the SupplyType DOES exist
        // ---
        try (PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (?, ?, ?, ?, ?)")) {
            BigDecimal addedDecimal = BigDecimal.valueOf(added);
            BigDecimal consumedDecimal = BigDecimal.valueOf(consumed);

            preparedStatement.setInt(1, supplyType);
            preparedStatement.setDate(2, srDate);
            preparedStatement.setBigDecimal(3, addedDecimal);
            preparedStatement.setBigDecimal(4, consumedDecimal);
            preparedStatement.setBoolean(5, false); // always assumes Retrieved is False at creation

            preparedStatement.executeUpdate();
            return String.format("INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (%d, '%s', %f, %f, %b);", supplyType, srDate, added, consumed, false);
        } catch (SQLException e) {
            generateErrorMessage("Error in `createSupplyRecord()` in `createSupplyRecord.", "SQL Exception error occurred", "", e);
            return null;
        }
    }
}
