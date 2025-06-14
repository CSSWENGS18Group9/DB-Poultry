package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class ReadFlockDetailsTest {
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
    fun testReadFlockDetailsValidInput() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")


        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, 1, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, 1, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, 1, dateThree, 15)

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)


        Assertions.assertEquals(1, result.flockId)
        Assertions.assertEquals(dateThree, result.fdDate)
        Assertions.assertEquals(15, result.depletedCount)
    }

    @Test
    fun testReadFlockDetailsWithNoData() {
        val dateFlock = Date.valueOf("1000-01-01")

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)

        Assertions.assertNull(result)
    }

    @Test
    fun testDepletedCountValidInput() {
        val dateOne = Date.valueOf("1000-05-01")
        val dateTwo = Date.valueOf("1000-06-01")
        val dateThree = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(
            conn, 100, dateOne
        )

        CreateFlockDetails.createFlockDetails(
            conn, 1, dateOne, 5
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateTwo, 10
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateThree, 15
        )

        val result = ReadFlockDetails.getCumulativeDepletedCount(
            conn, 1
        )
        Assertions.assertEquals(30, result)
    }

    @Test
    fun testDepletedCountOverStartCount() {
        val dateOne = Date.valueOf("1000-08-01")
        val dateTwo = Date.valueOf("1000-09-01")
        val dateThree = Date.valueOf("1000-10-01")

        CreateFlock.createFlock(
            conn, 15, dateOne
        )

        CreateFlockDetails.createFlockDetails(
            conn, 1, dateOne, 5
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateTwo, 10
        )
        CreateFlockDetails.createFlockDetails(
            conn, 1, dateThree, 15
        )

        val result = ReadFlockDetails.getCumulativeDepletedCount(
            conn, 1
        )
        Assertions.assertEquals(15, result)
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

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDThree)

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

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDFour)

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

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFlockOne, dateFDTwo)

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

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDTwo)

        assertEquals(0, flockDetailsList.size)
    }

    @Test
    fun testGetFlockDetailsFromDateEndDateBeforeStartDate(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDTwo, dateFDOne)

        assertNull(flockDetailsList)
    }
}