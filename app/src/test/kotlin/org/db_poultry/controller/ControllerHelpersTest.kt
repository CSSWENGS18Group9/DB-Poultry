package org.db_poultry.controller

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.Date

class ControllerHelpersTest {
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
    fun testValidateAndReadByIDWithValidData() {
        val connection = conn.getConnection()
        requireNotNull(connection)
        // Insert a flock
        CreateFlock.createFlock(connection, 100, Date.valueOf("2025-01-01"))
        val result = validateAndReadByID(connection)
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
        val connection = conn.getConnection()
        cleanTables(connection)
        val result = validateAndReadByID(connection)
        assertNull(result)
    }

    @Test
    fun testValidateAndReadByDatesWithValidData() {
        val connection = conn.getConnection()
        requireNotNull(connection)
        CreateFlock.createFlock(connection, 100, Date.valueOf("2025-01-01"))
        val result = validateAndReadByDates(connection)
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
        val connection = conn.getConnection()
        cleanTables(connection)
        val result = validateAndReadByDates(connection)
        assertNull(result)
    }

    @Test
    fun testCheckDateInbetweenWithNoOverlap() {
        val connection = conn.getConnection()
        cleanTables(connection)
        CreateFlock.createFlock(connection, 100, Date.valueOf("2025-01-01"))
        val overlap = connection?.let { checkDateInbetween(it, Date.valueOf("2024-12-31")) }
        assertEquals(0, overlap)
    }

    @Test
    fun testCheckDateInbetweenWithOverlap() {
        val connection = conn.getConnection()
        cleanTables(connection)
        CreateFlock.createFlock(connection, 100, Date.valueOf("2025-01-01"))
        val overlap = connection?.let { checkDateInbetween(it, Date.valueOf("2025-01-01")) }
        overlap?.let { assertTrue(it > 0) }
    }

    @Test
    fun testCheckDateInbetweenSQLException() {
        val connection = conn.getConnection()
        connection?.close() // force SQLException
        val overlap = connection?.let { checkDateInbetween(it, Date.valueOf("2025-01-01")) }
        assertEquals(-1, overlap)
    }
}