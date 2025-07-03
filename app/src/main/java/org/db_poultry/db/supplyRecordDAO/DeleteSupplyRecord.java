package org.db_poultry.db.supplyRecordDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DeleteSupplyRecord {

    public static String undoCreateSupplyRecord(Connection conn) {
        try (PreparedStatement preppedStatement = conn.prepareStatement("DELETE FROM Supply_Record ORDER BY Supply_ID DESC LIMIT 1")) {

            preppedStatement.executeUpdate();

            return "DELETE FROM Supply_Record ORDER BY Supply_ID DESC LIMIT 1";
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `undoCreateSupplyRecord()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }

    }
}
