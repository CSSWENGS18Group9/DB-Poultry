package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.fxml.Initializable
import java.util.ResourceBundle
import java.net.URL

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class FlockHomeController: Initializable {

    @FXML
    private lateinit var flockHomeAnchorPane: AnchorPane

    @FXML
    private lateinit var homeFlockBtn1: Button

    @FXML
    private lateinit var homeFlockBtn2: Button

    @FXML
    private lateinit var homeFlockBtn3: Button

    @FXML
    private lateinit var txtHomeMenu1: Text

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {

        GeneralUtil.initializeFontSizeManager(flockHomeAnchorPane)

    }

    @FXML
    fun goToCreate(event: ActionEvent) {
        GeneralUtil.loadContentView(flockHomeAnchorPane, "/fxml/content_create.fxml")
    }

    @FXML
    fun goToView(event: ActionEvent) {
        GeneralUtil.loadContentView(flockHomeAnchorPane, "/fxml/content_view.fxml")
    }

    @FXML
    fun switchToMenu(event: ActionEvent) {
        GeneralUtil.loadContentView(flockHomeAnchorPane, "/fxml/content_home.fxml")
    }

    @FXML 
    fun switchToGenerate() {
        GeneralUtil.loadContentView(flockHomeAnchorPane, "/fxml/content_generate_report.fxml")
    }

}
