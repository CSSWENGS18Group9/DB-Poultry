package org.db_poultry.db.flockDAO;

import org.db_poultry.pojo.Flock;
import org.db_poultry.pojo.FlockComplete;
import org.db_poultry.pojo.FlockDetails;

import java.sql.*;
import java.util.HashMap;
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

        String sql = "SELECT f.flock_id, f.starting_count, f.starting_date, " + "fd.flock_details_id, fd.fd_date, fd.depleted_count " + "FROM Flock f INNER JOIN Flock_Details fd ON f.flock_id = fd.flock_id";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Flock columns
                int flockId = rs.getInt("flock_id");
                int startingCount = rs.getInt("starting_count");
                Timestamp startingDate = rs.getTimestamp("starting_date");

                // Flock Details columns
                int fdId = rs.getInt("flock_details_id");
                Timestamp fdDate = rs.getTimestamp("fd_date");
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