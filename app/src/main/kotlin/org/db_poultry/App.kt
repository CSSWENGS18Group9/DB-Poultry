package org.db_poultry

import io.github.cdimascio.dotenv.Dotenv
import javafx.application.Application
import org.db_poultry.controller.MainFrame
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables
import org.db_poultry.db.initDBAndUser
import org.db_poultry.db.initTables
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.theLifesaver.Backup
import org.db_poultry.theLifesaver.Config.TL_loadConfig
import org.db_poultry.theLifesaver.ENV
import org.db_poultry.theLifesaver.TL.TL_firstOpen
import org.db_poultry.theLifesaver.TL.wipe
import org.db_poultry.theLifesaver.Variables
import java.nio.file.Files
import java.sql.Connection

object App {
    lateinit var databaseName: String
    lateinit var databasePass: String
    lateinit var databasePort: String

    /**
     * Check if database credentials are initialized
     */
    fun areCredentialsInitialized(): Boolean {
        return ::databaseName.isInitialized && ::databasePass.isInitialized && ::databasePort.isInitialized
    }

    /**
     * Check if this is the first time running the application
     * by checking if .env file exists
     */
    fun isFirstRun(): Boolean {
        val envPath = Variables.getENVFilePath()
        return !Files.exists(envPath)
    }

    fun getDotEnv(): Boolean {
        val envPath = Variables.getENVFilePath()
        if (!Files.exists(envPath)) { // if .env does not exist
            return false
        }

        try {
            val dotenv = Dotenv.configure()
                .directory(envPath.parent.toString()) // .db_poultry folder
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
                "Ensure .env exists in `\"Username\"/.db_poultry`.",
                e
            )
            return false
        }
    }

    fun fillDatabasePasswordName(inputPassword: String, inputName: String) {
        databasePass = inputPassword
        databaseName = inputName.lowercase()
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

    fun start(inputPassword: String, inputUsername: String) {
        // Only create .env if it doesn't exist (first run)
        val envPath = Variables.getENVFilePath()
        if (!Files.exists(envPath)) {
            println(".env not found. creating")
            ENV.makeENVfile() // create the .env file
            ENV.writeENVfile(inputPassword, inputUsername)
            getDotEnv()
        }
    }

    fun connect() {
        val jdbcUrl = "jdbc:postgresql://localhost:$databasePort/$databaseName"
        DBConnect.init(jdbcUrl, databaseName, databasePass)
        println("Connected to: ${DBConnect.getConnection()}")
    }

    fun getConnection(): Connection? = DBConnect.getConnection()

    /**
     * Initialize database and connection for FIRST RUN
     */
    fun initializeApplication() {
        start(databasePass, databaseName)
        println("Initialized DB and User with values $databaseName and $databasePass")

        var config: HashMap<String, String>? = null
        if (__CLIENT_MODE)
            config = TL_loadConfig()
            if (config == null)
                TL_firstOpen(this)

        if (config == null) {
            initDBAndUser(databasePass, databaseName)
            println("Initialized DB and User with values $databaseName and $databasePass")
            connect()
            println("Connected to $databaseName")
            initTables(getConnection(), databaseName)
            println("Initialized tables")
        }

        println("passed initialization")
        connect()
        println("Connected to $databaseName\n")

        println("=== First Run Initialization Complete ===")
    }

    /**
     * Initialize database connection for SUBSEQUENT RUNS
     * THIS IS THE MAINEST METHOD
     */
    fun initializeSubsequentRun() {
        if (!getDotEnv()) {
            println("MAJOR ERROR: .env MISSING | FIX IMMEDIATELY")
            return
        }

        connect()

        var config: HashMap<String, String>? = null
        if (__CLIENT_MODE)
            config = TL_loadConfig()
            Backup.TL_checkLastBackupDate(config, databaseName, databasePass)
    }

    /**
     * Wipe database (for development only)
     */
    fun wipeDatabase() {
        if (__DO_WIPE) {
            println("!!! WIPING DATABASE !!!")
            getConnection()?.close()
            cleanTables(getConnection(), databaseName)
            wipe(databaseName)
        }
    }
}

// checks if the developers are the ones running the code
val __CLIENT_MODE: Boolean = true

// Do clean database. Should always be FALSE!
val __DO_WIPE: Boolean = false

fun main() {
    println("=== DB Poultry Application Starting ===")

    if (App.isFirstRun()) {
        // First run: Launch GUI first, then initialize after user enters credentials
        App.openMainFrame()
    } else { // Existing installation detected - database will connect during GUI initialization
        App.getDotEnv()
        App.wipeDatabase() // Only if __DO_WIPE is true (development only)
        App.openMainFrame()
    }
}
