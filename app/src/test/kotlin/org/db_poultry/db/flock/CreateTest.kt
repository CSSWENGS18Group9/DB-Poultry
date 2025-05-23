package org.db_poultry.db.flock

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App
import org.db_poultry.db.flock.*

class CreateTest {
    private var jdbcURL: String
    private var connection: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:$app.databasePort/$app.databaseName"
        connection = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }

    @Test
    fun testCreateFlock() {
        val create = Create()
        assertEquals(create.createFlock(connection, "1999-01-01"), "INSERT INTO Flock (Starting_Date) VALUES (1999-01-01)")
    }

}

