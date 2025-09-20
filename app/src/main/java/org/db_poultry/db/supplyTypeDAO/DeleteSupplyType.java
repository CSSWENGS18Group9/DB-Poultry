package org.db_poultry.db.supplyTypeDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DeleteSupplyType {

    /**
     * Deletes the latest supply type added in Supply_Type
     *
     * @param conn       the Connection thing with SQL
     * @return a String which is the query
     */
    public static String undoCreateSupplyType(Connection conn) {
        try (PreparedStatement preppedStatement = conn.prepareStatement("DELETE FROM Supply_Type WHERE ctid IN (SELECT ctid FROM Supply_Type ORDER BY Supply_Type_ID DESC LIMIT 1)")) {

            int rows = preppedStatement.executeUpdate();

            if (rows == 0) {
                return null;
            }

            return "DELETE FROM Supply_Type ORDER BY Supply_Type_ID DESC LIMIT 1";
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `undoCreateSupplyType()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }

    }
}