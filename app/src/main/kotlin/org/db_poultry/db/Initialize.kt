package org.db_poultry.db

import org.db_poultry.errors.generateErrorMessage
import java.sql.Connection
import java.sql.SQLException

fun cleanTables(conn: Connection) {
    // The names of our tables in the database
    val databaseTables = HashMap<String, String>()

    databaseTables["Flock"] = """
    Flock_ID SERIAL PRIMARY KEY,
    Starting_Count INTEGER CHECK (Starting_Count > 0) NOT NULL,
    Starting_Date DATE UNIQUE NOT NULL
    """.trimIndent()

    databaseTables["Flock_Details"] = """
    Flock_Details_ID SERIAL PRIMARY KEY,
    Flock_ID INTEGER NOT NULL,
    FD_Date DATE UNIQUE NOT NULL,
    Depleted_Count INTEGER CHECK (Depleted_Count >= 0),
    FOREIGN KEY (Flock_ID) REFERENCES Flock(Flock_ID) ON DELETE CASCADE
    """.trimIndent()

    // add future tables here

    for ((table, columns) in databaseTables) {
        try {
            val dropQuery = "DROP TABLE IF EXISTS $table"
            val createQuery = "CREATE TABLE $table ($columns)"

            conn.createStatement().use { stmt ->
                stmt.execute(dropQuery)
                stmt.execute(createQuery)
            }
        } catch (e: SQLException) {
            generateErrorMessage(
                "Error at `cleanTables` in `Initialize.kt`",
                "Cleaning table $table caused an error.",
                "",
                e)
        }
    }
}
