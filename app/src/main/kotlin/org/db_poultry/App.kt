package org.db_poultry

import io.github.cdimascio.dotenv.Dotenv
import javafx.application.Application
import org.db_poultry.controller.MainFrame
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.perf.PerformanceTest
import org.db_poultry.theLifesaver.Backup.TL_checkLastBackupDate
import org.db_poultry.theLifesaver.TL.TL_firstOpen
import org.db_poultry.theLifesaver.Config.TL_loadConfig
import org.db_poultry.theLifesaver.TL.wipe
import java.sql.Connection

class App {
    lateinit var databaseName: String
    lateinit var databasePass: String
    lateinit var databasePort: String

    fun getDotEnv(): Boolean {
        try {
            val dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load()

            databaseName = (dotenv["DATABASE_NAME"] ?: "Missing DATABASE_NAME")
            databasePass = (dotenv["DATABASE_PASS"] ?: "Missing DATABASE_PASS")
            databasePort = (dotenv["DATABASE_PORT"] ?: "Missing DATABASE_PORT")

            println("database name: $databaseName")
            println("database pass: $databasePass")
            println("database port: $databasePort")

            return true
        } catch (e: Exception) {
            generateErrorMessage(
                "Error at `getDotEnv()` in `App.kt`",
                "Failed to load environment variables",
                "In local dev, ensure .env exists in `app/src/main/resources`.",
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
    }

    fun connect() {
        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"

        // Connect to the PostgresSQL DB
        DBConnect.init(jdbcUrl, databaseName, databasePass)
    }

    fun getConnection(): Connection? = DBConnect.getConnection()
}
// checks if the developers are the ones running the code
val __CLIENT_MODE: Boolean = false

// Do clean database. Should always be FALSE!
val __DO_WIPE: Boolean = false

// Do performance testing. Should always be FALSE!
val __DO_PERF: Boolean = true

fun main() {
    val app = App()
    app.start()

    if (__CLIENT_MODE and !__DO_PERF) {
        // Check if this is the first open
        val config = TL_loadConfig()
        if (config == null){
            TL_firstOpen(app)
            cleanTables(app.getConnection())
        } else {
            TL_checkLastBackupDate(config)
        }
    }

    app.connect()

    if (__CLIENT_MODE && !__DO_PERF) {
        // MAIN PROCESS OF THE APPLICATION
        // Open MainFrame (index GUI)
        app.openMainFrame()
    } else {
        // PERFORMANCE TESTING PROCESS OF THE APPLICATION
        PerformanceTest.runTest(app);
    }


    // ==================================================
    // Keep this here but remove before shipping or every release
    // ==================================================
    if (!__CLIENT_MODE && __DO_WIPE) {
        app.getConnection()?.close()
        wipe(app.databaseName)
    }
}
