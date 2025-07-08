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
import org.db_poultry.controller.backend.CurrentFlockInUse

class FlockHomeController: Initializable {

    @FXML
    private lateinit var flockHomeAnchorPane: AnchorPane

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {

    }

    @FXML
    fun navigateToHome() {
        GeneralUtil.navigateToMainContent(flockHomeAnchorPane, "/fxml/content_home.fxml")
    }

    @FXML
    fun navigateToCreate() {
        GeneralUtil.navigateToMainContent(flockHomeAnchorPane, "/fxml/content_create_flock.fxml")
    }

    @FXML
    fun navigateToView() {
        GeneralUtil.navigateToMainContent(flockHomeAnchorPane, "/fxml/content_view_flock.fxml")
        CurrentFlockInUse.setCurrentFlockFXML("view_flock_details")
    }

    @FXML 
    fun navigateToGenerate() {
        GeneralUtil.navigateToMainContent(flockHomeAnchorPane, "/fxml/content_view_flock.fxml")
        CurrentFlockInUse.setCurrentFlockFXML("flock_generate_reports")
    }

}
