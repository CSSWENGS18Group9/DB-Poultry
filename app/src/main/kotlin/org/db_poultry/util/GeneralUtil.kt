package org.db_poultry.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.scene.image.ImageView

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty

class GeneralUtil {
    companion object {
        @JvmStatic
        fun loadContentView(contentAnchorPane: AnchorPane, fxmlPath: String) {
            try {
                println("Loading content view: $fxmlPath")

                val loader = FXMLLoader(GeneralUtil::class.java.getResource(fxmlPath))

                loader.controllerFactory = ControllerManager.controllerFactory
                val view = loader.load<Parent>()
                
                // DEBUGGING
                val controller = loader.getController<Any>()
                println("Controller for $fxmlPath: ${controller.javaClass.name} (${System.identityHashCode(controller)})")

                contentAnchorPane.children.clear()
                contentAnchorPane.children.add(view)
                
                

                // Set AnchorPane constraints to fill the entire area
                AnchorPane.setTopAnchor(view, 0.0)
                AnchorPane.setRightAnchor(view, 0.0)
                AnchorPane.setBottomAnchor(view, 0.0)
                AnchorPane.setLeftAnchor(view, 0.0)
                
            } catch (e: Exception) {
                println("Error loading content view: $fxmlPath")
                e.printStackTrace()
            }
        }

        /**
         * Initializes the font size manager for the main pane.
         * This will adjust the font size based on the scene dimensions.
         * mainPane should be the root of your layout.
         * Credits to https://stackoverflow.com/a/51948875
         */
        @JvmStatic
        fun initializeFontSizeManager(mainPane: Node) {
            mainPane.sceneProperty().addListener { _, oldScene, newScene ->
                if (oldScene == null && newScene != null) {
                    val fontSize = SimpleDoubleProperty(0.0)
                    fontSize.bind(
                        newScene.widthProperty().add(newScene.heightProperty())
                            .divide(1280.0 + 720.0)
                            .multiply(100)
                    )
                    mainPane.styleProperty().bind(
                        Bindings.concat("-fx-font-size: ", fontSize.asString("%.0f"), "%;")
                    )
                }
            }
        }

        @JvmStatic
        fun resizeImageViewToFit(mainNode: Node, toResizeImage: ImageView, widthMultiplier: Double = 0.15, heightMultiplier: Double = 0.2) {
            mainNode.sceneProperty().addListener { _, _, scene ->
                if (scene != null) {
                    toResizeImage.fitWidthProperty().bind(scene.widthProperty().multiply(widthMultiplier))
                    toResizeImage.fitHeightProperty().bind(scene.heightProperty().multiply(heightMultiplier))
                }
            }
        }
    }
}