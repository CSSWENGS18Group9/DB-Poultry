package org.db_poultry

import javafx.application.Application
import io.github.cdimascio.dotenv.Dotenv
import org.db_poultry.db.DBConnect
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.gui.MainFrame

import java.lang.ClassNotFoundException;

class App {
    private lateinit var databaseName: String
    private lateinit var databasePass: String
    private lateinit var databasePort: String

    private fun getDotEnv(): Boolean {
        try {
            val dotenv = Dotenv.load()

            databaseName = dotenv["DATABASE_NAME"] ?: error("Missing DATABASE_NAME")
            databasePass = dotenv["DATABASE_PASS"] ?: error("Missing DATABASE_PASS")
            databasePort = dotenv["DATABASE_PORT"] ?: error("Missing DATABASE_PORT")

            println("database name:$databaseName")
            println("database pass:$databasePass")
            println("database port:$databasePort")

            return true
        } catch (e: Exception) {
            generateErrorMessage(
                "Error at `getDotEnd() in `App.kt`",
                "Failed to load environment variables; not found",
                "If environment variables does exist make sure it is in `app/`. Otherwise, creare one in `app/`.",
                e
            )
            
            return false
        }

    }

    private fun openMainFrame(): Boolean {
        try {
            Application.launch(MainFrame::class.java)

            return true
        } catch (e: ClassNotFoundException) {
            generateErrorMessage(
                "Error at `openMainFrame()` in `App.kt`.",
                "Failed to open MainFrame GUI; class not found.",
                "Rename the `index` GUI to `MainFrame` and should be at the root folder of `Gui/`."
            )

            return false
        }
    }

    fun start() {
        if (!getDotEnv()) {
            println("Error at `start()` in `App.kt`")
            println("Environment (dot env) has a missing variable.")
            return
        }

        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"

        // Connect to the PostgreSQL DB
        DBConnect(
            databaseName,
            jdbcUrl,
            databaseName,
            databasePass
        )

        // Open MainFrame (index GUI)
        openMainFrame()
    }
}

fun main() {
    App().start()
}
