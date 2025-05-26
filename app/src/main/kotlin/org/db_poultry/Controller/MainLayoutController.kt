package org.db_poultry.Controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import javafx.scene.Parent

import javafx.scene.control.Button
import javafx.scene.control.Label
import java.time.LocalDate

class MainLayoutController {

    @FXML
    private lateinit var borderPane: BorderPane

    @FXML
    private lateinit var sidebarHomeBtn: Button

    @FXML
    private lateinit var sidebarCreateBtn: Button

    @FXML
    private lateinit var sidebarViewBtn: Button

    @FXML
    private lateinit var sidebarGenerateBtn: Button

    @FXML
    private lateinit var sideBarDateLabel: Label
    
    // Initialize with home content
    fun initialize() {
        // Set today's date
        val today = LocalDate.now()
        sideBarDateLabel.text = today.toString()
        loadContentView("/fxml/content_home.fxml")
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

    // @FXML
    // private fun navigateToGenerate() {
    //     loadContentView("/fxml/content_generate.fxml")
    // }
    
    // Load content into the center area of BorderPane
    private fun loadContentView(fxmlPath: String) {
        try {
            val loader = FXMLLoader(javaClass.getResource(fxmlPath))
            val view = loader.load<Parent>()
            borderPane.center = view
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}