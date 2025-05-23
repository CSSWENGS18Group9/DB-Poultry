package org.db_poultry.db.flock_details

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import java.sql.Timestamp
import java.text.SimpleDateFormat

class CreateTest {
    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }

    @Test
    fun testCreateFlock() {
        val timestamp = Timestamp(SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01").time)
        val result = Create.createFlockDetails(conn.conn, 1, timestamp, 0, 0)
        assertEquals(result, "INSERT INTO Flock_Details (Flock_ID, FD_Date, Current_Count, Depleted_Count) VALUES (1, $timestamp, 0, 0)", "succesfully created flock")

    }
}


