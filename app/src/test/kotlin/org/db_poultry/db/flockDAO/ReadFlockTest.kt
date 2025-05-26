package org.db_poultry.db.flockDAO

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import org.db_poultry.db.cleanTables
import java.sql.Date

class ReadFlockTest {
    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
        cleanTables(conn.getConnection())
    }

    @Test
    fun testAllByIDValidData() {
        val connection = conn.getConnection()

        // Ensure the connection is non-null before proceeding
        requireNotNull(connection) { "Database connection is null" }

        // Insert valid data into the Flock and Flock_Details tables
        connection.createStatement().use { stmt ->
            stmt.executeUpdate("INSERT INTO Flock VALUES (1, 100, '2025-01-01 00:00:00')")
            stmt.executeUpdate("INSERT INTO Flock_Details VALUES (10, 1, '2025-01-02 00:00:00', 5)")
        }

        // Call the allByID method to retrieve data
        val result = ReadFlock.allByID(connection)

        // Assert that the result is not null and contains the expected data
        assertNotNull(result)
        assertEquals(1, result.size)
        val flockComplete = result[1]
        assertNotNull(flockComplete)
        assertEquals(100, flockComplete!!.flock.startingCount)
        assertEquals(1, flockComplete.flock.flockId)
        assertEquals(1, flockComplete.flockDetails.size)
        assertEquals(5, flockComplete.flockDetails[0].depletedCount)
    }

    @Test
    fun testAllByIDEmptyResultSet() {
        val connection = conn.getConnection()

        // Call the allByID method without inserting any data
        val result = ReadFlock.allByID(connection)

        // Assert that the result is not null and is an empty collection
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testAllByDateValidData() {
        val connection = conn.getConnection()

        // Ensure the connection is non-null before proceeding
        requireNotNull(connection) { "Database connection is null" }

        // Insert valid data into the Flock and Flock_Details tables
        connection.createStatement().use { stmt ->
            stmt.executeUpdate("INSERT INTO Flock VALUES (1, 100, '2025-01-01 00:00:00')")
            stmt.executeUpdate("INSERT INTO Flock_Details VALUES (10, 1, '2025-01-02 00:00:00', 5)")
        }

        // Call the allByDate method to retrieve data grouped by date
        val result = ReadFlock.allByDate(connection)

        // Assert that the result is not null and contains the expected data
        assertNotNull(result)
        assertEquals(1, result.size)
        assertTrue(result.containsKey(Date.valueOf("2025-01-01")))
    }

    @Test
    fun testAllByDateEmptyResultSet() {
        val connection = conn.getConnection()

        // Call the allByDate method without inserting any data
        val result = ReadFlock.allByDate(connection)

        // Assert that the result is not null and is an empty collection
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testQueryAllHandlesSQLException() {
        val connection = conn.getConnection()

        // Ensure the connection is non-null before proceeding
        requireNotNull(connection) { "Database connection is null" }

        // Close the connection to simulate an SQLException
        connection.close()

        // Call the allByID method with a closed connection
        val result = ReadFlock.allByID(connection)

        // Assert that the result is null, indicating the exception was handled gracefully
        assertNull(result)
    }
}