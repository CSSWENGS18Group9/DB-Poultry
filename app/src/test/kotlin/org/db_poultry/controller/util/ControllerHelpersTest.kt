package org.db_poultry.controller.util

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

class ControllerHelpersTest {
    private var jdbcURL: String
    private val conn: Connection

    init {
        val app = App()
        app.getDotEnv()
        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"

        DBConnect.init(jdbcURL, app.databaseName, app.databasePass)
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testValidateAndReadByIDWithValidData() {
        
        requireNotNull(conn)
        // Insert a flock
        CreateFlock.createFlock(conn, 100, Date.valueOf("2025-01-01"))
        val result = validateAndReadByID(conn)
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testValidateAndReadByIDWithNullConnection() {
        val result = validateAndReadByID(null)
        assertNull(result)
    }

    @Test
    fun testValidateAndReadByIDWithNoData() {
        
        cleanTables(conn)
        val result = validateAndReadByID(conn)
        assertNull(result)
    }

    @Test
    fun testValidateAndReadByDatesWithValidData() {
        
        requireNotNull(conn)
        CreateFlock.createFlock(conn, 100, Date.valueOf("2025-01-01"))
        val result = validateAndReadByDates(conn)
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
    }

    @Test
    fun testValidateAndReadByDatesWithNullConnection() {
        val result = validateAndReadByDates(null)
        assertNull(result)
    }

    @Test
    fun testValidateAndReadByDatesWithNoData() {
        
        cleanTables(conn)
        val result = validateAndReadByDates(conn)
        assertNull(result)
    }

    @Test
    fun testCheckDateInbetweenWithNoOverlap() {
        
        cleanTables(conn)
        CreateFlock.createFlock(conn, 100, Date.valueOf("2025-01-01"))
        val overlap = checkDateInbetween(conn, Date.valueOf("2024-12-31"))
        assertEquals(0, overlap)
    }

    @Test
    fun testCheckDateInbetweenWithOverlap() {
        
        cleanTables(conn)
        CreateFlock.createFlock(conn, 100, Date.valueOf("2025-01-01"))
        val overlap = checkDateInbetween(conn, Date.valueOf("2025-01-01"))
        assertTrue(overlap > 0)
    }

    @Test
    fun testCheckDateInbetweenNoOverlap() {
        val dateFlockOne = Date.valueOf("1000-02-01")
        val dateFDOne = Date.valueOf("1000-03-01")
        val dateFDTwo = Date.valueOf("1000-04-01")
        val dateFDThree = Date.valueOf("1000-05-01")

        val dateFlockTwo = Date.valueOf("1000-06-01")
        val dateFDFour = Date.valueOf("1000-07-01")
        val dateFDFive = Date.valueOf("1000-08-01")
        val dateFDSix = Date.valueOf("1000-09-01")

        

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        CreateFlockDetails.createFlockDetails(conn, 1, dateFDOne, 5)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDThree, 15)

        CreateFlock.createFlock(conn, 100, dateFlockTwo)

        CreateFlockDetails.createFlockDetails(conn, 2, dateFDFour, 5)
        CreateFlockDetails.createFlockDetails(conn, 2, dateFDFive, 10)
        CreateFlockDetails.createFlockDetails(conn, 2, dateFDSix, 15)

        assertEquals(
            checkDateInbetween(conn, Date.valueOf("1000-05-02")),
            0
        )

        assertEquals(
            checkDateInbetween(conn, Date.valueOf("1000-05-31")),
            0
        )

        assertEquals(
            checkDateInbetween(conn, Date.valueOf("1000-09-02")),
            0
        )

        assertEquals(
            checkDateInbetween(conn, Date.valueOf("1000-01-01")),
            0
        )
    }

    @Test
    fun testCheckDateInbetweenWithOverlapTwo() {
        val dateFlockOne = Date.valueOf("1000-02-01")
        val dateFDOne = Date.valueOf("1000-03-01")
        val dateFDTwo = Date.valueOf("1000-04-01")
        val dateFDThree = Date.valueOf("1000-05-01")

        val dateFlockTwo = Date.valueOf("1000-06-01")
        val dateFDFour = Date.valueOf("1000-07-01")
        val dateFDFive = Date.valueOf("1000-08-01")
        val dateFDSix = Date.valueOf("1000-09-01")

        

        CreateFlock.createFlock(conn, 100, dateFlockOne)

        CreateFlockDetails.createFlockDetails(conn, 1, dateFDOne, 5)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDTwo, 10)
        CreateFlockDetails.createFlockDetails(conn, 1, dateFDThree, 15)

        CreateFlock.createFlock(conn, 100, dateFlockTwo)

        CreateFlockDetails.createFlockDetails(conn, 2, dateFDFour, 5)
        CreateFlockDetails.createFlockDetails(conn, 2, dateFDFive, 10)
        CreateFlockDetails.createFlockDetails(conn, 2, dateFDSix, 15)

        assertEquals(
            1,
            checkDateInbetween(conn, Date.valueOf("1000-05-01"))
        )

        assertEquals(
            1,
            checkDateInbetween(conn, Date.valueOf("1000-06-01"))
        )

        assertEquals(
            1,
            checkDateInbetween(conn, Date.valueOf("1000-07-12"))
        )

        assertEquals(
            1,
            checkDateInbetween(conn, Date.valueOf("1000-02-01"))
        )

        assertEquals(
            1,
            checkDateInbetween(conn, Date.valueOf("1000-02-05"))
        )
    }

    @Test
    fun testCheckDateInbetweenSQLException() {

        conn.close() // force SQLException
        val overlap = checkDateInbetween(conn, Date.valueOf("2025-01-01"))
        assertEquals(-1, overlap)
    }
}