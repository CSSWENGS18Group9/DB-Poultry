package org.db_poultry.db.supplyRecordDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.Date

/*
CreateSupplyRecordTest.kt

Restrictions:
- Supply_ID must be a positive integer.
- SR_Date must be a valid date.
- Added and Consumed must be non-negative floats/integers.
- You cannot create a supply record with the same date
- You can create a supply record with zero Added and Consumed.

TODO: Tests to implement with helper functions :
- You cannot create a supply record with a date before the most recent supply record date with the same supply ID.
- You can create a supply record with the same date as the most recent supply record if a supply record 
with the same supply ID does not already exist for that date.
- You can create a supply record with the same date as the most recent supply record if the supply ID is different.
- You cannot create a supply record with a higher consumed than the summation of all previous added values for that supply ID on that date.
*/

class CreateSupplyRecordTest {
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
    fun testCreateSupplyRecordValidInputs() {
        val date    = Date.valueOf("2025-01-01")
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 1, date, 100.0f, 50.0f)
        assertEquals("INSERT INTO Supply_Record (Supply_ID, SR_Date, Added, Consumed, Retrieved) VALUES (1, '2025-01-01', 100.0, 50.0, 0)", result)
    }

    @Test
    fun testCreateSupplyRecordWithSameDateAndSameSupplyID() {
        val date    = Date.valueOf("2025-01-02")
        CreateSupplyRecord.createSupplyRecord(conn, 1, date, 100.0f, 50.0f)
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 1, date, 200.0f, 100.0f)
        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithOldDate() {
        val date    = Date.valueOf("2025-01-03")
        CreateSupplyRecord.createSupplyRecord(conn, 1, date, 100.0f, 50.0f)
        val oldDate = Date.valueOf("2025-01-02")
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 1, oldDate, 200.0f, 100.0f)
        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithSameDateButWithDifferentSupplyID() {
        val date    = Date.valueOf("2025-01-04")
        CreateSupplyRecord.createSupplyRecord(conn, 1, date, 100.0f, 50.0f)
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 2, date, 200.0f, 100.0f)
        assertEquals("INSERT INTO Supply_Record (Supply_ID, SR_Date, Added, Consumed) VALUES (2, '2025-01-04', 200.0, 100.0)", result)
    }

    @Test
    fun testCreateSupplyRecordWithZeroAddedAndConsumed() {
        val date    = Date.valueOf("2025-01-03")
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 1, date, 0.0f, 0.0f)
        assertEquals("INSERT INTO Supply_Record (Supply_ID, SR_Date, Added, Consumed) VALUES (1, '2025-01-03', 0.0, 0.0)", result)
    }

    @Test
    fun testCreateSupplyRecordWithNegativeAdded() {
        val date    = Date.valueOf("2025-01-04")
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 1, date, -10.0f, 50.0f)
        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithNegativeConsumed() {
        val date    = Date.valueOf("2025-01-05")
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 1, date, 100.0f, -50.0f)
        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithZeroSupplyID() {
        val date    = Date.valueOf("2025-01-06")
        val result  = CreateSupplyRecord.createSupplyRecord(conn, 0, date, 100.0f, 50.0f)
        assertNull(result)
    }
}