package org.db_poultry.gui

import org.db_poultry.Util.ControllerManager

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.stage.Stage
import javafx.scene.Parent
import javafx.scene.Scene

import javafx.scene.text.Font

class MainFrame : Application() {
    override fun start(primaryStage: Stage) {
        loadCustomFonts()

        try {
            val loader = FXMLLoader(javaClass.getResource("/fxml/login.fxml"))
            loader.controllerFactory = ControllerManager.controllerFactory
            val root = loader.load<Parent>()
            val mainScene = Scene(root)

            primaryStage.apply {
                scene = mainScene
                title = "DB Poultry"

                setMinWidth(600.0)
                setMinHeight(400.0)

                // setMaxWidth(600.0)
                // setMaxHeight(400.0)

                // setResizable(false)
                show()
            }

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