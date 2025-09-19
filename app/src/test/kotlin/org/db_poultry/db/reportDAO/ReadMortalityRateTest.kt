package org.db_poultry.db.reportDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReadMortalityRateTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
        initDBAndUser()

        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!

        initTables(conn)
    }

    @Test
    fun testReadMortalityRateForFlockWithValidInput() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-02-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-07-01")
        val fdDateFour = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)
        CreateFlock.createFlock(conn, 1000, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, flockOneDate, 20)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 30)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 50)

        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, flockTwoDate, 30)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateThree, 40)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateFour, 60)

        val result = ReadMortalityRate.calculateMortalityRateForFlock(conn, flockOneDate)

        assertEquals(flockOneDate, result.startDate)
        assertEquals(fdDateTwo, result.endDate)
        assertEquals(10.0f, result.mortalityRate)
        assertEquals(1000, result.startCount)
        assertEquals(900, result.curCount)

        val resultTwo = ReadMortalityRate.calculateMortalityRateForFlock(conn, flockTwoDate)

        assertEquals(flockTwoDate, resultTwo.startDate)
        assertEquals(fdDateFour, resultTwo.endDate)
        assertEquals(13.0f, resultTwo.mortalityRate)
        assertEquals(1000, resultTwo.startCount)
        assertEquals(870, resultTwo.curCount)
    }

    @Test
    fun testReadMortalityRateForFlockWithWrongDate() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-02-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-07-01")
        val fdDateFour = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)
        CreateFlock.createFlock(conn, 1000, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, flockOneDate, 20)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 30)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 50)

        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, flockTwoDate, 30)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateThree, 40)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateFour, 60)

        val result = ReadMortalityRate.calculateMortalityRateForFlock(conn, fdDateOne)

        assertNull(result)
    }

    @Test
    fun testReadMortalityRateForFlockNoFlockDetails() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)
        CreateFlock.createFlock(conn, 1000, flockTwoDate)

        val result = ReadMortalityRate.calculateMortalityRateForFlock(conn, flockOneDate)

        assertEquals(flockOneDate, result.startDate)
        assertNull(result.endDate)
        assertEquals(0.0f, result.mortalityRate)
        assertEquals(1000, result.startCount)
        assertEquals(1000, result.curCount)

        val resultTwo = ReadMortalityRate.calculateMortalityRateForFlock(conn, flockTwoDate)

        assertEquals(flockTwoDate, resultTwo.startDate)
        assertNull(resultTwo.endDate)
        assertEquals(0.0f, resultTwo.mortalityRate)
        assertEquals(1000, resultTwo.startCount)
        assertEquals(1000, resultTwo.curCount)
    }

    @Test
    fun testReadMortalityRateForFlockNoData() {
        val flockTwoDate = Date.valueOf("1000-06-01")

        val result = ReadMortalityRate.calculateMortalityRateForFlock(conn, flockTwoDate)

        assertNull(result)
    }

    //tests for calculateMortalityRateForFlockDate

    @Test
    fun testReadMortalityRateForFlockDateWithValidInput() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-02-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-07-01")
        val fdDateFour = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)
        CreateFlock.createFlock(conn, 1000, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, flockOneDate, 20)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 30)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 50)

        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, flockTwoDate, 30)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateThree, 40)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateFour, 60)

        val result = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockOneDate, fdDateTwo)

        assertEquals(flockOneDate, result.startDate)
        assertEquals(fdDateTwo, result.endDate)
        assertEquals(5.263158f, result.mortalityRate)
        assertEquals(1000, result.startCount)
        assertEquals(900, result.curCount)

        val resultTwo = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockTwoDate, fdDateFour)

        assertEquals(flockTwoDate, resultTwo.startDate)
        assertEquals(fdDateFour, resultTwo.endDate)
        assertEquals(6.4516125f, resultTwo.mortalityRate)
        assertEquals(1000, resultTwo.startCount)
        assertEquals(870, resultTwo.curCount)

        val resultThree = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockOneDate, fdDateOne)

        assertEquals(flockOneDate, resultThree.startDate)
        assertEquals(fdDateTwo, resultThree.endDate)
        assertEquals(3.0612245f, resultThree.mortalityRate)
        assertEquals(1000, resultThree.startCount)
        assertEquals(900, resultThree.curCount)

        val resultFour = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockTwoDate, fdDateThree)

        assertEquals(flockTwoDate, resultFour.startDate)
        assertEquals(fdDateFour, resultFour.endDate)
        assertEquals(4.123711f, resultFour.mortalityRate)
        assertEquals(1000, resultFour.startCount)
        assertEquals(870, resultFour.curCount)

        val resultFive = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, fdDateOne, fdDateTwo)

        assertNull(resultFive)

        val resultSix = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, fdDateThree, fdDateFour)

        assertNull(resultSix)
    }

    @Test
    fun testReadMortalityRateForFlockDateWithSameStartandEndDate() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-02-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-07-01")
        val fdDateFour = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)
        CreateFlock.createFlock(conn, 1000, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, flockOneDate, 20)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 30)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 50)

        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, flockTwoDate, 30)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateThree, 40)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateFour, 60)

        val result = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockOneDate, flockOneDate)

        assertEquals(flockOneDate, result.startDate)
        assertEquals(fdDateTwo, result.endDate)
        assertEquals(2.0f, result.mortalityRate)
        assertEquals(1000, result.startCount)
        assertEquals(900, result.curCount)

        val resultTwo = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockTwoDate, flockTwoDate)

        assertEquals(flockTwoDate, resultTwo.startDate)
        assertEquals(fdDateFour, resultTwo.endDate)
        assertEquals(3.0f, resultTwo.mortalityRate)
        assertEquals(1000, resultTwo.startCount)
        assertEquals(870, resultTwo.curCount)
    }

    @Test
    fun testReadMortalityRateForFlockDateWithOverlAppingDates() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-02-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-07-01")
        val fdDateFour = Date.valueOf("1000-08-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)
        CreateFlock.createFlock(conn, 1000, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, flockOneDate, 20)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 30)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 50)

        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, flockTwoDate, 30)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateThree, 40)
        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateFour, 60)

        val result = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockOneDate, fdDateFour)

        assertNull(result)
    }

    @Test
    fun testReadMortalityRateForFlockDateWithNoFlockDetails() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-03-01")

        CreateFlock.createFlock(conn, 1000, flockOneDate)

        val result = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockOneDate, fdDateTwo)

        assertEquals(flockOneDate, result.startDate)
        assertNull(result.endDate)
        assertEquals(0.0f, result.mortalityRate)
        assertEquals(1000, result.startCount)
        assertEquals(1000, result.curCount)
    }

    @Test
    fun testReadMortalityRateForFlockDateWithNoData() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-03-01")

        val result = ReadMortalityRate.calculateMortalityRateForFlockDate(conn, flockOneDate, fdDateTwo)

        assertNull(result)
    }
}