package org.db_poultry.db.supplyRecordDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class createSupplyRecord {

    public static String createSupplyRecord(Connection connect, int supplyType, Date srDate, float added,
                                            float consumed) {
        // TODO: Add checker later on if the SupplyType DOES exist
        // ---

        String query = "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (?, ?, ?, ?, ?)";
        String filledQuery = String.format("INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Retrieved) VALUES (%d, '%s', %.2f, %.2f, %b);",
                supplyType, srDate, added, consumed, false);

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(query);

            BigDecimal addedDecimal = BigDecimal.valueOf(added);
            BigDecimal consumedDecimal = BigDecimal.valueOf(consumed);

            preparedStatement.setInt(1, supplyType);
            preparedStatement.setDate(2, srDate);
            preparedStatement.setBigDecimal(3, addedDecimal);
            preparedStatement.setBigDecimal(4, consumedDecimal);
            preparedStatement.setBoolean(5, false); // always assumes Retrieved is False at creation

            preparedStatement.executeUpdate();

            preparedStatement.close();
            return filledQuery;
        } catch (SQLException e) {
            generateErrorMessage("Error in `createSupplyRecord()` in `createSupplyRecord.",
                    "SQL Exception error occured",
                    "",
                    e);

            return null;
        }
    }
}
