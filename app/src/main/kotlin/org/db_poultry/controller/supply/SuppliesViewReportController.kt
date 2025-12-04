package org.db_poultry.controller.supply

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.layout.AnchorPane
import org.db_poultry.util.GeneralUtil

class SuppliesViewReportController: Initializable {

    @FXML
    lateinit var mainAnchorPane: AnchorPane

    override fun initialize(location: java.net.URL?, resources: java.util.ResourceBundle?) {

    }


    @FXML
    fun backToViewSupplies() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_home_supplies_grid.fxml")
    }


}