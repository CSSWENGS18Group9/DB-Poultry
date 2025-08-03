package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.FXML
import javafx.event.ActionEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import org.db_poultry.App
import org.db_poultry.util.GUIUtil

class LoginController: Initializable {

    @FXML
    private lateinit var loginAnchorPane: AnchorPane

    @FXML
    private lateinit var loginButton: Button

    @FXML
    private lateinit var passPasswordField: PasswordField

    @FXML
    private lateinit var passTextField: TextField

    @FXML
    private lateinit var showPassButton: Button

    @FXML
    private lateinit var incorrectPassLabel: Label

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        GeneralUtil.initializeFontSizeManager(loginAnchorPane)

        GUIUtil.setupPassword(passTextField, passPasswordField, showPassButton)
        incorrectPassLabel.isVisible = false
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
        val password = if (passPasswordField.isVisible) passPasswordField.text else passTextField.text

        if (password.isEmpty()) {
            incorrectPassLabel.isVisible = false
        } else if (password != "correct_password") {
            incorrectPassLabel.isVisible = true
            incorrectPassLabel.text = "Incorrect password. Please try again."
        } else {
            incorrectPassLabel.isVisible = false
            switchToHome()
        }

        if (password == App.databasePass) {
            println("Login successful")
            switchToHome()
        } else {
            println("Login failed")
            incorrectPassLabel.isVisible = true
            incorrectPassLabel.text = "Incorrect password. Please try again."
        }
    }

    @FXML
    fun switchToHome() {
        println("Switching to home view")
        SceneSwitcher.switchTo("/fxml/main_layout.fxml")
    }

}
