package org.db_poultry.db.supplyRecordDAO

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
    private val jdbcURL = "jdbc:postgresql://localhost:5432/db_poultry_test"
    private val conn: Connection

    init {
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
            false,
            BigDecimal("50.00")
        )
        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-01-01', 100.0000, 50.0000, 50.0000, false, 50.0000)",
            result
        )
    }

    // SUPPLY TYPE ID TESTS

    @Test
    fun testCreateSupplyRecordWithDNESupplyID() {
        val date = Date.valueOf("2025-01-02")

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            13,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )


        assertNull(result)
    }

    //DATE CONSTRAINT TESTS

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
            false,
            BigDecimal("50.00")
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
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
            false,
            BigDecimal("50.00")
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )


        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (2, '2025-01-02', 100.0000, 50.0000, 50.0000, false, 50.0000)",
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
            false,
            BigDecimal("50.00")
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
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
            false,
            BigDecimal("50.00")
        )

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            oldDate,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )


        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (2, '2025-01-02', 100.0000, 50.0000, 50.0000, false, 50.0000)",
            result,
        )
    }

    //PRICE CHANGE TEST

    fun testCreateSupplyRecordChangingPrice(){
        val date = Date.valueOf("2025-02-02")
        val oldDate = Date.valueOf("2025-01-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        CreateSupplyType.createSupplyType(conn, "Test_2", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            oldDate,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false,
            BigDecimal("50.00")
        )

        CreateSupplyRecord.createSupplyRecord(
            conn,
            2,
            date,
            BigDecimal("200.00"),
            BigDecimal("20.00"),
            false,
            BigDecimal("45.00")
        )


        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("45.00")
        )

        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-02-01', 100.0000, 50.0000, 50.0000, false, 45.0000)",
            result
        )

    }

    // ZERO INPUT TESTS

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
            false,
            BigDecimal("50.00")
        )

        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-02-02', 50.0000, 0.0000, 50.0000, false, 50.0000)",
            resultOne
        )

        val resultTwo = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            dateTwo,
            BigDecimal("0.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )


        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-02-03', 0.0000, 50.0000, 0.0000, false, 50.0000)",
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
            false,
            BigDecimal("50.00")
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
            false,
            BigDecimal("50.00")
        )

        assertEquals(
            result,
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-01-01', 0.0000, 0.0000, 0.0000, false, 50.0000)"
        )
    }


    // RETRIVED START TEST
    @Test
    fun testCreateSupplyRecordWithRetrievedStart() {
        val date = Date.valueOf("2025-01-01")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val result = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("10.00"),
            BigDecimal("20.00"),
            true,
            BigDecimal("50.00")
        )

        assertEquals(
            result,
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-01-01', 0.0000, 0.0000, 0.0000, true, 50.0000)"
        )
    }


    //NUMBER CONSTRAINT TEST

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
            false,
            BigDecimal("50.00")
        )

        assertNull(resultOne)

        val resultTwo = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("-50.00"),
            BigDecimal("10.00"),
            false,
            BigDecimal("50.00")
        )

        assertNull(resultTwo)

        val resultThree = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("50.00"),
            BigDecimal("10.00"),
            false,
            BigDecimal("-50.00")
        )

        assertNull(resultThree)

        val resultFour = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("-50.00"),
            BigDecimal("-10.00"),
            false,
            BigDecimal("-50.00")
        )

        assertNull(resultFour)

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
            false,
            BigDecimal("50.00")
        )

        assertNull(resultOne)

        val resultTwo = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00005"),
            false,
            BigDecimal("50.00")
        )

        assertNull(resultTwo)

        val resultThree = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00005")
        )

        assertNull(resultThree)

        val resultFour = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00005"),
            BigDecimal("50.00005"),
            false,
            BigDecimal("50.00005")
        )

        assertNull(resultFour)
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
            false,
            BigDecimal("50")
        )

        assertEquals(
            "INSERT INTO Supply_Record (Supply_Type_ID, SR_Date, Added, Consumed, Current_Count, Retrieved, Price) VALUES (1, '2025-01-01', 100.0000, 50.0000, 50.0000, false, 50.0000)",
            resultOne
        )
    }

    @Test
    fun testCreateSupplyRecordWithThirteenNonDecimalPlace(){
        val date = Date.valueOf("2025-02-02")

        CreateSupplyType.createSupplyType(conn, "Test_1", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")

        val resultOne = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("3210987654321.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("50.00")
        )

        assertNull(resultOne)

        val resultTwo = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("3210987654321.00"),
            false,
            BigDecimal("50.00")
        )

        assertNull(resultTwo)

        val resultThree = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("100.00"),
            BigDecimal("50.00"),
            false,
            BigDecimal("3210987654321.00")
        )

        assertNull(resultThree)

        val resultFour = CreateSupplyRecord.createSupplyRecord(
            conn,
            1,
            date,
            BigDecimal("3210987654321.0000005"),
            BigDecimal("3210987654321.0000005"),
            false,
            BigDecimal("3210987654321.0000005")
        )

        assertNull(resultFour)
    }




}
