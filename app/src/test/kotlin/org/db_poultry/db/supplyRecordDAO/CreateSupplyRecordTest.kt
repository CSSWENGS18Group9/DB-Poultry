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

class CreateSupplyRecordTest {
    private var jdbcURL: String
    private var conn: Connection

    init {
        jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
        DBConnect.init(jdbcURL, "db_poultry_test", "db_poultry_test")
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testCreateSupplyRecordValidInputs() {
        val date = Date.valueOf("2025-01-01")

        CreateSupplyType.createSupplyType(conn, "Test", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )
        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (1, '2025-01-01', 100.0000, 50.0000, 50.0000, false)",
            result
        )
    }

    @Test
    fun testCreateSupplyRecordWithDNESupplyID() {
        val date = Date.valueOf("2025-01-02")

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            13,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )


        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithSameDateAndSameSupplyID() {
        val date = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )


        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithSameDateAndDiffSupplyID() {
        val date = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )


        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (2, '2025-01-02', 100.0000, 50.0000, 50.0000, false)",
            result
        )
    }

    @Test
    fun testCreateSupplyRecordWithOldDateAndSameSupplyID() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )


        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithOldDateAndDiffSupplyID() {
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")


        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            oldDate,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false
        )


        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (2, '2025-01-02', 100.0000, 50.0000, 50.0000, false)",
            result,
        )
    }

    @Test
    fun testCreateSupplyRecordWithZeroAddedOrZeroConsumed() {
        val dateOne = Date.valueOf("2025-02-02")
        val dateTwo = Date.valueOf("2025-02-03")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val resultOne = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            dateOne,
            BigDecimal("50.00"),
            BigDecimal("0.00"),
            false
        )

        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (1, '2025-02-02', 50.0000, 0.0000, 50.0000, false)",
            resultOne
        )

        val resultTwo = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            dateTwo,
            BigDecimal("0.00"),
            BigDecimal("50.00"),
            false
        )


        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (1, '2025-02-03', 0.0000, 50.0000, 0.0000, false)",
            resultTwo
        )
    }

    @Test
    fun testCreateSupplyRecordWithOnlyConsume() {
        val date = Date.valueOf("2025-02-03")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            13,
            date,
            BigDecimal("0.00"),
            BigDecimal("50.00"),
            false
        )


        assertNull(result)
    }

    @Test
    fun testCreateSupplyRecordWithZeroConsumedAndZeroAdded() {
        val date = Date.valueOf("2025-01-01")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("0.00"),
            BigDecimal("0.00"),
            false
        )

        assertEquals(
            result,
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (1, '2025-01-01', 0.0000, 0.0000, 0.0000, false)"
        )
    }

    @Test
    fun testCreateSupplyRecordWithNegativeInputs() {
        val date = Date.valueOf("2025-02-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val resultOne = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("10.00"),
            BigDecimal("-50.00"),
            false
        )

        assertNull(resultOne)

        val resultTwo = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("-50.00"),
            BigDecimal("10.00"),
            false
        )

        assertNull(resultTwo)

        val resultThree = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("-50.00"),
            BigDecimal("-10.00"),
            false
        )

        assertNull(resultThree)

    }

    @Test
    fun testCreateSupplyRecordWithFiveDecimals() {
        val date = Date.valueOf("2025-02-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val resultOne = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00005"),
            BigDecimal("50.00"),
            false
        )

        assertNull(resultOne)
    }

    @Test
    fun testCreateSupplyRecordWithZeroDecimalPlace() {
        val date = Date.valueOf("2025-01-01")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val resultOne = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100"),
            BigDecimal("50"),
            false
        )

        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved) VALUES (1, '2025-01-01', 100.0000, 50.0000, 50.0000, false)",
            resultOne
        )
    }
}
