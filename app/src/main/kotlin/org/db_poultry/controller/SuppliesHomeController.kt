package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane

class SuppliesHomeController {

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    fun navigateToMenu() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home.fxml")
    }

    @FXML
    fun navigateToCreate() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_create_supplies.fxml")
    }

    @FXML
    fun navigateToUpdate() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_home_supplies_grid.fxml")
    }

    @FXML
    fun navigateToView() {
        GeneralUtil.loadContentView(homeAnchorPane, "/fxml/content_view_supplies.fxml")
    }

}
