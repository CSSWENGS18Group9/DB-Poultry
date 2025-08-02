package org.db_poultry.controller.flock

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.FlockSingleton

// TODO: Remove this class and merge with FlockHomeController @Dattebayo2505
class CreateFlockMenuController {

    @FXML
    private lateinit var anchorPaneCreate: AnchorPane

    @FXML
    private fun navigateToCreateFlockDetails() {
        // GeneralUtil.navigateToMainContent(anchorPaneCreate, "/fxml/content_home_flock_grid.fxml")
        println("debugging")
        PopupUtil.showContentPopup("/fxml/content_create_new_flock.fxml")
        FlockSingleton.setCurrentFlockFXML("create_flock_details")
    }

    @FXML
    private fun navigateToCreateNewFlock() {
        // GeneralUtil.navigateToMainContent(anchorPaneCreate, "/fxml/content_create_new_flock.fxml")
        println("debugging")
        PopupUtil.showContentPopup("/fxml/content_create_new_flock.fxml")
    }
}
