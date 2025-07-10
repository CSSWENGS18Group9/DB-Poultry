package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import org.db_poultry.controller.backend.CurrentFlockInUse

// TODO: Remove this class and merge with FlockHomeController @Dattebayo2505
class CreateFlockMenuController {

    @FXML
    private lateinit var anchorPaneCreate: AnchorPane

    @FXML
    private fun navigateToCreateFlockDetails() {
        GeneralUtil.navigateToMainContent(anchorPaneCreate, "/fxml/content_view_flock.fxml")
        CurrentFlockInUse.setCurrentFlockFXML("create_flock_details")
    }

    @FXML
    private fun navigateToCreateNewFlock() {
        GeneralUtil.navigateToMainContent(anchorPaneCreate, "/fxml/content_create_new_flock.fxml")
    }
}
