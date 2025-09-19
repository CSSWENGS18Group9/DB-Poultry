package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class CreateFlockDetailsTest {
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
        initDBAndUser()

        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!

        initTables(conn)
    }

    @Test
    fun testCreateFlockDetailsValidInput() {
        val flockDate = Date.valueOf("1000-01-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockDate)

        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 15)

        assertEquals(result, "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (1, '1000-01-03', 15)")
    }

    @Test
    fun testCreateFlockDetailsZeroDepletedCount() {
        val flockDate = Date.valueOf("1000-01-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockDate)

        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 0)
        assertEquals(result, "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (1, '1000-01-03', 0)")
    }

    @Test
    fun testCreateFlockDetailsWithNegativeDepletedCount() {
        val flockDate = Date.valueOf("1000-01-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockDate)

        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, -1)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithDepletedGreaterThanStartingCountOne() {
        val flockDate = Date.valueOf("1000-01-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockDate)

        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 1000)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithDepletedGreaterThanStartingCountTwo() {
        val flockDate = Date.valueOf("1000-01-01")
        val fdDateOne = Date.valueOf("1000-01-03")
        val fdDateTwo = Date.valueOf("1000-01-05")

        CreateFlock.createFlock(conn, 999, flockDate)

        CreateFlockDetails.createFlockDetails(conn, flockDate, fdDateOne, 500)
        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDateTwo, 600)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDate() {
        val flockDate = Date.valueOf("1000-01-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockDate)

        CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 100)
        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 100)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsBeforeFlockStartDate() {
        val flockDate = Date.valueOf("1000-02-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockDate)

        CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 100)
        val result = CreateFlockDetails.createFlockDetails(conn, flockDate, fdDate, 100)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsWithSameDateInDifferentFlock() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDateOne = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-04-01")
        val fdDateFour = Date.valueOf("1000-07-01")
        val fdDateOverlad = Date.valueOf("1000-06-05")

        CreateFlock.createFlock(conn, 999, flockOneDate)
        CreateFlock.createFlock(conn, 999, flockTwoDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 10)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateThree, 10)

        val resultOne = CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateFour, 10)
        assertNull(resultOne)

        CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDateFour, 10)

        val resultTwo = CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOverlad, 10)
        assertNull(resultTwo)

        val resultThree = CreateFlockDetails.createFlockDetails(conn, flockOneDate, flockTwoDate, 10)
        assertNull(resultThree)
    }

    @Test
    fun testCreateFlockDetailsWithDNEFlockDate() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val flockTwoDate = Date.valueOf("1000-06-01")
        val fdDate = Date.valueOf("1000-01-03")

        CreateFlock.createFlock(conn, 999, flockOneDate)

        val result = CreateFlockDetails.createFlockDetails(conn, flockTwoDate, fdDate, 100)
        assertNull(result)
    }

    @Test
    fun testCreateFlockDetailsInbetween() {
        val flockOneDate = Date.valueOf("1000-01-01")
        val fdDateOne = Date.valueOf("1000-01-01")
        val fdDateTwo = Date.valueOf("1000-03-01")
        val fdDateThree = Date.valueOf("1000-04-01")

        CreateFlock.createFlock(conn, 999, flockOneDate)

        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateOne, 10)
        CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateThree, 15)

        val result = CreateFlockDetails.createFlockDetails(conn, flockOneDate, fdDateTwo, 20)

        assertEquals(result, "INSERT INTO Flock_Details (Flock_ID, FD_Date, Depleted_Count) VALUES (1, '1000-03-01', 20)")
    }
}

