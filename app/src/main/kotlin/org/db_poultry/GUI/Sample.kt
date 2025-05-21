package org.db_poultry.GUI

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class Sample : Application() {
    override fun start(primaryStage: Stage) {
        val label = Label("Hello, World!")
        val root = StackPane(label)
        val scene = Scene(root, 300.0, 200.0)

        primaryStage.title = "JavaFX Kotlin Example"
        primaryStage.scene = scene
        primaryStage.show()
    }
}
