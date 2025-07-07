package org.db_poultry.db.flockDetailsDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import org.db_poultry.util.flockDetailsSingleton;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class DeleteFlockDetail {

    /**
     * Deletes the specified Detail from a Flock (the latest one user made)
     *
     * @param conn          the Connection thing with SQL
     * @param flockID       ID of the flock containing the detail
     * @param detailDate    date of the Detail to delete
     * @return a String which is the query with filled-in values
     */
    public static String undoCreateFlockDetail(Connection conn, int flockID, Date detailDate) {
        try (PreparedStatement preppedStatement = conn.prepareStatement("DELETE FROM Flock_Details WHERE Flock_ID = ? AND FD_Date = ?")) {

            preppedStatement.setInt(1, flockID);
            preppedStatement.setDate(2, detailDate);
            preppedStatement.executeUpdate();

            return String.format("DELETE FROM Flock_Details WHERE Flock_ID = %d AND FD_Date = %s", flockID, detailDate.toString());
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

    /**
     * Sets the Flock ID and the Detail date to store into the singleton (this will be set every time the user creates a detail,
     * making the singleton attributes contain the latest ones the user made)
     *
     * @param flockID       ID of the flock containing the detail
     * @param detailDate    date of the Detail to delete
     */
    public static void setFlockDetailsToDelete(int flockID, Date detailDate) {
        flockDetailsSingleton.INSTANCE.setId(flockID);
        flockDetailsSingleton.INSTANCE.setDate(detailDate);
    }

    /**
     * Gets the Flock ID and the Detail date from the singleton, then passes the values into undoCreateFlockDetail()
     *
     * @param conn          the Connection thing with SQL
     * @return a String which is the query with filled-in values
     */
    public static String getFlockDetailsToDelete(Connection conn) {
        int flockID = flockDetailsSingleton.INSTANCE.getId();
        Date detailDate = flockDetailsSingleton.INSTANCE.getDate();

        return undoCreateFlockDetail(conn, flockID, detailDate);
    }

}
