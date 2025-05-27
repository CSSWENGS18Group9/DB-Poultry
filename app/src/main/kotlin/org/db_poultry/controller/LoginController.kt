package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.event.ActionEvent
import javafx.scene.layout.AnchorPane

import javafx.scene.control.Button
import javafx.scene.text.Text
import javafx.scene.control.TextField

class LoginController {

    @FXML
    private lateinit var loginTitleText: Text

    @FXML
    private lateinit var passwordTextField: TextField

    @FXML
    private lateinit var usernameTextField: TextField

    @FXML
    private lateinit var signInBtn: Button
    
    @FXML
    fun switchToHome(event: ActionEvent) {
        println("Switching to home view")
        SceneSwitcher.switchTo(signInBtn, "/fxml/main_layout.fxml")
    }

}
