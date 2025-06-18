package org.db_poultry.db.supplyRecordDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date

class ReadSupplyRecordTest {
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
    fun testReadSupplyGetFromDateWithData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyCompList = ReadSupplyRecord.getFromDate(conn, date)

        val first = supplyCompList[0]
        val second = supplyCompList[1]

        assertEquals(2, supplyCompList.size)

        assertEquals(1, first.supply_type_id)
        assertEquals(date, first.date)
        assertEquals(BigDecimal("300.0000"), first.added)
        assertEquals(BigDecimal("30.0000"), first.consumed)
        assertEquals(false, first.isRetrieved)

        assertEquals(2, second.supply_type_id)
        assertEquals(date, second.date)
        assertEquals(BigDecimal("100.0000"), second.added)
        assertEquals(BigDecimal("50.0000"), second.consumed)
        assertEquals(false, second.isRetrieved)
    }

    @Test
    fun testReadSupplyGetFromDateWithNoData() {
        val date = Date.valueOf("2025-02-02")

        val supplyCompList = ReadSupplyRecord.getFromDate(conn, date)

        assertNull(supplyCompList)
    }

    @Test
    fun testReadSupplyGetFromDateWithDateNoData() {
        val date = Date.valueOf("2025-02-02")

        // this variable was UNUSED
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        // I changed this from (conn, date) to (conn, oldDate)
        // I am assuming that's what you want @OutForMilks
        val supplyCompList = ReadSupplyRecord.getFromDate(conn, oldDate)

        assertNull(supplyCompList)
    }

    //getFromName

    @Test
    fun testReadSupplyGetFromNameWithData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyCompList = ReadSupplyRecord.getFromName(conn, "Test_1")

        val first = supplyCompList[0]
        val second = supplyCompList[1]

        assertEquals(2, supplyCompList.size)

        assertEquals(1, first.supply_type_id)
        assertEquals(oldDate, first.date)
        assertEquals(BigDecimal("200.0000"), first.added)
        assertEquals(BigDecimal("20.0000"), first.consumed)
        assertEquals(false, first.isRetrieved)

        assertEquals(1, second.supply_type_id)
        assertEquals(date, second.date)
        assertEquals(BigDecimal("300.0000"), second.added)
        assertEquals(BigDecimal("30.0000"), second.consumed)
        assertEquals(false, second.isRetrieved)
    }

    @Test
    fun testReadSupplyGetFromNameWithNoData() {
        val date = Date.valueOf("2025-02-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")

        val supplyCompList = ReadSupplyRecord.getFromName(conn, "Test_1")

        assertNull(supplyCompList)
    }

    @Test
    fun testReadSupplyGetFromNameWithDNESupplyType() {
        val date = Date.valueOf("2025-02-02")

        val supplyCompList = ReadSupplyRecord.getFromName(conn, "Test_1")

        assertNull(supplyCompList)
    }

    @Test
    fun testReadSupplyGetFromNameWithNameNoData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyCompList = ReadSupplyRecord.getFromName(conn, "Test_1")

        assertNull(supplyCompList)
    }

    //GetOneByDateAndName

    @Test
    fun testGetOneByDateAndNameWithData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_1")

        assertEquals(1, supplyComp.supply_type_id)
        assertEquals(date, supplyComp.date)
        assertEquals(BigDecimal("300.0000"), supplyComp.added)
        assertEquals(BigDecimal("30.0000"), supplyComp.consumed)
        assertEquals(false, supplyComp.isRetrieved)
    }

    @Test
    fun testGetOneByDateAndNameWithError() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("600.00"),
            BigDecimal("60.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_1")

        assertEquals(1, supplyComp.supply_type_id)
        assertEquals(date, supplyComp.date)
        assertEquals(BigDecimal("300.0000"), supplyComp.added)
        assertEquals(BigDecimal("30.0000"), supplyComp.consumed)
        assertEquals(false, supplyComp.isRetrieved)
    }

    @Test
    fun testGetOneByDateAndNameWithNoData() {
        val date = Date.valueOf("2025-02-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")

        val supplyComp = ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_1")

        assertNull(supplyComp)
    }

    @Test
    fun testGetOneByDateAndNameWithDNESupplyType() {
        val date = Date.valueOf("2025-02-02")

        val supplyComp = ReadSupplyRecord.getOneByDateAndName(conn, date, "Test_1")

        assertNull(supplyComp)
    }

    @Test
    fun testGetOneByDateAndNameWithDateNoDataOnName() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getOneByDateAndName(conn, oldDate, "Test_2")

        assertNull(supplyComp)
    }

    //getMostRecentFromName

    @Test
    fun testGetMostRecentFromNameWithData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getMostRecentFromName(conn, "Test_1")

        assertEquals(1, supplyComp.supply_type_id)
        assertEquals(date, supplyComp.date)
        assertEquals(BigDecimal("300.0000"), supplyComp.added)
        assertEquals(BigDecimal("30.0000"), supplyComp.consumed)
        assertEquals(false, supplyComp.isRetrieved)
    }

    @Test
    fun testGetMostRecentFromNameWithError() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("600.00"),
            BigDecimal("60.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getMostRecentFromName(conn, "Test_1")

        assertEquals(1, supplyComp.supply_type_id)
        assertEquals(date, supplyComp.date)
        assertEquals(BigDecimal("300.0000"), supplyComp.added)
        assertEquals(BigDecimal("30.0000"), supplyComp.consumed)
        assertEquals(false, supplyComp.isRetrieved)
    }

    @Test
    fun testtGetMostRecentFromNameWithNoData() {

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")

        val supplyComp = ReadSupplyRecord.getMostRecentFromName(conn, "Test_1")

        assertNull(supplyComp)
    }

    @Test
    fun testGetMostRecentFromNameWithDNESupplyType() {

        val supplyComp = ReadSupplyRecord.getMostRecentFromName(conn, "Test_1")

        assertNull(supplyComp)
    }

    //GetMostRecentFromID

    @Test
    fun testGetMostRecentFromIDWithData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getMostRecentFromID(conn, 1)

        assertEquals(1, supplyComp.supply_type_id)
        assertEquals(date, supplyComp.date)
        assertEquals(BigDecimal("300.0000"), supplyComp.added)
        assertEquals(BigDecimal("30.0000"), supplyComp.consumed)
        assertEquals(false, supplyComp.isRetrieved)
    }

    @Test
    fun testGetMostRecentFromIDWithError() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("600.00"),
            BigDecimal("60.00"),
            false
        )

        val supplyComp = ReadSupplyRecord.getMostRecentFromID(conn, 1)

        assertEquals(1, supplyComp.supply_type_id)
        assertEquals(date, supplyComp.date)
        assertEquals(BigDecimal("300.0000"), supplyComp.added)
        assertEquals(BigDecimal("30.0000"), supplyComp.consumed)
        assertEquals(false, supplyComp.isRetrieved)
    }

    @Test
    fun testtGetMostRecentFromIDWithNoData() {

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")

        val supplyComp = ReadSupplyRecord.getMostRecentFromID(conn, 1)

        assertNull(supplyComp)
    }

    @Test
    fun testGetMostRecentFromIDWithDNESupplyType() {

        val supplyComp = ReadSupplyRecord.getMostRecentFromID(conn, 1)

        assertNull(supplyComp)
    }

    //GetCurrentCountForDate

    @Test
    fun testGetCurrentCountWithData() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val oldCount = ReadSupplyRecord.getCurrentCountForDate(conn, 1, oldDate)
        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 1, date)

        assertEquals(BigDecimal("180.0000"), oldCount)
        assertEquals(BigDecimal("450.0000"), currCount)
    }

    @Test
    fun testGetMostRecenCountWithError() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("600.00"),
            BigDecimal("60.00"),
            false
        )

        val oldCount = ReadSupplyRecord.getCurrentCountForDate(conn, 1, oldDate)
        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 1, date)

        assertEquals(BigDecimal("180.0000"), oldCount)
        assertEquals(BigDecimal("450.0000"), currCount)
    }

    @Test
    fun testGetMostRecenCountWithOnlyAdd() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("300.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("0.00"),
            false
        )

        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 2, date)

        assertEquals(BigDecimal("300.0000"), currCount)
    }

    @Test
    fun testGetMostRecenCountWithOnlyConsume() {
        val newDate = Date.valueOf("2025-03-02")
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("0.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("0.00"),
            BigDecimal("70.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            newDate,
            BigDecimal("0.00"),
            BigDecimal("30.00"),
            false
        )


        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 2, newDate)

        assertEquals(BigDecimal("100.0000"), currCount)
    }

    @Test
    fun testGetMostRecenCountWithOverConsume() {
        val newDate = Date.valueOf("2025-03-02")
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("0.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("0.00"),
            BigDecimal("70.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            newDate,
            BigDecimal("0.00"),
            BigDecimal("1000.00"),
            false
        )


        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 2, newDate)

        assertEquals(BigDecimal("130.0000"), currCount)
    }

    @Test
    fun testGetMostRecenCountWithRetrived() {
        val newDate = Date.valueOf("2025-03-02")
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("0.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("0.00"),
            BigDecimal("30.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("0.00"),
            BigDecimal("70.00"),
            false
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            newDate,
            BigDecimal("0.00"),
            BigDecimal("10.00"),
            true
        )


        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 2, newDate)

        assertEquals(BigDecimal("0.0000"), currCount)
    }

    @Test
    fun testtGetMostRecentCountWithNoData() {
        val date = Date.valueOf("2025-02-02")


        CreateSupplyType.createSupplyType(conn, "Test_1", "kg")

        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 1, date)

        assertEquals(BigDecimal.ZERO.setScale(4), currCount)
    }

    @Test
    fun testGetMostRecentCountWithDNESupplyType() {
        val date = Date.valueOf("2025-02-02")


        val currCount = ReadSupplyRecord.getCurrentCountForDate(conn, 1, date)

        assertNull(currCount)
    }
}