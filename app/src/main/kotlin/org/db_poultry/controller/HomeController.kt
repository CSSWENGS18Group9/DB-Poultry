package org.db_poultry.Controller

import org.db_poultry.Util.SceneSwitcher

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class HomeController {

    @FXML
    private lateinit var homeReturnLoginBtn: Button

    @FXML
    private lateinit var homeCreateBtn: Button

    @FXML
    private lateinit var homeViewBtn: Button

    @FXML
    private lateinit var homeGenerateBtn: Button

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToLogin(event: ActionEvent) {
        println("Switching to login")
        SceneSwitcher.switchTo(homeReturnLoginBtn, "/fxml/login.fxml")
    }

    @FXML
    private fun navigateToHome() {
        loadContentView("/fxml/content_home.fxml")
    }
    
    @FXML
    private fun navigateToCreate() {
        loadContentView("/fxml/content_create.fxml")
    }
    
    @FXML
    private fun navigateToView() {
        loadContentView("/fxml/content_view.fxml")
    }

    private fun loadContentView(fxmlPath: String) {
        try {
            val loader = FXMLLoader(javaClass.getResource(fxmlPath))
            // loader.controllerFactory = ControllerManager.controllerFactory
            val view = loader.load<Parent>()
            
            homeAnchorPane.children.clear()
            homeAnchorPane.children.add(view)
            
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