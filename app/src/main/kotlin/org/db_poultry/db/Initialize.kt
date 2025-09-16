package org.db_poultry.db

import java.sql.Connection
import java.sql.SQLException
import org.db_poultry.errors.generateErrorMessage
fun cleanTables(conn: Connection?) {
    if (conn == null) {
        generateErrorMessage(
            "Error at `cleanTables()` in `Initialize.kt`.",
            "Connection is null.",
            "Ensure valid connection is given."
        )
        return
    }

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
        // Drop tables in reverse order
        for (table in databaseTables.keys.reversed()) {
            val dropQuery = "DROP TABLE IF EXISTS $table CASCADE"
            conn.createStatement().use { stmt ->
                stmt.execute(dropQuery)
            }
        }

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
            "Error at `cleanTables()` in `Initialize.kt`",
            "Cleaning tables caused an error.",
            "",
            e
        )
    }
}
