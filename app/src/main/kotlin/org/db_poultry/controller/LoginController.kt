package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil
import org.db_poultry.App
import org.db_poultry.util.GUIUtil

import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField

class LoginController: Initializable {

    @FXML
    private lateinit var loginAnchorPane: AnchorPane

    @FXML
    private lateinit var userLoginLabel: Label

    @FXML
    private lateinit var loginButton: Button

    @FXML
    private lateinit var usernameTextField: TextField

    @FXML
    private lateinit var incorrectUsernameLabel: Label

    @FXML
    private lateinit var passPasswordField: PasswordField

    @FXML
    private lateinit var passTextField: TextField

    @FXML
    private lateinit var showPassButton: Button

    @FXML
    private lateinit var incorrectPassLabel: Label

    private lateinit var username: String
    private lateinit var password: String

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        GeneralUtil.initializeFontSizeManager(loginAnchorPane)

        // Apply the saved dark mode setting
        GUIUtil.applyDarkMode(loginAnchorPane, GUIUtil.getDarkMode())

        if(GUIUtil.getDarkMode()) { userLoginLabel.styleClass.add("login-subtitle-dark") }
        else { userLoginLabel.styleClass.add("login-subtitle") }

        GUIUtil.setupPassword(passTextField, passPasswordField, showPassButton)
        incorrectPassLabel.isVisible = false
        incorrectUsernameLabel.isVisible = false

        loginButton.setOnAction { handleLogin() }

        val hideIfEmpty = { _: Any, _: String, newValue: String ->
            if (newValue.isEmpty()) {
                incorrectPassLabel.isVisible = false
            }
        }
        passPasswordField.textProperty().addListener(hideIfEmpty)
        passTextField.textProperty().addListener(hideIfEmpty)
    }

    private fun handleLogin() {
        username = usernameTextField.text.trim()
        password = if (passPasswordField.isVisible) passPasswordField.text else passTextField.text

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            if (username.isEmpty()) {
                incorrectUsernameLabel.isVisible = true
                incorrectUsernameLabel.text = "Username cannot be empty."
            }
            if (password.isEmpty()) {
                incorrectPassLabel.isVisible = true
                incorrectPassLabel.text = "Password cannot be empty."
            }
            return
        }

        // Check if this is the first run
        if (App.isFirstRun()) {
            // First run: Set up database credentials using DatabasePasswordNameController
            println("First run detected. Setting up database credentials.")
            try {
                DatabasePasswordNameController.setupDatabaseCredentials(password, username)
                incorrectUsernameLabel.isVisible = false
                incorrectPassLabel.isVisible = false
                switchToHome()
            } catch (e: Exception) {
                incorrectPassLabel.isVisible = true
                incorrectPassLabel.text = "Failed to initialize database. Check logs."
                e.printStackTrace()
            }
        } else {
            println("Verifying credentials against .env values.")

            if (!App.areCredentialsInitialized()) {
                incorrectPassLabel.isVisible = true
                incorrectPassLabel.text = "Error: Database credentials not loaded."
                println("ERROR: Credentials not initialized!")
                return
            }

            // Verify username and password match the .env values
            val isUsernameValid = username.equals(App.databaseName, ignoreCase = true)
            val isPasswordValid = password == App.databasePass

            if (isUsernameValid && isPasswordValid) {
                incorrectUsernameLabel.isVisible = false
                incorrectPassLabel.isVisible = false
                switchToHome()
            } else {
                if (!isUsernameValid) {
                    incorrectUsernameLabel.isVisible = true
                    incorrectUsernameLabel.text = "Incorrect username."
                }
                if (!isPasswordValid) {
                    incorrectPassLabel.isVisible = true
                    incorrectPassLabel.text = "Incorrect password."
                }
            }
        }
    }

    @FXML
    fun switchToHome() {
        println("Switching to home view")
        SceneSwitcher.switchTo("/fxml/main_layout.fxml")
    }

    /**
    * DEV MODE ONLY: Skip login and directly go to home view
    * Delete in Prod
    */
    @FXML
    fun skipLogin() {
        println("SKIP LOGIN (DEV MODE) - bypassing authentication")

        if (App.isFirstRun()) {
            try {
                DatabasePasswordNameController.setupDatabaseCredentials("db_poultry", "db_poultry")
            } catch (e: Exception) {
                println("Failed to initialize database: ${e.message}")
                e.printStackTrace()
                return
            }
        }
        switchToHome()
    }

}
