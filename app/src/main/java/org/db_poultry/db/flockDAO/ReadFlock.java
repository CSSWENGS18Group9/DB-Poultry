package org.db_poultry.db.flockDAO;

import org.db_poultry.pojo.FlockPOJO.Flock;
import org.db_poultry.pojo.FlockPOJO.FlockComplete;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class ReadFlock {
    /**
     * A query that gets all the information and all records in the Flock table. Used in allByID and allByDate.
     *
     * @param con       the sql connection
     * @param keyMapper function that extracts a key from the FlockDetails pojo and uses it as the key of the hash map
     * @param <K>       the generic; the type of the key in the hash map. See allByID and allByDate to see what <K>
     *           refers to
     * @return hashmap of the Flock and FlockDetails table
     */
    private static <K> HashMap<K, FlockComplete> queryAll(Connection con, Function<Flock, K> keyMapper) {
        HashMap<K, FlockComplete> result = new HashMap<>();

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("""
                SELECT f.flock_id, f.starting_count, f.starting_date,
                fd.flock_details_id, fd.fd_date, fd.depleted_count
                FROM Flock f LEFT JOIN Flock_Details fd ON f.flock_id = fd.flock_id
                """.stripIndent())) {
            while (rs.next()) {
                // Flock columns
                int flockId = rs.getInt("flock_id");
                int startingCount = rs.getInt("starting_count");
                Date startingDate = rs.getDate("starting_date");

                // Flock Details columns
                int fdId = rs.getInt("flock_details_id");
                Date fdDate = rs.getDate("fd_date");
                int depletedCount = rs.getInt("depleted_count");

                Flock flock = new Flock(flockId, startingCount, startingDate);
                FlockDetails detail = new FlockDetails(fdId, flockId, fdDate, depletedCount);
                K key = keyMapper.apply(flock);

                result.computeIfAbsent(key, k -> new FlockComplete(flock)).addFlockDetail(detail);
            }

        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `queryFlocks()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }

        return result;
    }

    /**
     * Uses the generic function above to get all table entries and relates it by ID
     *
     * @param con the sql connection
     * @return the hashmap where flockID is the key of each entry in the hash map
     */
    public static HashMap<Integer, FlockComplete> allByID(Connection con) {
        return queryAll(con, Flock::getFlockId);
    }

    /**
     * Uses the generic function above to get all table entries and relates it by Date
     *
     * @param con the sql connection
     * @return the hashmap where Date is the key of each entry in the hash map
     */
    public static HashMap<Date, FlockComplete> allByDate(Connection con) {
        return queryAll(con, flock -> new Date(flock.getStartingDate().getTime()));
    }

    /**
     * Returns a list of flocks between two dates
     *
     * @param conn      the sql connection
     * @param startDate the start date of range
     * @param endDate   the end date of range
     * @return a List of Flock objects
     */
    public static List<Flock> getFlocksFromDate(Connection conn, Date startDate, Date endDate) {
        if (startDate.after(endDate)) {
            generateErrorMessage(
                    "Error in `getFlockFromDate()`.",
                    "End date happens before start date.",
                    "",
                    null
            );

            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT * FROM Flock WHERE Starting_Date BETWEEN ? AND ?
                """)) {

            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
            List<Flock> flocks = new ArrayList<>();

            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    int flockID = result.getInt("Flock_ID");
                    int startingCount = result.getInt("Starting_Count");
                    Date startingDate = result.getDate("Starting_Date");

                    // Creates a flock object and add to the list
                    Flock returnedFlock = new Flock(flockID, startingCount, startingDate);
                    flocks.add(returnedFlock);
                }
            }
            return flocks.isEmpty() ? null : flocks;
        } catch (SQLException e) {
            generateErrorMessage(
                    "Error in `getFlockFromDate()`.",
                    "SQLException occurred.",
                    "",
                    e
            );

            return null;
        }
    }

    /**
     * Get a single flock record from a specified date
     *
     * @param conn the JDBC connection
     * @param date the date
     * @return returns the flock if it exists, otherwise null
     */
    public static Flock getFlockFromADate(Connection conn, Date date) {
        // simply call `getFlocksFromDate` but use the same startDate and endDate
        List<Flock> ret = getFlocksFromDate(conn, date, date);

        return ret != null && !ret.isEmpty() ? ret.getFirst() : null;
    }

    public static List<Flock> searchFlocks(Connection conn, String searchQuery) {
        if (conn == null || searchQuery == null || searchQuery.isBlank()) return null;

        String query = searchQuery.trim().toUpperCase();

        // Year only
        if (query.matches("\\d{4}")) {
            return fetchFlocksByYear(conn, Integer.parseInt(query));
        }

        // Full date
        if (query.matches("(?i)^(\\d{1,2}[-\\s]\\d{1,2}[-\\s]\\d{4}|[A-Z]{3,9}[-\\s]\\d{1,2}[-,\\s]?\\s*\\d{4}|\\d{1,2}\\s\\d{1,2},\\s*\\d{4})$")) {
            return fetchFlocksByFullDate(conn, query);
        }

        // Month + Year
        String[] parts = query.split("[-\\s]+");
        if (query.matches("(?i)^([A-Z]{3,9}|\\d{1,2})(-)?\\s*\\d{4}$")) {
            int month = monthToInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            return fetchFlocksByMonthYearWithFallback(conn, month, year);
        }

        // Month only
        if (query.matches("^[A-Z]{3,9}$|^\\d{1,2}$")) {
            return fetchFlocksByMonthWithFallback(conn, query);
        }

        return null;
    }

    private static List<Flock> fetchFlocksByYear(Connection conn, int year) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
            SELECT flock_id, starting_count, starting_date
            FROM Flock
            WHERE EXTRACT(YEAR FROM starting_date) = ?;
            """)) {
            pstmt.setInt(1, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                return extractFlockResults(rs);
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `fetchFlocksByYear()`", "SQLException occurred.", "", e);
            return null;
        }
    }

    private static List<Flock> fetchFlocksByFullDate(Connection conn, String query) {
        try {
            String cleaned = query.replaceAll("[,\\-]", " ").trim();
            String[] parts = cleaned.split("\\s+");

            String monthStr = parts[0];
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            int month = monthToInt(monthStr);
            if (month == -1 || day <= 0 || day > 31) return null;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date parsedDate = sdf.parse(day + "-" + month + "-" + year);
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

            try (PreparedStatement pstmt = conn.prepareStatement("""
                SELECT flock_id, starting_count, starting_date
                FROM Flock
                WHERE starting_date = ?
                ORDER BY starting_date;
                """)) {
                pstmt.setDate(1, sqlDate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return extractFlockResults(rs);
                }
            }
        } catch (Exception e) {
            generateErrorMessage("Error in `fetchFlocksByFullDate()`", "Exception occurred.", "", e);
            return null;
        }
    }

    private static List<Flock> fetchFlocksByMonthWithFallback(Connection conn, String query) {
        int month = monthToInt(query);
        if (month == -1) return null;

        List<Flock> results = fetchFlocksByMonth(conn, month);
        if (results != null && !results.isEmpty()) return results;

        int prevMonth = (month == 1) ? 12 : month - 1;
        results = fetchFlocksByMonth(conn, prevMonth);
        if (results != null && !results.isEmpty()) return results;

        int nextMonth = (month == 12) ? 1 : month + 1;
        return fetchFlocksByMonth(conn, nextMonth);
    }

    private static List<Flock> fetchFlocksByMonth(Connection conn, int month) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
            SELECT flock_id, starting_count, starting_date
            FROM Flock
            WHERE EXTRACT(MONTH FROM starting_date) = ?
            ORDER BY starting_date;
            """)) {
            pstmt.setInt(1, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                return extractFlockResults(rs);
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `fetchFlocksByMonth()`", "SQLException occurred.", "", e);
            return null;
        }
    }

    private static List<Flock> fetchFlocksByMonthYear(Connection conn, int month, int year) {
        try (PreparedStatement pstmt = conn.prepareStatement("""
            SELECT flock_id, starting_count, starting_date
            FROM Flock
            WHERE EXTRACT(MONTH FROM starting_date) = ? AND EXTRACT(YEAR FROM starting_date) = ?
            ORDER BY starting_date;
            """)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            try (ResultSet rs = pstmt.executeQuery()) {
                return extractFlockResults(rs);
            }
        } catch (SQLException e) {
            generateErrorMessage("Error in `fetchFlocksByMonthYear()`", "SQLException occurred.", "", e);
            return null;
        }
    }

    private static int monthToInt(String monthStr) {
        return switch (monthStr.toUpperCase()) {
            case "JANUARY", "JAN", "1", "01" -> 1;
            case "FEBRUARY", "FEB", "2", "02" -> 2;
            case "MARCH", "MAR", "3", "03" -> 3;
            case "APRIL", "APR", "4", "04" -> 4;
            case "MAY", "5", "05" -> 5;
            case "JUNE", "JUN", "6", "06" -> 6;
            case "JULY", "JUL", "7", "07" -> 7;
            case "AUGUST", "AUG", "8", "08" -> 8;
            case "SEPTEMBER", "SEP", "9", "09" -> 9;
            case "OCTOBER", "OCT", "10" -> 10;
            case "NOVEMBER", "NOV", "11" -> 11;
            case "DECEMBER", "DEC", "12" -> 12;
            default -> -1;
        };
    }

    private static List<Flock> extractFlockResults(ResultSet rs) throws SQLException {
        List<Flock> flocks = new ArrayList<>();
        while (rs.next()) {
            int flockId = rs.getInt("flock_id");
            int startingCount = rs.getInt("starting_count");
            Date startingDate = rs.getDate("starting_date");
            flocks.add(new Flock(flockId, startingCount, startingDate));
        }
        return flocks;
    }

    private static List<Flock> fetchFlocksByMonthYearWithFallback(Connection conn, int month, int year) {
        if (month <= 0 || month > 12) return null;

        List<Flock> results = fetchFlocksByMonthYear(conn, month, year);
        if (results != null && !results.isEmpty()) return results;

        // Fallback: Previous month
        int prevMonth = (month == 1) ? 12 : month - 1;
        results = fetchFlocksByMonthYear(conn, prevMonth, year);
        if (results != null && !results.isEmpty()) return results;

        // Fallback: Next month
        int nextMonth = (month == 12) ? 1 : month + 1;
        return fetchFlocksByMonthYear(conn, nextMonth, year);
    }
}

// Sample execution in App.kt
//fun main() {
//    val app = App()
//    app.start()
//
//
//    val flockMap = View.allByID(app.getConnection())
//
//    if (flockMap == null || flockMap.isEmpty()) {
//        println("No data found.")
//        return
//    }
//
//    for (entry in flockMap.entries) {
//        val flockId = entry.key
//        val flockComplete: FlockComplete = entry.value!!
//                val flock = flockComplete.flock
//
//        println("Flock ID: " + flockId)
//        println("  Starting Count: " + flock.startingCount)
//        println("  Starting Date: " + flock.startingDate)
//
//        val details = flockComplete.flockDetails
//        if (details.isEmpty()) {
//            println("  No Flock Details")
//        } else {
//            for (fd in details) {
//                println("    FD ID: " + fd.flockDetailsId)
//                println("    FD Date: " + fd.fdDate)
//                println("    Depleted Count: " + fd.depletedCount)
//            }
//        }
//    }
//}