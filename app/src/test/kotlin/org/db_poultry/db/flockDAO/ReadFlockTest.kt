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
    private lateinit var conn: Connection

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

    //TESTS FOR public static List<Flock> getFlockFromDate(Connection conn, Date startDate, Date endDate) {

    @Test
    fun testGetFlockFromDateWithDataOne(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlockFromDate(conn, dateFlockOne, dateEnd)

        val first = flockList[0]

        assertEquals(1, first.flockId)
        assertEquals(100, first.startingCount)
        assertEquals(dateFlockOne, first.startingDate)

    }

    @Test
    fun testGetFlockFromDateWithDataTwo(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateEnd)

        val flockList = ReadFlock.getFlockFromDate(conn, dateFlockOne, dateEnd)

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
    fun testGetFlockFromDateNoData(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlockFromDate(conn, dateEnd, dateEnd)

        assertEquals(0, flockList.size)
    }

    @Test
    fun testGetFlockFromDateEndDateBeforeStartDate(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlockFromDate(conn, dateEnd, dateFlockOne)

        assertNull(flockList)
    }

    // TEST FOR public static List<FlockDetails> getFlockDetailsFromDate(Connection conn, Date flockDate, Date fdStartDate, Date fdEndDate) {
    @Test
    fun testGetFlockDetailsFromDateWithDataOne(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        CreateFlockDetails.createFlockDetails(conn, 1, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDThree, 2)

        val flockDetailsList = ReadFlock.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDThree)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]
        val third = flockDetailsList[2]

        assertEquals(3, flockDetailsList.size)

        assertEquals(1, first.flockDetailsId)
        assertEquals(1, first.flockId)
        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(2, second.flockDetailsId)
        assertEquals(1, second.flockId)
        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)

        assertEquals(3, third.flockDetailsId)
        assertEquals(1, third.flockId)
        assertEquals(dateFDThree, third.fdDate)
        assertEquals(2, third.depletedCount)
    }


    @Test
    fun testGetFlockDetailsFromDateWithDataTwo(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")
        val dateFlockTwo = Date.valueOf("1000-09-01")
        val dateFDFour = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateFlockTwo)

        CreateFlockDetails.createFlockDetails(conn, 1, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDThree, 2)

        CreateFlockDetails.createFlockDetails(conn, 2, dateFDFour, 0)

        val flockDetailsList = ReadFlock.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDFour)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]
        val third = flockDetailsList[2]

        assertEquals(3, flockDetailsList.size)

        assertEquals(1, first.flockDetailsId)
        assertEquals(1, first.flockId)
        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(2, second.flockDetailsId)
        assertEquals(1, second.flockId)
        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)

        assertEquals(3, third.flockDetailsId)
        assertEquals(1, third.flockId)
        assertEquals(dateFDThree, third.fdDate)
        assertEquals(2, third.depletedCount)
    }

    @Test
    fun testGetFlockDetailsFromSelectSlice(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")
        val dateFlockTwo = Date.valueOf("1000-09-01")
        val dateFDFour = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateFlockTwo)

        CreateFlockDetails.createFlockDetails(conn, 1, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDThree, 2)

        CreateFlockDetails.createFlockDetails(conn, 2, dateFDFour, 0)

        val flockDetailsList = ReadFlock.getFlockDetailsFromDate(conn, dateFlockOne, dateFlockOne, dateFDTwo)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]

        assertEquals(2, flockDetailsList.size)

        assertEquals(1, first.flockDetailsId)
        assertEquals(1, first.flockId)
        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(2, second.flockDetailsId)
        assertEquals(1, second.flockId)
        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)
    }

    @Test
    fun testGetFlockDetailsNoData(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlock.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDTwo)

        assertEquals(0, flockDetailsList.size)
    }

    @Test
    fun testGetFlockDetailsFromDateEndDateBeforeStartDate(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlock.getFlockDetailsFromDate(conn, dateFlockOne, dateFDTwo, dateFDOne)

        assertNull(flockDetailsList)
    }
}