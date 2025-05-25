package org.db_poultry.Controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import javafx.scene.Parent
import javafx.scene.control.Label
import java.time.LocalDate

class MainLayoutController {

    @FXML
    private lateinit var borderPane: BorderPane

    @FXML
    private lateinit var dateLabel: Label
    
    // Initialize with home content
    fun initialize() {
        // Set today's date
        val today = LocalDate.now()
        dateLabel.text = today.toString()
        loadContentView("/content_home.fxml")
    }
    
    @FXML
    private fun navigateToHome() {
        loadContentView("/content_home.fxml")
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