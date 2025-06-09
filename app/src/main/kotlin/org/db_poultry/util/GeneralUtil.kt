package org.db_poultry.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

import javafx.scene.layout.AnchorPane

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
    }
}