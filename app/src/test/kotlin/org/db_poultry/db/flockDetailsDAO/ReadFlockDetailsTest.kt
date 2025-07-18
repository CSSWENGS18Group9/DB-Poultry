package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.db_poultry.db.flockDAO.ReadFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date
import kotlin.test.assertNotNull

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

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)


        Assertions.assertEquals(1, result.flockId)
        Assertions.assertEquals(dateThree, result.fdDate)
        Assertions.assertEquals(15, result.depletedCount)
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
    }


    @Test
    fun testGetMostRecentWithNoData() {
        val dateFlock = Date.valueOf("1000-01-01")

        val result = ReadFlockDetails.getMostRecent(conn, dateFlock)

        Assertions.assertNull(result)
    }

    // TEST FOR public static List<FlockDetails> getFlockDetailsFromDate(Connection conn, Date flockDate, Date fdStartDate, Date fdEndDate) {
    @Test
    fun testGetFlockDetailsFromDateWithDataOne(){
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
    }

    @Test
    fun testGetFlockDetailsFromDateNoData(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockOne, dateFDOne, dateFDTwo)

        assertNull(flockDetailsList)
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

    @Test
    fun testGetFlockDetailsFromDateDNEFlock(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFlockTwo = Date.valueOf("1000-06-01")
        val dateFDOne = Date.valueOf("1000-06-01")
        val dateFDTwo = Date.valueOf("1000-07-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromDate(conn, dateFlockTwo, dateFDOne, dateFDTwo)

        assertNull(flockDetailsList)
    }

    //TEST for getFlockDetailsFromFlock(Connection conn, Date flockDate)

    @Test
    fun testGetFlockDetailsFromFlockWithValidInput(){
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
    }

    @Test
    fun testGetFlockDetailsFromFlockNoData(){
        val dateFlockOne = Date.valueOf("1000-05-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromFlock(conn, dateFlockOne)

        assertNull(flockDetailsList)
    }

    @Test
    fun testGetFlockDetailsFromFlockDNEFlock(){
        val dateFlockOne = Date.valueOf("1000-05-01")
        val dateFlockTwo = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        val flockDetailsList = ReadFlockDetails.getFlockDetailsFromFlock(conn, dateFlockTwo)

        assertNull(flockDetailsList)
    }

    // SEARCH TESTING TIME WOOHOO!
    @Test
    fun testSearchFlockDetailsWithValidInputForFetchByYear() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "1000")

        assertEquals(3, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForFetchByFullDateFirstVariation() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "February 1, 1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForFetchByFullDateSecondVariation() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "Feb 1, 1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForFetchByFullDateThirdVariation() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "02-01-1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForMonthOnly() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")
        val dateFour = Date.valueOf("1000-02-02")
        val dateFive = Date.valueOf("1000-02-03")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateFour, 20)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateFive, 25)

        val result = ReadFlockDetails.searchFlockDetails(conn, "February")
        assertEquals(3, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
        assertEquals(dateFour, result[1].fdDate)
        assertEquals(20, result[1].depletedCount)
        assertEquals(dateFive, result[2].fdDate)
        assertEquals(25, result[2].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForMonthAndYearText() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "February 1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForMonthAndYearNumeric() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "2 1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForMonthAndYearDash() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "02-1000")
        assertEquals(1, result.size)
        assertEquals(dateOne, result[0].fdDate)
        assertEquals(5, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithInvalidInputForYear() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "1001")

        assertNull(result)
    }

    @Test
    fun testSearchFlockDetailsWithInvalidInputForMonth() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "December")

        assertNull(result)
    }

    @Test
    fun testSearchFlockDetailsWithValidInputForMonthNumericBuffer() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "5")

        assertEquals(1, result.size)
        assertEquals(dateThree, result[0].fdDate)
        assertEquals(15, result[0].depletedCount)
    }

    @Test
    fun testSearchFlockDetailsWithInvalidInputForMonthAndYearText() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "March 2000")

        assertNull(result)
    }

    @Test
    fun testSearchFlockDetailsInputForMonthAndYearNumericDNE() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "3 2000")

        assertNull(result)
    }

    @Test
    fun testSearchFlockDetailsWithInvalidInputForFullDateFirstVariation() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "December 1, 1000")

        assertNull(result)
    }

    @Test
    fun testSearchFlockDetailsWithInvalidInputForFullDateSecondVariation() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "Dec 1, 1000")

        assertNull(result)
    }

    @Test
    fun testSearchFlockDetailsWithInvalidInputForFullDateThirdVariation() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, dateFlock, dateThree, 15)

        val result = ReadFlockDetails.searchFlockDetails(conn, "12-01-1000")

        assertNull(result)
    }
}