package org.db_poultry.db.flock

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.db_poultry.db.DBConnect
import org.db_poultry.App

class ViewTest {
    private var jdbcURL: String
    private var connection: DBConnect

    init {
        val app = App()

        app.getDotEnv()

        jdbcURL = "jdbc:postgresql://localhost:$app.databasePort/$app.databaseName"
        connection = DBConnect(jdbcURL, app.databaseName, app.databasePass)
    }
}