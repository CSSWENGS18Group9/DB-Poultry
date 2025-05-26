package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Date

class DepletedCountTest {

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
    fun testGetCumulativeDepletedCount() {
        val dateOne = Date.valueOf("1000-05-01")
        val dateTwo = Date.valueOf("1000-06-01")
        val dateThree = Date.valueOf("1000-07-01")

        CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn.getConnection(), 2, dateThree, 15)

        val result = DepletedCount.cumulativeDepletedCount(conn.getConnection(), 2)
        Assertions.assertEquals(30, result)
    }
}