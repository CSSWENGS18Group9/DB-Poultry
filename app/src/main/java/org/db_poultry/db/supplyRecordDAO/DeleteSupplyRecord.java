package org.db_poultry.db.supplyRecordDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DeleteSupplyRecord {

    /**
     * Deletes the latest supply record added in Supply_Record
     *
     * @param conn       the Connection thing with SQL
     * @return a String which is the query
     */
    public static String undoCreateSupplyRecord(Connection conn) {
        try (PreparedStatement preppedStatement = conn.prepareStatement("DELETE FROM Supply_Record WHERE ctid IN (SELECT ctid FROM Supply_Record ORDER BY Supply_ID DESC LIMIT 1)")) {

            int rows = preppedStatement.executeUpdate();

            if (rows == 0) {
                return null;
            }

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
