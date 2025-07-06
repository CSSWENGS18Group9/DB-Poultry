package org.db_poultry.db.flockDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date
import kotlin.test.*

class DeleteFlockTest {
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
    fun testDeleteFlockWithDataOne() {
        val date =      Date.valueOf("1000-01-01")

        CreateFlock.createFlock(conn, 100, date)
        val result = DeleteFlock.undoCreateFlock(conn)
        assertEquals("DELETE FROM Flock ORDER BY flock_id DESC LIMIT 1", result)
        assertNull(ReadFlock.getFlockFromADate(conn, date))
    }

    fun testDeleteFlockWithDataTwo() {
        val dateOne = Date.valueOf("1000-01-02")
        val dateTwo = Date.valueOf("1000-01-01")

        CreateFlock.createFlock(conn, 100, dateOne)
        CreateFlock.createFlock(conn, 100, dateTwo)
        val result = DeleteFlock.undoCreateFlock(conn)
        assertEquals("DELETE FROM Flock ORDER BY flock_id DESC LIMIT 1", result)
        assertNull(ReadFlock.getFlockFromADate(conn, dateTwo))
    }

    fun testDeleteFlockWithNoData() {
        val result = DeleteFlock.undoCreateFlock(conn)
        assertNull(result)
    }
}