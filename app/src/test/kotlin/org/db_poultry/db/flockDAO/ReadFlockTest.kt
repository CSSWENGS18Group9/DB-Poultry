package org.db_poultry.db.flockDAO

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import java.sql.Connection
import java.sql.Date

class ReadFlockTest {
    private var jdbcURL: String
    private var conn: Connection

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"

        DBConnect.init(jdbcURL, app.databaseName, app.databasePass)
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testAllByIDValidData() {
        val connection = conn

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
        val connection = conn

        // Call the allByID method without inserting any data
        val result = ReadFlock.allByID(connection)

        // Assert that the result is not null and is an empty collection
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testAllByDateValidData() {
        val connection = conn

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
        val connection = conn

        // Call the allByDate method without inserting any data
        val result = ReadFlock.allByDate(connection)

        // Assert that the result is not null and is an empty collection
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun testQueryAllHandlesSQLException() {
        val connection = conn

        // Ensure the connection is non-null before proceeding
        requireNotNull(connection) { "Database connection is null" }

        // Close the connection to simulate an SQLException
        connection.close()

        // Call the allByID method with a closed connection
        val result = ReadFlock.allByID(connection)

        // Assert that the result is null, indicating the exception was handled gracefully
        assertNull(result)
    }

    //TESTs FOR public static Flock getFlockFromADate(Connection conn, Date flockDate) {}

    @Test
    fun testGetFlockFromADateWithDataOne(){
        val dateFlockOne = Date.valueOf("1000-05-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val first = ReadFlock.getFlockFromADate(conn, dateFlockOne)

        assertEquals(1, first.flockId)
        assertEquals(100, first.startingCount)
        assertEquals(dateFlockOne, first.startingDate)

    }

    @Test
    fun testGetFlockFromADateNoData(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flock = ReadFlock.getFlockFromADate(conn, dateEnd)

        assertNull(flock)
    }


    //TESTS getFlockFromDate(Connection conn, Date flockDate, Date fdStartDate, Date fdEndDate) {}

    @Test
    fun testGetFlocksFromDateWithDataOne(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlocksFromDate(conn, dateFlockOne, dateEnd)

        val first = flockList[0]

        assertEquals(1, first.flockId)
        assertEquals(100, first.startingCount)
        assertEquals(dateFlockOne, first.startingDate)

    }

    @Test
    fun testGetFlocksFromDateWithDataTwo(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateEnd)

        val flockList = ReadFlock.getFlocksFromDate(conn, dateFlockOne, dateEnd)

        val first = flockList[0]
        val second = flockList[1]

        assertEquals(1, first.flockId)
        assertEquals(100, first.startingCount)
        assertEquals(dateFlockOne, first.startingDate)
        assertEquals(2, second.flockId)
        assertEquals(1000, second.startingCount)
        assertEquals(dateEnd, second.startingDate)
    }

    @Test
    fun testGetFlocksFromDateNoData(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flock = ReadFlock.getFlocksFromDate(conn, dateEnd, dateEnd)

        assertNull(flock)
    }

    @Test
    fun testGetFlocksFromDateEndDateBeforeStartDate(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlocksFromDate(conn, dateEnd, dateFlockOne)

        assertNull(flockList)
    }
}