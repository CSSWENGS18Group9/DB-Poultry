package org.db_poultry.db.flockDetailsDAO;

import org.db_poultry.db.flockDAO.ReadFlock;
import org.db_poultry.pojo.FlockPOJO.FlockComplete;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadFlockDetails {
    /**
     * Returns a list of a flock's flock details between two dates
     *
     * @param conn        the sql connection
     * @param flockDate   the starting date of a flock
     * @param fdStartDate the start date of range (flock details)
     * @param fdEndDate   the end date of range (flock details)
     * @return a List of FlockDetails objects
     */
    public static List<FlockDetails> getFlockDetailsFromDate(Connection conn, Date flockDate, Date fdStartDate, Date fdEndDate) {
        if (fdStartDate.after(fdEndDate)) {
            generateErrorMessage("Error in `getFlockDetailsFromDate()`.", "End date happens before start date.", "", null);
            return null;
        }

        try (PreparedStatement preppedStatement = conn.prepareStatement("SELECT * FROM Flock_Details LEFT JOIN Flock ON Flock.Flock_ID = Flock_Details.Flock_ID WHERE (Flock.Starting_Date = ?) AND (Flock_Details.FD_Date BETWEEN ? AND ?) ORDER BY Flock_Details.FD_Date")) {
            preppedStatement.setDate(1, flockDate);
            preppedStatement.setDate(2, fdStartDate);
            preppedStatement.setDate(3, fdEndDate);

            List<FlockDetails> flockDetails = new ArrayList<>();
            try (ResultSet result = preppedStatement.executeQuery()) {
                while (result.next()) {
                    // Gets results per row from the SQL output
                    int flockDetailsID = result.getInt("Flock_Details_ID"); // Gets the output from the column named Flock_Details_ID
                    int flockID = result.getInt("Flock_ID"); // Gets the output from the column named Flock_ID
                    Date flockDetailsDate = result.getDate("FD_Date"); // Gets the output from the column named FD_Date
                    int depletedCount = result.getInt("Depleted_Count"); // Gets the output from the column named Depleted_Count


                    FlockDetails returnedFlockDetails = new FlockDetails(flockDetailsID, flockID, flockDetailsDate, depletedCount); // Creates a flock object to be returned

                    flockDetails.add(returnedFlockDetails);
                }
            }

            return flockDetails;
        } catch (SQLException e) {
            generateErrorMessage("Error in `getFlockDetailsFromDate()`.", "SQLException occurred.", "", e);
            return null;
        }
    }

    public static List<FlockDetails> getFlockDetailsFromFlock(Connection conn, Date flockDate) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT fd.Flock_Details_ID, fd.Flock_ID, fd.FD_Date, fd.Depleted_Count FROM Flock_Details fd JOIN Flock f ON fd.Flock_ID = f.Flock_ID WHERE f.Starting_Date = ?")) {
            pstmt.setDate(1, flockDate);
            ResultSet result = pstmt.executeQuery();
            List<FlockDetails> flockDetails = new ArrayList<>();

            while (result.next()) {
                int flockDetailsID = result.getInt("Flock_Details_ID");
                int flockID = result.getInt("Flock_ID");
                Date flockDetailsDate = result.getDate("FD_Date");
                int depletedCount = result.getInt("Depleted_Count");

                flockDetails.add(new FlockDetails(flockDetailsID, flockID, flockDetailsDate, depletedCount));
            }

            return flockDetails;
        } catch (SQLException e) {
            generateErrorMessage("Error in `getFlockDetailsFromFlock()`.", "SQLException occurred.", "", e);
            return null;
        }
    }

    public static FlockDetails getMostRecent(Connection conn, Date flockDate) {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT fd.flock_details_id, fd.flock_id, fd.fd_date, fd.depleted_count FROM Flock_Details fd JOIN Flock f ON fd.Flock_ID = f.Flock_ID WHERE f.Starting_Date = ? ORDER BY fd.FD_Date DESC LIMIT 1")) {
            pstmt.setDate(1, flockDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  // Move cursor to first row if present
                    int flock_details_id = rs.getInt("flock_details_id");
                    int flock_id = rs.getInt("flock_id");
                    Date fd_date = rs.getDate("fd_date");
                    int depleted_count = rs.getInt("depleted_count");

                    return new FlockDetails(flock_details_id, flock_id, fd_date, depleted_count);
                } else {
                    return null; // No matching record found
                }
            }
        } catch (Exception e) {
            generateErrorMessage("Error in `getMostRecent()` in `ReadFlockDetails`.", "SQL Exception error occurred", "", e);
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
        HashMap<Integer, FlockComplete> flocks = ReadFlock.allByID(connect);
        FlockComplete flockChosen = flocks.get(flockID);
        int flockStartingCount = flockChosen.getFlock().getStartingCount();

        try (PreparedStatement preppedStatement = connect.prepareStatement("SELECT SUM(Depleted_Count) AS Total_Count_Depleted FROM Flock_Details WHERE Flock_ID = ?")) {
            preppedStatement.setInt(1, flockID);

            int sum = 0; // Gets total depleted count
            try (ResultSet result = preppedStatement.executeQuery()) {
                while (result.next()) sum = result.getInt("Total_Count_Depleted");
                if (flockStartingCount < sum) return -1;
            }
            return sum; // Returns the total depleted count

        } catch (SQLException e) {
            generateErrorMessage("Error in `cumulativeDepletedCount()`.", "SQLException occurred.", "", e);
            return -1;
        }

    }

    public static int getCumulativeDepletedCountUpToDate(Connection connect, int flockID, Date targetDate) {
        HashMap<Integer, FlockComplete> flocks = ReadFlock.allByID(connect);
        FlockComplete flockChosen = flocks.get(flockID);
        int flockStartingCount = flockChosen.getFlock().getStartingCount();

        try (PreparedStatement preppedStatement = connect.prepareStatement("SELECT SUM(Depleted_Count) AS Total_Count_Depleted FROM Flock_Details WHERE Flock_ID = ? AND FD_Date < ?")) {
            preppedStatement.setInt(1, flockID);
            preppedStatement.setDate(2, targetDate);

            int sum = 0; // Gets total depleted count
            try (ResultSet result = preppedStatement.executeQuery()) {
                while (result.next()) sum = result.getInt("Total_Count_Depleted");
                if (flockStartingCount < sum) return -1;
            }
            return sum; // Returns the total depleted count

        } catch (SQLException e) {
            generateErrorMessage("Error in `cumulativeDepletedCount()`.", "SQLException occurred.", "", e);
            return -1;
        }

    }
}
