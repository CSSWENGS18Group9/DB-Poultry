package org.db_poultry.db.flockDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.db.cleanAndInitTables
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class ReadFlockTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
        initDBAndUser()

        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!

        initTables(conn)
    }

    @Test
    fun testAllByIDValidData() {
        val connection = conn

        // Ensure the connection is non-null before proceeding

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
        cleanAndInitTables(conn)
    }

    @Test
    fun testAllByIDEmptyResultSet() {
        val connection = conn

        // Call the allByID method without inserting any data
        val result = ReadFlock.allByID(connection)

        // Assert that the result is not null and is an empty collection
        assertNotNull(result)
        assertTrue(result.isEmpty())
        cleanAndInitTables(conn)
    }

    @Test
    fun testAllByDateValidData() {
        val connection = conn

        // Ensure the connection is non-null before proceeding

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
        cleanAndInitTables(conn)
    }

    @Test
    fun testAllByDateEmptyResultSet() {
        val connection = conn

        // Call the allByDate method without inserting any data
        val result = ReadFlock.allByDate(connection)

        // Assert that the result is not null and is an empty collection
        assertNotNull(result)
        assertTrue(result.isEmpty())
        cleanAndInitTables(conn)
    }

    @Test
    fun testQueryAllHandlesSQLException() {
        val connection = conn

        // Ensure the connection is non-null before proceeding

        // Close the connection to simulate an SQLException
        connection.close()

        // Call the allByID method with a closed connection
        val result = ReadFlock.allByID(connection)

        // Assert that the result is null, indicating the exception was handled gracefully
        assertNull(result)
        cleanAndInitTables(conn)
    }

    //TESTs FOR public static Flock getFlockFromADate(Connection conn, Date flockDate) {cleanAndInitTables(conn)

    @Test
    fun testGetFlockFromADateWithDataOne() {
        val dateFlockOne = Date.valueOf("1000-05-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val first = ReadFlock.getFlockFromADate(conn, dateFlockOne)

        assertEquals(1, first.flockId)
        assertEquals(100, first.startingCount)
        assertEquals(dateFlockOne, first.startingDate)

        cleanAndInitTables(conn)
    }

    @Test
    fun testGetFlockFromADateNoData() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flock = ReadFlock.getFlockFromADate(conn, dateEnd)

        assertNull(flock)
        cleanAndInitTables(conn)
    }


    //TESTS getFlockFromDate(Connection conn, Date flockDate, Date fdStartDate, Date fdEndDate) {cleanAndInitTables(conn)
    @Test
    fun testGetFlocksFromDateWithDataOne() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlocksFromDate(conn, dateFlockOne, dateEnd)

        val first = flockList[0]

        assertEquals(1, first.flockId)
        assertEquals(100, first.startingCount)
        assertEquals(dateFlockOne, first.startingDate)

        cleanAndInitTables(conn)
    }

    @Test
    fun testGetFlocksFromDateWithDataTwo() {
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
        cleanAndInitTables(conn)
    }

    @Test
    fun testGetFlocksFromDateNoData() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flock = ReadFlock.getFlocksFromDate(conn, dateEnd, dateEnd)

        assertNull(flock)
        cleanAndInitTables(conn)
    }

    @Test
    fun testGetFlocksFromDateEndDateBeforeStartDate() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateEnd = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockList = ReadFlock.getFlocksFromDate(conn, dateEnd, dateFlockOne)

        assertNull(flockList)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForFetchByYear() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "1000")

        assertEquals(3, result.size)
        assertEquals(dateOne, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForFetchByFullDateFirstVariation() {
        val date = Date.valueOf("1000-02-01")
        CreateFlock.createFlock(conn, 100, date)

        val result = ReadFlock.searchFlocks(conn, "February 1, 1000")

        assertEquals(1, result.size)
        assertEquals(date, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForFetchByFullDateSecondVariation() {
        val date = Date.valueOf("1000-02-01")
        CreateFlock.createFlock(conn, 100, date)

        val result = ReadFlock.searchFlocks(conn, "Feb 1, 1000")

        assertEquals(1, result.size)
        assertEquals(date, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForFetchByFullDateThirdVariation() {
        val date = Date.valueOf("1000-02-01")
        CreateFlock.createFlock(conn, 100, date)

        val result = ReadFlock.searchFlocks(conn, "02-01-1000")

        assertEquals(1, result.size)
        assertEquals(date, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForMonthOnly() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-02-02")
        val dateThree = Date.valueOf("1000-02-03")
        val dateOther = Date.valueOf("1000-03-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)
        CreateFlock.createFlock(conn, 400, dateOther)

        val result = ReadFlock.searchFlocks(conn, "February")

        assertEquals(3, result.size)
        assertEquals(dateOne, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        assertEquals(dateTwo, result[1].startingDate)
        assertEquals(200, result[1].startingCount)
        assertEquals(dateThree, result[2].startingDate)
        assertEquals(300, result[2].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForMonthAndYearText() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "February 1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForMonthAndYearNumeric() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "2 1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForMonthAndYearDash() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "02-1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].startingDate)
        assertEquals(100, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithInvalidInputForYear() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "1001")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithInvalidInputForMonth() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "December")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithValidInputForMonthNumericBuffer() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "5") // May doesn't exist, fallback to April
        assertEquals(1, result.size)
        assertEquals(dateThree, result[0].startingDate)
        assertEquals(300, result[0].startingCount)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithInvalidInputForMonthAndYearText() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "March 2000")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksInputForMonthAndYearNumericDNE() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "3 2000")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithInvalidInputForFullDateFirstVariation() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "December 1, 1000")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithInvalidInputForFullDateSecondVariation() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "Dec 1, 1000")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testSearchFlocksWithInvalidInputForFullDateThirdVariation() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 200, dateTwo)
        CreateFlock.createFlock(conn, 300, dateThree)

        val result = ReadFlock.searchFlocks(conn, "12-01-1000")
        assertEquals(0, result.size)
        cleanAndInitTables(conn)
    }

    @Test
    fun testCalculateAliveCountValid() {
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")
        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlockDetails.createFlockDetails(conn, dateOne, dateOne, 50)
        val flock = ReadFlock.getFlockFromADate(conn, dateOne)

        val resultOne = ReadFlock.calculateAliveCount(conn, flock.flockId)

        CreateFlockDetails.createFlockDetails(conn, dateOne, dateTwo, 20)

        val resultTwo = ReadFlock.calculateAliveCount(conn, flock.flockId)

        CreateFlockDetails.createFlockDetails(conn, dateOne, dateThree, 40)

        val resultThree = ReadFlock.calculateAliveCount(conn, flock.flockId)

        assertEquals(50, resultOne)
        assertEquals(30, resultTwo)
        assertEquals(30, resultThree)
        cleanAndInitTables(conn)
    }

    @Test
    fun testCalculateAliveCountZeroFlockDetails() {
        val date = Date.valueOf("1000-02-01")
        CreateFlock.createFlock(conn, 100, date)
        val flock = ReadFlock.getFlockFromADate(conn, date)
        val result = ReadFlock.calculateAliveCount(conn, flock.flockId)
        assertEquals(100, result)
        cleanAndInitTables(conn)
    }

    @Test
    fun testCalculateAliveCountNoData() {
        val result = ReadFlock.calculateAliveCount(conn, 1)
        assertNull(result)
        cleanAndInitTables(conn)
    }
}