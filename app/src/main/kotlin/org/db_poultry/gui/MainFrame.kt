package org.db_poultry.gui

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.fxml.FXMLLoader
import javafx.scene.Parent


class MainFrame : Application() {
    override fun start(primaryStage: Stage) {
        try {
            val loader = FXMLLoader(javaClass.getResource("/dbpoultry.fxml"))
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
