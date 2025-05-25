package org.db_poultry.GUI

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.stage.Stage
import javafx.scene.Parent
import javafx.scene.Scene

class Sample : Application() {
    override fun start(primaryStage: Stage) {
        try {
            val loader = FXMLLoader(javaClass.getResource("/login.fxml"))
            val root = loader.load<Parent>()
            val scene = Scene(root)
            primaryStage.scene = scene
            primaryStage.title = "JavaFX SceneBuilder Demo"
            primaryStage.show()

            println("JavaFX application started successfully.")
        } catch (e: Exception) {
            println("Error loading FXML file:\n")
            e.printStackTrace()
        }
    }
}
