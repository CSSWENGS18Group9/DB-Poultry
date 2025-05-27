package org.db_poultry.Controller

import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import javafx.scene.shape.Rectangle
import javafx.fxml.FXMLLoader
import javafx.scene.Parent

class CreateController {

    @FXML
    private lateinit var anchorPaneCreate: AnchorPane

    @FXML
    private lateinit var createHeader: Text

    @FXML
    private lateinit var insideFrame: Rectangle

    @FXML
    private fun navigateToCreateFlockDetails() {
        loadContentView("/fxml/content_createFlockDetails.fxml")
    }

    @FXML
    private fun navigateToCreateNewFlock() {
        loadContentView("/fxml/content_createNewFlock.fxml")
    }

    private fun loadContentView(fxmlPath: String) {
        try {
            val loader = FXMLLoader(javaClass.getResource(fxmlPath))
            // loader.controllerFactory = ControllerManager.controllerFactory
            val view = loader.load<Parent>()
            
            anchorPaneCreate.children.clear()
            anchorPaneCreate.children.add(view)
            
            // Set AnchorPane constraints to fill the entire area
            AnchorPane.setTopAnchor(view, 0.0)
            AnchorPane.setRightAnchor(view, 0.0)
            AnchorPane.setBottomAnchor(view, 0.0)
            AnchorPane.setLeftAnchor(view, 0.0)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
