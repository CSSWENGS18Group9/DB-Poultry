package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane

class CreateFlockMenuController {

    @FXML
    private lateinit var anchorPaneCreate: AnchorPane

    @FXML
    private fun navigateToCreateFlockDetails() {
        GeneralUtil.navigateToMainContent(anchorPaneCreate, "/fxml/content_create_flock_details.fxml")
    }

    @FXML
    private fun navigateToCreateNewFlock() {
        GeneralUtil.navigateToMainContent(anchorPaneCreate, "/fxml/content_create_new_flock.fxml")
    }
}
