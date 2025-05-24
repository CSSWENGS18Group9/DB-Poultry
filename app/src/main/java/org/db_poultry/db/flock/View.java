package org.db_poultry.db.flock;

import org.db_poultry.db.flock.pojo.FlockDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class View {

    /**
     * A query that gets all the information and all records in the Flock table. Used in allByID and allByDate.
     *
     *
     * @param con the sql connection
     * @param keyMapper function that extracts a key from the FlockDetails pojo and uses it as the key of the hash map
     * @return hashmap of the Flock and FlockDetails table
     * @param <K> the generic; the type of the key in the hash map. See allByID and allByDate to see what <K> refers to
     */
    private static <K> HashMap<K, List<FlockDetails>> queryAll(Connection con, Function<FlockDetails, K> keyMapper) {
        HashMap<K, List<FlockDetails>> result = new HashMap<>();

        String sql = "SELECT f.flock_id, f.starting_date, fd.fd_date, fd.current_count, fd.depleted_count " +
                "FROM Flock f INNER JOIN Flock_Details fd ON f.flock_id = fd.flock_id";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int flockId = rs.getInt("flock_id");
                Timestamp startingDate = rs.getTimestamp("starting_date");
                Timestamp fdDate = rs.getTimestamp("fd_date");
                int currentCount = rs.getInt("current_count");
                int depletedCount = rs.getInt("depleted_count");

                FlockDetails detail = new FlockDetails(flockId, startingDate, fdDate, currentCount, depletedCount);
                K key = keyMapper.apply(detail);

                result.computeIfAbsent(key, k -> new ArrayList<>()).add(detail);
            }

        } catch (SQLException e) {
            generateErrorMessage("Error in `queryFlocks()`.", "SQLException occurred.", "", e);
            return null;
        }

        return result;
    }

    /**
     * Uses the generic function above to get all table entries and relates it by ID
     * @param con the sql connection
     * @return the hashmap where flockID is the key of each entry in the hash map
     */
    public static HashMap<Integer, List<FlockDetails>> allByID(Connection con) {
        return queryAll(con, FlockDetails::getFlockId);
    }

    /**
     * Uses the generic function above to get all table entries and relates it by Date
     * @param con the sql connection
     * @return the hashmap where Date is the key of each entry in the hash map
     */
    public static HashMap<Date, List<FlockDetails>> allByDate(Connection con) {
        return queryAll(con, detail -> new Date(detail.getFdDate().getTime()));
    }
}

// Sample execution in App.kt
//fun main() {
//    val app = App()
//    app.start()
//
//
//    val res: HashMap<Int, List<FlockDetails>> = allByID(app.getConnection())
//
//    for ((key, detailList) in res) {
//        println("Flock ID: $key")
//        for (detail in detailList) {
//            println("  FD Date: ${detail.fdDate}, Current: ${detail.currentCount}, Depleted: ${detail.depletedCount}")
//        }
//    }
//
//}