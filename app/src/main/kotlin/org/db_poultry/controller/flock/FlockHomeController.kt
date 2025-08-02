package org.db_poultry.controller.flock

import org.db_poultry.util.GeneralUtil

import javafx.fxml.Initializable
import java.util.ResourceBundle
import java.net.URL

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import org.db_poultry.util.FlockSingleton

// TODO: Remove this and transfer to FlockGridHomeController @Dattebayo2505
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
        FlockSingleton.setCurrentFlockFXML("view_flock_details")
    }

    @FXML 
    fun navigateToGenerate() {
        GeneralUtil.navigateToMainContent(flockHomeAnchorPane, "/fxml/content_view_flock.fxml")
        FlockSingleton.setCurrentFlockFXML("flock_generate_reports")
    }

}
