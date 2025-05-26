package org.db_poultry

import io.github.cdimascio.dotenv.Dotenv
import javafx.application.Application
import org.db_poultry.controller.recordFlock
import org.db_poultry.controller.recordFlockDetails
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.gui.MainFrame
import java.sql.Connection
import java.sql.Date

class App {
    lateinit var databaseName: String
    lateinit var databasePass: String
    lateinit var databasePort: String
    lateinit var databaseObjc: DBConnect

    fun getDotEnv(): Boolean {
        try {
            val dotenv = Dotenv.load()

            databaseName = (dotenv["DATABASE_NAME"] ?: "Missing DATABASE_NAME")
            databasePass = (dotenv["DATABASE_PASS"] ?: "Missing DATABASE_PASS")
            databasePort = (dotenv["DATABASE_PORT"] ?: "Missing DATABASE_PORT")

            println("database name:$databaseName")
            println("database pass:$databasePass")
            println("database port:$databasePort")

            return true
        } catch (e: Exception) {
            generateErrorMessage(
                "Error at `getDotEnv()` in `App.kt`",
                "Failed to load environment variables; not found",
                "If environment variables does exist make sure it is in `app/`. Otherwise, create one in `app/`.",
                e
            )

            return false
        }

    }

    fun openMainFrame(): Boolean {
        try {
            Application.launch(MainFrame::class.java)

            return true
        } catch (e: ClassNotFoundException) {
            generateErrorMessage(
                "Error at `openMainFrame()` in `App.kt`.",
                "Failed to open MainFrame GUI; class not found.",
                "Rename the `index` GUI to `MainFrame` and should be at the root folder of `Gui/`.", e
            )

            return false
        }
    }

    fun start() {
        if (!getDotEnv()) {
            generateErrorMessage(
                "Error at `start()` in `App.kt`.",
                "Dot env file has a missing variable.",
                "Check if the dot env file has DATABASE_NAME, DATABASE_PASS, and DATABASE_PORT"
            )

            return
        }

        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"

        // Connect to the PostgresSQL DB
        databaseObjc = DBConnect(
            jdbcUrl,
            databaseName,
            databasePass
        )
    }

    fun getConnection(): Connection? = databaseObjc.getConnection()
}

fun main() {
    val app = App()
    app.start()

    // Open MainFrame (index GUI)
    app.openMainFrame()



}
