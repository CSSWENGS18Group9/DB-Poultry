package org.db_poultry.controller

import org.db_poultry.Util.SceneSwitcher

import javafx.fxml.FXML
import javafx.event.ActionEvent

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
