package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.db.cleanAndInitTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class ReadFlockDetailsTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection
    private val name = "db_poultry_test"
    init {
        initDBAndUser(name, name)

        DBConnect.init(jdbcURL, name, name)
        conn = DBConnect.getConnection()!!

        initTables(conn, name)
    }

    @Test
    fun testReadFlockDetailsValidInput() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")


        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)


        Assertions.assertEquals(1, result.flockId)
        assertEquals(dateThree, result.fdDate)
        Assertions.assertEquals(15, result.depletedCount)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testDepletedCountValidInput() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-05-01")
        val dateTwo = Date.valueOf("1000-06-01")
        val dateThree = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(
            conn, 100, dateFlock
        )

        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateOne, 5
        )
        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateTwo, 10
        )
        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateThree, 15
        )

        val result = ReadFlockDetails.getCumulativeDepletedCount(
            conn, 1
        )
        Assertions.assertEquals(30, result)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testDepletedCountOverStartCount() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-08-01")
        val dateTwo = Date.valueOf("1000-09-01")
        val dateThree = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(
            conn, 15, dateFlock
        )

        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateOne, 5
        )

        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateTwo, 10
        )

        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateThree, 15
        )

        val result = ReadFlockDetails.getCumulativeDepletedCount(
            conn, 1
        )

        Assertions.assertEquals(15, result)
        cleanAndInitTables(conn, name)
    }

    //Test getMostRecent(

    @Test
    fun testGetMostRecentWithValidInput() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-08-01")
        val dateTwo = Date.valueOf("1000-09-01")
        val dateThree = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(
            conn, 999, dateFlock
        )

        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateOne, 5
        )
        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateTwo, 10
        )
        CreateFlockDetails.createFlockDetails(
            conn, dateFlock, dateThree, 15
        )

        val result = ReadFlockDetails.getMostRecent(
            conn, dateFlock
        )

        assertEquals(dateThree, result.fdDate)
        assertEquals(15, result.depletedCount)
        cleanAndInitTables(conn, name)
    }


    @Test
    fun testGetMostRecentWithNoData() {
        val dateFlock = Date.valueOf("1000-01-01")

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)

        assertNull(result)
        cleanAndInitTables(conn, name)
    }

    // TEST FOR public static List<FlockDetails> getFlockDetailsFromDate(Connection conn, Date flockDate, Date fdStartDate, Date fdEndDate) {
    @Test
    fun testGetFlockDetailsFromDateWithDataOne() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDThree, 2)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDThree)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]
        val third = flockDetailsList[2]

        assertEquals(3, flockDetailsList.size)

        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)

        assertEquals(dateFDThree, third.fdDate)
        assertEquals(2, third.depletedCount)
        cleanAndInitTables(conn, name)
    }


    @Test
    fun testGetFlockDetailsFromDateWithDataTwo() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")
        val dateFlockTwo = Date.valueOf("1000-09-01")
        val dateFDFour = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateFlockTwo)


        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDThree, 2)

        CreateFlockDetails.createFlockDetails(conn, dateFlockTwo, dateFDFour, 0)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDFour)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]
        val third = flockDetailsList[2]

        assertEquals(3, flockDetailsList.size)

        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)

        assertEquals(dateFDThree, third.fdDate)
        assertEquals(2, third.depletedCount)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testGetFlockDetailsFromSelectSlice() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")
        val dateFlockTwo = Date.valueOf("1000-09-01")
        val dateFDFour = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateFlockTwo)

        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDThree, 2)

        CreateFlockDetails.createFlockDetails(conn, dateFlockTwo, dateFDFour, 0)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFlockOne, dateFDTwo)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]

        assertEquals(2, flockDetailsList.size)


        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testGetFlockDetailsFromDateNoData() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDTwo)

        assertNull(flockDetailsList)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testGetFlockDetailsFromDateEndDateBeforeStartDate() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDTwo, dateFDOne)

        assertNull(flockDetailsList)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testGetFlockDetailsFromDateDNEFlock() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFlockTwo = Date.valueOf("1000-06-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockTwo, dateFDOne, dateFDTwo)

        assertNull(flockDetailsList)
        cleanAndInitTables(conn, name)
    }

    //TEST for getFlockDetailsFromFlock(Connection conn, Date flockDate)

    @Test
    fun testGetFlockDetailsFromFlockWithValidInput() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")
        val dateFDThree = Date.valueOf("1000-08-01")
        val dateFlockTwo = Date.valueOf("1000-09-01")
        val dateFDFour = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)
        CreateFlock.createFlock(conn, 1000, dateFlockTwo)

        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDOne, 0)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDTwo, 1)
        CreateFlockDetails.createFlockDetails(conn, dateFlockOne, dateFDThree, 2)

        CreateFlockDetails.createFlockDetails(conn, dateFlockTwo, dateFDFour, 0)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromFlock(conn, dateFlockOne)

        val first = flockDetailsList[0]
        val second = flockDetailsList[1]

        assertEquals(3, flockDetailsList.size)

        assertEquals(dateFDOne, first.fdDate)
        assertEquals(0, first.depletedCount)

        assertEquals(dateFDTwo, second.fdDate)
        assertEquals(1, second.depletedCount)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testGetFlockDetailsFromFlockNoData() {
        val dateFlockOne = Date.valueOf("1000-05-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromFlock(conn, dateFlockOne)

        assertNull(flockDetailsList)
        cleanAndInitTables(conn, name)
    }

    @Test
    fun testGetFlockDetailsFromFlockDNEFlock() {
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFlockTwo = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromFlock(conn, dateFlockTwo)

        assertNull(flockDetailsList)
        cleanAndInitTables(conn, name)
    }
}