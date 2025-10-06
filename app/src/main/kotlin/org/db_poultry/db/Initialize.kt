package org.db_poultry.db

import java.sql.Connection
import java.sql.SQLException
import org.db_poultry.errors.generateErrorMessage
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.use

fun cleanTables(conn: Connection?, databaseName: String) {
    if (conn == null) {
        generateErrorMessage("Error at `cleanTables()` in `Initialize.kt`",
            "Connection is null.",
            "Ensure a connection exists."
        )

        return
    }

    val tables = listOf(
        "DROP TABLE IF EXISTS Supply_Record",
        "DROP TABLE IF EXISTS Flock_Details",
        "DROP TABLE IF EXISTS Supply_Type",
        "DROP TABLE IF EXISTS Flock"
    )

    try {

        for (query in tables) {
            conn.createStatement().use { stmt ->
                stmt.execute(query)
            }
        }

    } catch (e: SQLException) {
        generateErrorMessage(
            "Error at `initTables()` in `Initialize.kt`",
            "Creating tables caused an error.",
            "",
            e
        )
    }

    conn.close()

    println("closed $databaseName database connection")

    val defaultConn = connectToDefault()

    println("connected to default database")

    if (defaultConn == null) {
        generateErrorMessage(
            "Error at `cleanTables()` in `Initialize.kt`.",
            "Default connection is null.",
            "Ensure valid connection exists."
        )
        return
    }

    val DBandUser = listOf(
        "DROP DATABASE IF EXISTS \"$databaseName\"",
        "DROP USER IF EXISTS \"$databaseName\""
    )

    try {

        for (query in DBandUser) {
            defaultConn.createStatement().use { stmt ->
                stmt.execute(query)
            }
        }

    } catch (e: SQLException) {
        generateErrorMessage(
            "Error at `initTables()` in `Initialize.kt`",
            "Creating tables caused an error.",
            "",
            e
        )
    }

    defaultConn.close()

}

fun cleanTablesTest(conn: Connection?) {
    if (conn == null) {
        generateErrorMessage("Error at `cleanTablesTest()` in `Initialize.kt`",
            "Connection is null.",
            "Ensure a connection exists."
        )

        return
    }

    val tables = listOf("Supply_Record", "Flock_Details", "Supply_Type", "Flock")

    val defaultSupplyTypes = listOf(
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('apog', 'ml', 'src/main/resources/img/supply-img/Apog.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('adulticide', 'ml', 'src/main/resources/img/supply-img/Adulticide.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('string', 'cm', 'src/main/resources/img/supply-img/String.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('fuel', 'l', 'src/main/resources/img/supply-img/Fuel.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('chicken medicine', 'bottles', 'src/main/resources/img/supply-img/Chicken_Medicine.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('larvicide', 'ml', 'src/main/resources/img/supply-img/Larvicide.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('fly glue', 'ml', 'src/main/resources/img/supply-img/Fly_Glue.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('disinfectant', 'ml', 'src/main/resources/img/supply-img/Disinfectant.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('starter feed', 'kg', 'src/main/resources/img/supply-img/Starter_Feed.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('grower feed', 'kg', 'src/main/resources/img/supply-img/Grower_Feed.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('booster feed', 'kg', 'src/main/resources/img/supply-img/Booster_Feed.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('finisher feed', 'kg', 'src/main/resources/img/supply-img/Finisher_Feed.png')",
    )

    try {
        for (table in tables) {
            conn.createStatement().use { stmt ->
                stmt.execute("TRUNCATE TABLE $table RESTART IDENTITY CASCADE;")
            }
        }

        for (query in defaultSupplyTypes) {
            conn.createStatement().use { stmt ->
                stmt.execute(query)
            }
        }

    } catch (e: SQLException) {
        generateErrorMessage(
            "Error at `cleanTablesTest()` in `Initialize.kt`",
            "Cleaning tables caused an error.",
            "",
            e
        )
    }
}

fun initDBAndUser(databasePass: String, databaseName: String) {

    val conn = connectToDefault()

    if (conn == null) {
        generateErrorMessage(
            "Error at `initDBAndUser()` in `Initialize.kt`.",
            "Default connection is null.",
            "Ensure valid connection exists."
        )
        return
    }

    // Create user and DB_POULTRY DB
    val initUserDB = listOf(
        "DROP DATABASE IF EXISTS \"$databaseName\";",
        "DROP USER IF EXISTS \"$databaseName\";",
        "CREATE USER \"$databaseName\" WITH PASSWORD '$databasePass'",
        "CREATE DATABASE \"$databaseName\" OWNER \"$databaseName\";",
        "GRANT ALL PRIVILEGES ON DATABASE \"$databaseName\" TO \"$databaseName\";"
    )

    try {
        conn.autoCommit = true

        // Create user and DB
        for (query in initUserDB) {
            conn.createStatement().use { stmt ->
                stmt.execute(query)
            }
        }

        conn.autoCommit = false

    } catch (e: SQLException) {
        generateErrorMessage(
            "Error at `initDBAndUser()` in `Initialize.kt`",
            "Creating user and/or database caused an error.",
            "",
            e
        )
    }

    conn.close()

}

fun connectToDefault(): Connection? {
    val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"

    print("connecting to local")

    DBConnect.init(jdbcUrl, "postgres", "password") // default

    val conn = DBConnect.getConnection() // connect to default DB

    return conn
}

fun initTables(conn: Connection?, databaseName: String) {

    if (conn == null) {
        generateErrorMessage(
            "Error at `initTables()` in `Initialize.kt`.",
            "Connection to $databaseName is null.",
            "Ensure valid connection exists."
        )
        return
    }

    // Create tables
    val databaseTables = linkedMapOf(
        "Flock" to """
            Flock_ID SERIAL PRIMARY KEY,
            Starting_Count INTEGER CHECK (Starting_Count > 0) NOT NULL,
            Starting_Date DATE UNIQUE NOT NULL
        """.trimIndent(),

        "Flock_Details" to """
            Flock_Details_ID SERIAL PRIMARY KEY,
            Flock_ID INTEGER NOT NULL,
            FD_Date DATE UNIQUE NOT NULL,
            Depleted_Count INTEGER CHECK (Depleted_Count >= 0),
            FOREIGN KEY (Flock_ID) REFERENCES Flock(Flock_ID) ON DELETE CASCADE
        """.trimIndent(),

        "Supply_Type" to """
            Supply_Type_ID SERIAL PRIMARY KEY,
            Supply_Name VARCHAR(36) UNIQUE NOT NULL CHECK (Supply_Name <> ''),
            Unit VARCHAR(12) NOT NULL CHECK (Unit <> ''),
            Image_File_Path VARCHAR(255)
        """.trimIndent(),

        "Supply_Record" to """            
            Supply_ID SERIAL PRIMARY KEY,
            Supply_Type_ID INT  NOT NULL,
            SR_Date DATE NOT NULL,
            Added NUMERIC(12, 4),
            Consumed NUMERIC(12, 4),
            Current_Count   NUMERIC(12, 4),
            Retrieved BOOLEAN,
            Price NUMERIC(12, 4),
            FOREIGN KEY (Supply_Type_ID) REFERENCES Supply_Type (Supply_Type_ID) ON DELETE CASCADE,
            UNIQUE (Supply_Type_ID, SR_Date)
        """.trimIndent(),
    )

    // Create indices
    val indexQueries = listOf(
        "CREATE INDEX idx_flock_R_starting_date ON Flock (Starting_Date);",

        "CREATE INDEX idx_flock_details_R_flockid_fddate ON Flock_Details (Flock_ID, FD_Date DESC);",

        "CREATE INDEX idx_flock_R_details_flockid ON Flock_Details (Flock_ID);"
    )

    // @dattebayo @megandasal @keishoo4
    // Take note of the default file paths of the images here
    // SPECIFIC FOR @megandasal
    // Feel free to adjust the unit measurements for each default supply
    val defaultSupplyTypes = listOf(
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('apog', 'ml', 'src/main/resources/img/supply-img/Apog.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('adulticide', 'ml', 'src/main/resources/img/supply-img/Adulticide.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('string', 'cm', 'src/main/resources/img/supply-img/String.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('fuel', 'l', 'src/main/resources/img/supply-img/Fuel.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('chicken medicine', 'bottles', 'src/main/resources/img/supply-img/Chicken_Medicine.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('larvicide', 'ml', 'src/main/resources/img/supply-img/Larvicide.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('fly glue', 'ml', 'src/main/resources/img/supply-img/Fly_Glue.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('disinfectant', 'ml', 'src/main/resources/img/supply-img/Disinfectant.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('starter feed', 'kg', 'src/main/resources/img/supply-img/Starter_Feed.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('grower feed', 'kg', 'src/main/resources/img/supply-img/Grower_Feed.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('booster feed', 'kg', 'src/main/resources/img/supply-img/Booster_Feed.png')",
        "INSERT INTO supply_type (supply_name, unit, image_file_path) VALUES ('finisher feed', 'kg', 'src/main/resources/img/supply-img/Finisher_Feed.png')",
    )

    try {
        // Create tables in original order
        for ((table, columns) in databaseTables) {
            val createQuery = "CREATE TABLE $table ($columns)"
            conn.createStatement().use { stmt ->
                stmt.execute(createQuery)
            }
        }

        // Create indices after tables are created
        for (query in indexQueries) {
            conn.createStatement().use { stmt ->
                stmt.execute(query)
            }
        }

        for (query in defaultSupplyTypes) {
            conn.createStatement().use { stmt ->
                stmt.execute(query)
            }
        }
    } catch (e: SQLException) {
        generateErrorMessage(
            "Error at `initTables()` in `Initialize.kt`",
            "Creating tables in $databaseName caused an error.",
            "",
            e
        )
    }
}

fun cleanAndInitTables(conn: Connection?, databaseName: String) {
    if (conn == null) {
        generateErrorMessage(
            "Error at `initTables()` in `Initialize.kt`.",
            "Connection to $databaseName is null.",
            "Ensure valid connection exists."
        )
        return
    }

    cleanTables(conn, databaseName)
    initTables(conn, databaseName)
}