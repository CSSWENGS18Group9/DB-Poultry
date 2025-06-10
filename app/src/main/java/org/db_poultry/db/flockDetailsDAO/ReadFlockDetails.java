package org.db_poultry.db.flockDetailsDAO;

import org.db_poultry.db.flockDAO.ReadFlock;
import org.db_poultry.pojo.FlockPOJO.FlockComplete;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;

import java.sql.*;
import java.util.HashMap;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadFlockDetails {

    public static FlockDetails getMostRecent(Connection conn, Date flockDate) {
        String sql = "SELECT fd.flock_details_id, fd.flock_id, fd.fd_date, fd.depleted_count " +
                "FROM Flock_Details fd JOIN Flock f ON fd.Flock_ID = f.Flock_ID " +
                "WHERE f.Starting_Date = ? ORDER BY fd.FD_Date DESC LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, flockDate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  // Move cursor to first row if present
                    int flock_details_id = rs.getInt("flock_details_id");
                    int flock_id = rs.getInt("flock_id");
                    Date fd_date = rs.getDate("fd_date");
                    int depleted_count = rs.getInt("depleted_count");

                    return new FlockDetails(flock_details_id, flock_id, fd_date, depleted_count);
                } else {
                    // No matching record found
                    return null;
                }
            }
        } catch (Exception e) {
            generateErrorMessage("Error in `getMostRecent()` in `ReadFlockDetails`.",
                    "SQL Exception error occurred",
                    "",
                    e);
            return null;
        }
    }

    /**
     * Returns the total depleted count from a specified Flock ID
     *
     * @param connect the Connection thing with SQL
     * @param flockID the flock ID this poultry is connected to
     * @return an Int which is the total depleted count
     */
    public static int getCumulativeDepletedCount(Connection connect, int flockID) {
        String incompleteQuery = "SELECT SUM(Depleted_Count) AS Total_Count_Depleted FROM Flock_Details WHERE Flock_ID = ?"; // Query to be used in preparedStatement

        HashMap<Integer, FlockComplete> flocks = ReadFlock.allByID(connect);
        FlockComplete flockChosen = flocks.get(flockID);
        int flockStartingCount = flockChosen.getFlock().getStartingCount();

        try {
            PreparedStatement preppedStatement = connect.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff



            // Sets the values to be added
            preppedStatement.setInt(1, flockID);

            ResultSet result = preppedStatement.executeQuery(); // Executes query and stores it into a ResultSet

            int sum = 0; // Gets total depleted count

            while (result.next()) { // Gets results per row from the SQL output
                sum = result.getInt("Total_Count_Depleted"); // Gets the output from the column I named Total_Count_Depleted
            }

            result.close(); // CLoses result
            preppedStatement.close(); // Closes preparedStatement

            if(flockStartingCount < sum) {
                return -1;
            }

            return sum; // Returns the total depleted count

        } catch (SQLException e) {
            generateErrorMessage("Error in `cumulativeDepletedCount()`.", "SQLException occurred.", "", e);
            return -1;
        }

    }
}
