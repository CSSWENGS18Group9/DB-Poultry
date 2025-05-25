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

class HomeController {

    @FXML
    private lateinit var btnBackToLogin: Button

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var tfHomeMenu: TextField

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToHome() {
        SceneSwitcher.switchTo(btnBackToLogin, "/login.fxml")
    }

}
