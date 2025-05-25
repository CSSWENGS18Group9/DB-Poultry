package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.event.ActionEvent

import javafx.scene.control.Button
import javafx.scene.text.Text
import javafx.scene.control.TextField

class LoginController {

    @FXML
    private lateinit var btnSample: Button

    @FXML
    private lateinit var textSample: Text

    @FXML
    private lateinit var tfPassword: TextField

    @FXML
    private lateinit var tfUN: TextField
    
    @FXML
    fun switchToHome() {
        SceneSwitcher.switchTo(btnSample, "/home.fxml")
    }

}
