package org.db_poultry.controller

import org.db_poultry.Util.GeneralUtil

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import javafx.scene.shape.Rectangle
import javafx.scene.control.Button

class CreateController {

    @FXML
    private lateinit var anchorPaneCreate: AnchorPane

    @FXML
    private lateinit var createHeader: Text

    @FXML
    private lateinit var createNewFlockBtn: Button

    @FXML
    private lateinit var createFlockDetailsBtn: Button

    @FXML
    private lateinit var insideFrame: Rectangle

    @FXML
    private fun navigateToCreateFlockDetails() {
        GeneralUtil.loadContentView(anchorPaneCreate, "/fxml/content_create_flock_details.fxml")
    }

    @FXML
    private fun navigateToCreateNewFlock() {
        GeneralUtil.loadContentView(anchorPaneCreate, "/fxml/content_create_new_flock")
    }
}
