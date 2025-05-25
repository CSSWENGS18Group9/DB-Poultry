package org.db_poultry.db.flock

import org.db_poultry.db.flock.pojo.FlockDetails

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App

class ViewTest {
    private var jdbcURL: String
    private var conn: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${app.databasePort}/${app.databaseName}"
        conn = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }

    //The tests ARE to be updated once the Create and Delete functions are approved and tested.

    @Test
    //Testing to see if Flock is being returned correctly
    fun testViewFlock() {
        val result = View.allByID(conn.getConnection())
        assertEquals(result.size, 1)
    }

    @Test
    //Testing to see if Flock Details are being returned correctly
    fun testViewFlockDetails() {
        val result = View.allByID(conn.getConnection())
        val flockDetailsList = result[1] as List<FlockDetails>
        assertEquals(flockDetailsList.size, 3)
    }
}

