package org.db_poultry.GUI

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.stage.Stage
import javafx.scene.Parent
import javafx.scene.Scene

import javafx.scene.text.Font

class Sample : Application() {
    override fun start(primaryStage: Stage) {
        loadCustomFonts()

        try {
            val loader = FXMLLoader(javaClass.getResource("/fxml/login.fxml"))
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

private fun loadCustomFonts() {
    val fontFiles = listOf(
        "HelveticaNowDisplay-Black.ttf",
        "HelveticaNowDisplay-Bold.ttf",
        "HelveticaNowDisplay-BoldIta.ttf",
        "HelveticaNowDisplay-ExtBdIta.ttf",
        "HelveticaNowDisplay-ExtBlk.ttf",
        "HelveticaNowDisplay-ExtBlkIta.ttf",
        "HelveticaNowDisplay-ExtLt.ttf",
        "HelveticaNowDisplay-ExtraBold.ttf",
        "HelveticaNowDisplay-Hairline.ttf",
        "HelveticaNowDisplay-HairlineIta.ttf",
        "HelveticaNowDisplay-Light.ttf",
        "HelveticaNowDisplay-LightIta.ttf",
        "HelveticaNowDisplay-MedIta.ttf",
        "HelveticaNowDisplay-Medium.ttf",
        "HelveticaNowDisplay-RegIta.ttf",
        "HelveticaNowDisplay-Regular.ttf",
        "HelveticaNowDisplay-Thin.ttf",
        "HelveticaNowDisplay-ThinIta.ttf"
    )
    for (fontFile in fontFiles) {
        Font.loadFont(javaClass.getResourceAsStream("/fonts/$fontFile"), 15.0)
    }
}

}
