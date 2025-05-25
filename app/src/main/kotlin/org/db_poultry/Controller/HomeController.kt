package org.db_poultry.Controller

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text

class HomeController {

    @FXML
    private lateinit var btnBackToLogin: Button

    @FXML
    private lateinit var gotoCreate: Button

    @FXML
    private lateinit var gotoGenerate: Button

    @FXML
    private lateinit var gotoView: Button

    @FXML
    private lateinit var homeAnchorPane: AnchorPane

    @FXML
    private lateinit var txtHomeMenu: Text

    @FXML
    fun switchToHome(event: ActionEvent) {

    }

    @FXML
    private fun navigateToCreate() {
        loadContentView("/content_create.fxml")
    }

    @FXML
    private fun navigateToView() {
        loadContentView("/content_view.fxml")
    }

    @FXML
    private fun navigateToGenerate() {
        loadContentView("/content_generate.fxml")
    }

    private fun loadContentView(fxmlPath: String) {
        val loader = javafx.fxml.FXMLLoader(javaClass.getResource(fxmlPath))
        val content = loader.load<AnchorPane>()
        homeAnchorPane.children.clear()
        homeAnchorPane.children.add(content)
    }
}