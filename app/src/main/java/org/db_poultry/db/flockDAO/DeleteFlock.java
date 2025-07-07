package org.db_poultry.db.flockDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DeleteFlock {

    /**
     * Deletes the latest record added in Flock
     *
     * @param conn       the Connection thing with SQL
     * @return a String which is the query
     */
    public static String undoCreateFlock(Connection conn) {
        try (PreparedStatement preppedStatement = conn.prepareStatement("DELETE FROM Flock WHERE ctid IN (SELECT ctid FROM Flock ORDER BY flock_id DESC LIMIT 1)")) {

            preppedStatement.executeUpdate();

            return "DELETE FROM Flock WHERE ctid IN (SELECT ctid FROM Flock ORDER BY flock_id DESC LIMIT 1)";
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `undoCreateFlock()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }

    }
}
