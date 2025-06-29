package org.db_poultry.db.flockDetailsDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DeleteFlockDetail {

    public static String undoCreateFlockDetail(Connection connect, int flockId, Date detailDate) {
        try (PreparedStatement preppedStatement = connect.prepareStatement("DELETE FROM Flock_Details WHERE Flock_ID = ? AND FD_Date = ?")) {

            preppedStatement.setInt(1, flockId);
            preppedStatement.setDate(2, detailDate);
            preppedStatement.executeUpdate();

            return String.format("DELETE FROM Flock_Details WHERE Flock_ID = %d AND FD_Date = %s", flockId, detailDate.toString());
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `undoCreateFlockDetail()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }

    }
}
