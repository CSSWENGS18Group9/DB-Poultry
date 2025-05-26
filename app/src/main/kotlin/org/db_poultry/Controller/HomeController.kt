package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class HomeController {

    @FXML
    private lateinit var homeReturnLoginBtn: Button

    @FXML
    private lateinit var homeCreateBtn: Button

    @FXML
    private lateinit var homeViewBtn: Button

    @FXML
    private lateinit var homeGenerateBtn: Button

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToLogin(event: ActionEvent) {
        println("Switching to login")
        SceneSwitcher.switchTo(homeReturnLoginBtn, "/fxml/login.fxml")
    }

    @FXML
    fun switchToCreate(event: ActionEvent) {
        println("Switching to create view")
        SceneSwitcher.switchContent(homeCreateBtn, "/fxml/content_create.fxml")
    }

    @FXML
    fun switchToView(event: ActionEvent) {
        println("Switching to view content")
        SceneSwitcher.switchContent(homeViewBtn, "/fxml/content_view.fxml")
    }

    @FXML
    fun switchToGenerate(event: ActionEvent) {
        println("Switching to generate view")
        // SceneSwitcher.switchContent(homeGenerateBtn, "/fxml/content_view.fxml")
    }

}