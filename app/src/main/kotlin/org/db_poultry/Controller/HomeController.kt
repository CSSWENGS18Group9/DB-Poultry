package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class HomeController {

    @FXML
    private lateinit var btnBackToLogin: Button

    @FXML
    private lateinit var gotoCreate: Button

    @FXML
    private lateinit var gotoGenerate: Button

    @FXML
    private lateinit var gotoView: Button

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToHome(event: ActionEvent) {
        println("Switching to home view")
        SceneSwitcher.switchTo(btnBackToLogin, "/fxml/login.fxml")
    }

    @FXML
    fun switchToCreate(event: ActionEvent) {
        SceneSwitcher.switchContent(gotoCreate, "/fxml/content_create.fxml")
    }

    @FXML
    fun switchToGenerate(event: ActionEvent) {
        println("Switching to generate view")
        SceneSwitcher.switchContent(gotoGenerate, "/fxml/content_view.fxml")
    }

}