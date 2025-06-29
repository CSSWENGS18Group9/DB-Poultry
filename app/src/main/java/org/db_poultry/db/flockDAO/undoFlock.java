package org.db_poultry.db.flockDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class undoFlock {

    public static String undoCreateFlock(Connection connect) {
        try (PreparedStatement preppedStatement = connect.prepareStatement("DELETE FROM Flock ORDER BY flock_id DESC LIMIT 1")) {

            preppedStatement.executeUpdate();

            return "DELETE FROM Flock ORDER BY flock_id DESC LIMIT 1";
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
