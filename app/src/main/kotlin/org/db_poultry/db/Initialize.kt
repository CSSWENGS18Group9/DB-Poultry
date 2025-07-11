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
            Supply_Name TEXT UNIQUE NOT NULL CHECK (Supply_Name <> ''),
            Unit VARCHAR(12) NOT NULL CHECK (Unit <> '')
        """.trimIndent(),

        "Supply_Record" to """            
            Supply_ID SERIAL PRIMARY KEY,
            Supply_Type_ID INT  NOT NULL,
            SR_Date DATE NOT NULL,
            Added NUMERIC(12, 4),
            Consumed NUMERIC(12, 4),
            Retrieved BOOLEAN,
            FOREIGN KEY (Supply_Type_ID) REFERENCES Supply_Type (Supply_Type_ID) ON DELETE CASCADE,
            UNIQUE (Supply_Type_ID, SR_Date)
        """.trimIndent(),
    )

    val indexQueries = listOf(
        "CREATE INDEX idx_flock_R_starting_date ON Flock (Starting_Date);",

        "CREATE INDEX idx_flock_details_R_flockid_fddate ON Flock_Details (Flock_ID, FD_Date DESC);",

        "CREATE INDEX idx_flock_R_details_flockid ON Flock_Details (Flock_ID);"
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

    } catch (e: SQLException) {
        generateErrorMessage(
            "Error at `cleanTables()` in `Initialize.kt`",
            "Cleaning tables caused an error.",
            "",
            e
        )
    }
}
