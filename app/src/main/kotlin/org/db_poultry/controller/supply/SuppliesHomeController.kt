package org.db_poultry.controller.supply

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane

// TODO: Deprecate soon, use SuppliesGridHomeController as main supply page @Dattebayo25
class SuppliesHomeController {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    fun navigateToHome() {
        GeneralUtil.navigateToMainContent(homeAnchorPane, "/fxml/content_home.fxml")
    }

    @FXML
    fun navigateToCreate() {
        GeneralUtil.navigateToMainContent(homeAnchorPane, "/fxml/content_create_supplies.fxml")
    }

    @FXML
    fun navigateToUpdate() {
        GeneralUtil.navigateToMainContent(homeAnchorPane, "/fxml/content_home_supplies_grid.fxml")
    }

    @FXML
    fun navigateToView() {
        GeneralUtil.navigateToMainContent(homeAnchorPane, "/fxml/content_view_supplies.fxml")
    }

}
