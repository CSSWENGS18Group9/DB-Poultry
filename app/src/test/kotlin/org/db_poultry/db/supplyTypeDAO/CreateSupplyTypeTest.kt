package org.db_poultry.db.supplyTypeDAO

import org.db_poultry.App
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.sql.Connection
// Test class for CreateSupplyType
// This class tests the functionality of creating supply types in the database.
// Restrictions include:
// - Supply type names must be unique.
// - Supply type names cannot be empty.
// - Unit cannot be empty.

class CreateSupplyTypeTest {
    private var jdbcURL: String
    private var conn: Connection

    init {
        App.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:${App.databasePort}/${App.databaseName}"
        DBConnect.init(jdbcURL, App.databaseName, App.databasePass)
        conn = DBConnect.getConnection()!!
        cleanTables(conn)
    }

    @Test
    fun testCreateSupplyTypeValidInputs() {
        val result = CreateSupplyType.createSupplyType(conn, "feed", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        assertEquals("INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES('feed', 'kg', 'src/main/resources/img/supply-img/Apog.png')", result)
    }

    @Test
    fun testCreateSupplyTypeDNEImage() {
        val result = CreateSupplyType.createSupplyType(conn, "feedtwo", "kg", "src/main/resources/img/supply-img/Apog.jng", "src/main/resources/img/supply-img/default.png")
        assertEquals("INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES('feedtwo', 'kg', 'src/main/resources/img/supply-img/default.png')", result)
    }

    @Test
    fun testCreateSupplyTypeEmptyPath() {
        val result = CreateSupplyType.createSupplyType(conn, "feedthree", "kg", "", "src/main/resources/img/supply-img/default.png")
        assertEquals("INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES('feedthree', 'kg', 'src/main/resources/img/supply-img/default.png')", result)
    }

    @Test
    fun testCreateSupplyTypeWithSameName() {
        CreateSupplyType.createSupplyType(conn, "Water", "liter", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        val result = CreateSupplyType.createSupplyType(conn, "Water", "liter", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        assertNull(result)
    }

    @Test
    fun testCreateSupplyTypeWithEmptyName() {
        val result = CreateSupplyType.createSupplyType(conn, "", "kg", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        assertNull(result)
    }

    @Test
    fun testCreateSupplyTypeWithEmptyUnit() {
        val result = CreateSupplyType.createSupplyType(conn, "Water", "", "src/main/resources/img/supply-img/Apog.png", "src/main/resources/img/supply-img/default.png")
        assertNull(result)
    }
}

