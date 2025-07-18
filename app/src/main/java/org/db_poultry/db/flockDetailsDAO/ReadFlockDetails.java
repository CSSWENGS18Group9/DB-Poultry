package org.db_poultry.db.flockDetailsDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.db_poultry.db.flockDAO.ReadFlock;
import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;
import org.db_poultry.pojo.FlockPOJO.FlockComplete;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;

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
    public static List<FlockDetails> getFlockDetailsFromDate(Connection conn, Date flockDate, Date fdStartDate,
                                                             Date fdEndDate) {
        if (fdStartDate.after(fdEndDate)) {
            generateErrorMessage(
                    "Error in `getFlockDetailsFromDate()`.",
                    "End date happens before start date.",
                    "",
                    null
            );

            return null;
        }

        try (PreparedStatement preppedStatement = conn.prepareStatement("""
                SELECT fd.Flock_Details_ID, fd.Flock_ID, fd.FD_Date, fd.Depleted_Count
                FROM Flock_Details AS fd
                LEFT JOIN Flock ON Flock.Flock_ID = fd.Flock_ID
                WHERE (Flock.Starting_Date = ?)
                AND (fd.FD_Date BETWEEN ? AND ?)
                ORDER BY fd.FD_Date
                """)) {

            preppedStatement.setDate(1, flockDate);
            preppedStatement.setDate(2, fdStartDate);
            preppedStatement.setDate(3, fdEndDate);

            try (ResultSet result = preppedStatement.executeQuery()) {
                List<FlockDetails> flockDetails = new ArrayList<>();
                while (result.next()) {
                    // Gets results per row from the SQL output
                    int flockDetailsID = result.getInt("Flock_Details_ID");
                    int flockID = result.getInt("Flock_ID");
                    Date flockDetailsDate = result.getDate("FD_Date");
                    int depletedCount = result.getInt("Depleted_Count");

                    FlockDetails returnedFlockDetails = new FlockDetails(flockDetailsID, flockID, flockDetailsDate,
                            depletedCount); // Creates a flock object to be returned

                    flockDetails.add(returnedFlockDetails);
                }

                // checks if the flockDetails is empty, if it is return null. Otherwise, return the List
                return flockDetails.isEmpty() ? null : flockDetails;
            }
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getFlockDetailsFromDate()`.",
                    "SQLException occurred.",
                    "",
                    e);
            return null;
        }
    }

    /**
     * returns a list of all flock details for a specific flock record
     *
     * @param conn      the JDBC connection
     * @param flockDate the date of the flock. We use this to determine which Flock we're going to use to limit our
     *                  search scope. Recall that only a single FLOCK record can exist per time so the range of FDs of
     *                  a specific flock is given by [Flock.date, Flock.next.date]
     * @return a list of all flock details within that flock record
     */
    public static List<FlockDetails> getFlockDetailsFromFlock(Connection conn, Date flockDate) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT fd.Flock_Details_ID, fd.Flock_ID, fd.FD_Date, fd.Depleted_Count
                FROM Flock_Details fd
                JOIN Flock f ON fd.Flock_ID = f.Flock_ID
                WHERE f.Starting_Date = ?
                """)) {

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

            return flockDetails.isEmpty() ? null : flockDetails;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getFlockDetailsFromFlock()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Gets the most recent Flock Details date for a specific flock
     *
     * @param conn      the JDBC connection
     * @param flockDate the flock date (the selector for the flock)
     * @return a FlockDetails POJO is there exists, null otherwise
     */
    public static FlockDetails getMostRecent(Connection conn, Date flockDate) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT fd.flock_details_id, fd.flock_id, fd.fd_date, fd.depleted_count
                FROM Flock_Details fd
                JOIN Flock f ON fd.flock_id = f.flock_id
                WHERE f.starting_date = ?
                ORDER BY fd.fd_date DESC
                LIMIT 1
                """)) {

            pstmt.setDate(1, flockDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {  // Move cursor to first row if present
                    int flock_details_id = rs.getInt("flock_details_id");
                    int flock_id = rs.getInt("flock_id");
                    Date fd_date = rs.getDate("fd_date");
                    int depleted_count = rs.getInt("depleted_count");

                    return new FlockDetails(flock_details_id, flock_id, fd_date, depleted_count);
                }

                return null; // No matching record found
            }
        } catch (Exception e) {
            generateErrorMessage(
                    "Error in `getMostRecent()` in `ReadFlockDetails`.",
                    "SQL Exception error occurred",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Implements a general search function for the user interface
     *
     * @param conn        The JDBC connection
     * @param searchQuery The search query as a string
     * @return {List<FlockDetails>} if there exists a FlockDetails that matches the search query, null otherwise
     * <p> Notes </p>
     * The search query can be one of the following:
     * - A month (word, abbreviation, numeric): June, 06, 6, Jun return all FDs in the month of June
     * (no matter what year)
     * ---- This also accepts an epsilon value. If input was June and there is none, we can show all FDs in June - 1
     * (or May) and June + 1 (or July)
     * - A year: 2025 returns all FDs in the year 2025.'
     * - Specific date, in the following formats:
     * ---- MM-DD-YYYY (hyphens are optional; e.g., 10-01-2004)
     * ----- Month-Day-Year
     * --------- hyphens are optional; e.g., October 1, 2004 OR Oct 1, 2004
     */
    public static List<FlockDetails> searchFlockDetails(Connection conn, String searchQuery) {
        if (conn == null || searchQuery == null || searchQuery.isBlank()) return null;

        String query = searchQuery.trim().toUpperCase();

        // Year only (e.g., "2025")
        if (query.matches("\\d{4}")) {
            return fetchByYear(conn, Integer.parseInt(query));
        }

        // Full date (e.g., "10-01-2004", "October 1, 2004", "Oct 1, 2004")
        if (query.matches("([A-Z]+|\\d{2})( )*(-)?( )*\\d{1,2}( )*(-|,)?( )*\\d{4}")) {
            return fetchByFullDate(conn, query);
        }

        // Month + Year (e.g., "JUNE 2025", "6 2025")
        if (query.matches("([A-Z]+|\\d{1,2})*-?\\s+\\d{4}")) {
            String[] parts = query.split("\\s+");
            int month = monthToInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            return fetchByMonthYearWithFallback(conn, month, year);
        }

        // Month only (fallback logic inside)
        if (query.matches("^[A-Z]{3,9}$|^\\d{1,2}$")) {
            return fetchByMonthWithFallback(conn, query);
        }

        return null;
    }

    // Extracted method for fetching by year
    private static List<FlockDetails> fetchByYear(Connection conn, int year) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT flock_details_id, flock_id, fd_date, depleted_count
                FROM Flock_Details
                WHERE EXTRACT(YEAR FROM fd_date) = ?;
                """)) {
            pstmt.setInt(1, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                return extractSearchResults(rs);
            }

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `fetchByYear()` in `ReadFlockDetails.java`",
                    "SQLException occurred.",
                    "",
                    e
            );
            return null;
        }
    }

    // Extracted method for fetching by full date
    private static List<FlockDetails> fetchByFullDate(Connection conn, String query) {
        try {
            // clean and remove the delimiters in the string
            String cleaned = query.replaceAll("[,\\-]", " ").trim();
            String[] parts = cleaned.split("\\s+");

            // split string tokens and validate the tokens
            String monthStr = parts[0];
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            if (day <= 0 || day > 31 || year < 2000) return null;

            int month = monthToInt(monthStr);
            if (month == -1) return null;

            // convert str to Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date parsedDate = sdf.parse(day + "-" + month + "-" + year);
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

            try (PreparedStatement pstmt = conn.prepareStatement("""
                    SELECT flock_details_id, flock_id, fd_date, depleted_count
                    FROM Flock_Details
                    WHERE fd_date = ?
                    ORDER BY fd_date;
                    """)) {
                pstmt.setDate(1, sqlDate);

                try (ResultSet rs = pstmt.executeQuery()) {
                    return extractSearchResults(rs);
                }
            }

        } catch (Exception e) {
            generateErrorMessage(
                    "Error in `fetchByFullDate()` in `ReadFlockDetails.java`",
                    "Exception occurred while parsing or querying date.",
                    "",
                    e
            );
            return null;
        }
    }

    // Helper to convert month string to int
    private static int monthToInt(String monthStr) {
        return switch (monthStr) {
            case "JANUARY", "JAN", "1" -> 1;
            case "FEBRUARY", "FEB", "2" -> 2;
            case "MARCH", "MAR", "3" -> 3;
            case "APRIL", "APR", "4" -> 4;
            case "MAY", "5" -> 5;
            case "JUNE", "JUN", "6" -> 6;
            case "JULY", "JUL", "7" -> 7;
            case "AUGUST", "AUG", "8" -> 8;
            case "SEPTEMBER", "SEP", "9" -> 9;
            case "OCTOBER", "OCT", "10" -> 10;
            case "NOVEMBER", "NOV", "11" -> 11;
            case "DECEMBER", "DEC", "12" -> 12;
            default -> -1;
        };
    }

    private static List<FlockDetails> fetchByMonthWithFallback(Connection conn, String query) {
        int month = monthToInt(query);
        if (month == -1) return null;

        List<FlockDetails> results = fetchByMonth(conn, month);
        if (results != null) return results;

        // Try previous month
        int prevMonth = (month == 1) ? 12 : month - 1;
        results = fetchByMonth(conn, prevMonth);
        if (results != null) return results;

        // Try next month
        int nextMonth = (month == 12) ? 1 : month + 1;
        return fetchByMonth(conn, nextMonth);
    }

    private static List<FlockDetails> fetchByMonth(Connection conn, int month) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
            SELECT flock_details_id, flock_id, fd_date, depleted_count
            FROM Flock_Details
            WHERE EXTRACT(MONTH FROM fd_date) = ?
            ORDER BY fd_date;
        """)) {

            pstmt.setInt(1, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                return extractSearchResults(rs);
            }

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `fetchByMonth()` in `ReadFlockDetails.java`",
                    "SQLException occurred.",
                    "",
                    e
            );
            return null;
        }
    }

    private static List<FlockDetails> fetchByMonthYearWithFallback(Connection conn, int month, int year) {
        if (month <= 0 || month > 12 || year < 2000) return null;

        List<FlockDetails> results = fetchByMonthYear(conn, month, year);
        if (results != null) return results;

        // Fallback: Previous month
        int prevMonth = (month == 1) ? 12 : month - 1;
        results = fetchByMonthYear(conn, prevMonth, year);
        if (results != null) return results;

        // Fallback: Next month
        int nextMonth = (month == 12) ? 1 : month + 1;
        return fetchByMonthYear(conn, nextMonth, year);
    }

    private static List<FlockDetails> fetchByMonthYear(Connection conn, int month, int year) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
            SELECT flock_details_id, flock_id, fd_date, depleted_count
            FROM Flock_Details
            WHERE EXTRACT(MONTH FROM fd_date) = ?
              AND EXTRACT(YEAR FROM fd_date) = ?
            ORDER BY fd_date;
        """)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                return extractSearchResults(rs);
            }

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `fetchByMonthYear()` in `ReadFlockDetails.java`",
                    "SQLException occurred.",
                    "",
                    e
            );
            return null;
        }
    }

    private static List<FlockDetails> extractSearchResults(ResultSet rs) throws SQLException {
        List<FlockDetails> result = new ArrayList<>();
        while (rs.next()) {
            result.add(new FlockDetails(
                    rs.getInt("flock_details_id"),
                    rs.getInt("flock_id"),
                    rs.getDate("fd_date"),
                    rs.getInt("depleted_count")
            ));
        }
        return result.isEmpty() ? null : result;
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

        try (PreparedStatement preppedStatement = connect.prepareStatement("""
                SELECT SUM(Depleted_Count) AS Total_Count_Depleted
                FROM Flock_Details
                WHERE Flock_ID = ?
                """)) {

            preppedStatement.setInt(1, flockID);

            int sum = 0; // Gets total depleted count
            try (ResultSet result = preppedStatement.executeQuery()) {
                while (result.next()) sum = result.getInt("Total_Count_Depleted");
                if (flockStartingCount < sum) return -1;
            }
            return sum; // Returns the total depleted count
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `cumulativeDepletedCount()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return -1;
        }
    }

    public static int getCumulativeDepletedCountUpToDate(Connection connect, int flockID, Date targetDate) {
        HashMap<Integer, FlockComplete> flocks = ReadFlock.allByID(connect);
        FlockComplete flockChosen = flocks.get(flockID);
        int flockStartingCount = flockChosen.getFlock().getStartingCount();

        try (PreparedStatement preppedStatement = connect.prepareStatement("""
                
                    SELECT SUM(Depleted_Count) AS Total_Count_Depleted FROM Flock_Details 
                WHERE Flock_ID = ? AND FD_Date < ?
                """)) {
            preppedStatement.setInt(1, flockID);
            preppedStatement.setDate(2, targetDate);

            int sum = 0; // Gets total depleted count
            try (ResultSet result = preppedStatement.executeQuery()) {
                while (result.next()) sum = result.getInt("Total_Count_Depleted");
                if (flockStartingCount < sum) return -1;
            }
            return sum; // Returns the total depleted count

        } catch (SQLException e) {
            generateErrorMessage("Error in `cumulativeDepletedCount()`.",
                    "SQLException occurred.",
                    "",
                    e);
            return -1;
        }

    }

}
