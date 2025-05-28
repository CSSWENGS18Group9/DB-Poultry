package org.db_poultry.db.flockDetailsDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.flockDAO.CreateFlock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Date

class ReadFlockDetailsTest {
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
    fun testReadFlockDetailsValidInput() {
        val dateFlock = Date.valueOf("1000-01-01")
        val dateOne = Date.valueOf("1000-02-01")
        val dateTwo = Date.valueOf("1000-03-01")
        val dateThree = Date.valueOf("1000-04-01")


        CreateFlock.createFlock(conn.getConnection(), 100, dateFlock)

        CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, dateOne, 5)
        CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, dateTwo, 10)
        CreateFlockDetails.createFlockDetails(conn.getConnection(), 1, dateThree, 15)

        val result = ReadFlockDetails.getMostRecent(conn.getConnection(), dateFlock)


        Assertions.assertEquals(1, result.flockId)
        Assertions.assertEquals(dateThree, result.fdDate)
        Assertions.assertEquals(15, result.depletedCount)
    }

    @Test
    fun testReadFlockDetailsWithNoData() {
        val dateFlock = Date.valueOf("1000-01-01")

        val result = ReadFlockDetails.getMostRecent(conn.getConnection(), dateFlock)

        Assertions.assertNull(result)
    }
}