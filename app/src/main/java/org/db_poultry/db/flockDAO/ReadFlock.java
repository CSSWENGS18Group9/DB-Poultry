package org.db_poultry.db.flockDAO;

import org.db_poultry.pojo.FlockPOJO.Flock;
import org.db_poultry.pojo.FlockPOJO.FlockComplete;
import org.db_poultry.pojo.FlockPOJO.FlockDetails;

import java.sql.*;
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
     * @param <K>       the generic; the type of the key in the hash map. See allByID and allByDate to see what <K> refers to
     * @return hashmap of the Flock and FlockDetails table
     */
    private static <K> HashMap<K, FlockComplete> queryAll(Connection con, Function<Flock, K> keyMapper) {
        HashMap<K, FlockComplete> result = new HashMap<>();

        String sql =    "SELECT f.flock_id, f.starting_count, f.starting_date, " +
                        "fd.flock_details_id, fd.fd_date, fd.depleted_count " +
                        "FROM Flock f LEFT JOIN Flock_Details fd ON f.flock_id = fd.flock_id";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

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
            generateErrorMessage("Error in `queryFlocks()`.", "SQLException occurred.", "", e);
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
     * @param conn          the sql connection
     * @param startDate     the start date of range
     * @param endDate       the end date of range
     * @return a List of Flock objects
     */
    public static List<Flock> getFlocksFromDate(Connection conn, Date startDate, Date endDate) {
        String incompleteQuery = "SELECT * FROM Flock WHERE Starting_Date BETWEEN ? AND ?"; // Query to be used in preparedStatement

        if(startDate.after(endDate)) {
            generateErrorMessage("Error in `getFlockFromDate()`.", "End date happens before start date.", "", null);
            return null;
        }

        try {
            PreparedStatement pstmt = conn.prepareStatement(incompleteQuery); // preparedStatement for SQL stuff

            // Sets the values to be added
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);

            ResultSet result = pstmt.executeQuery(); // Executes query and stores it into a ResultSet

            List<Flock> flocks = new ArrayList<>();

            while (result.next()) { // Gets results per row from the SQL output
                int flockID = result.getInt("Flock_ID"); // Gets the output from the column named Flock_ID
                int startingCount = result.getInt("Starting_Count"); // Gets the output from the column named Starting_Count
                Date startingDate = result.getDate("Starting_Date"); // Gets the output from the column named Starting_Date

                Flock returnedFlock = new Flock(flockID, startingCount, startingDate); // Creates a flock object to be returned

                flocks.add(returnedFlock);
            }

            result.close(); // CLoses result
            pstmt.close(); // Closes preparedStatement

            return flocks; // Returns the List of Flock

        } catch (SQLException e) {
            generateErrorMessage("Error in `getFlockFromDate()`.", "SQLException occurred.", "", e);
            return null;
        }
    }

    public static Flock getFlockFromADate(Connection conn, Date startDate) {
        // simply call `getFlocksFromDate` but use the same startDate and endDate
        List<Flock> ret = getFlocksFromDate(conn, startDate, startDate);

        return ret != null && !ret.isEmpty() ? ret.getFirst() : null;
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