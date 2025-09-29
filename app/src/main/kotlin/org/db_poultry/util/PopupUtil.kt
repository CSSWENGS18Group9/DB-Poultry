package org.db_poultry.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

object PopupUtil {

    val inUseContentPane = GeneralUtil.getInUseContentPane()

    fun showPopup(status: String, text: String) {
        val fxmlPath = when (status.lowercase()) {
            "success" -> "/fxml/popup/popup_success.fxml"
            "error" -> "/fxml/popup/popup_error.fxml"
            else -> throw IllegalArgumentException("Invalid status: $status")
        }

        val loader = FXMLLoader(GeneralUtil::class.java.getResource(fxmlPath))
        val root = loader.load<Parent>()
        GUIUtil.applyDarkMode(root, GUIUtil.getDarkMode())

        // Set the popup text
        val textId = if (status.lowercase() == "success") "popupSuccessText" else "popupErrorText"
        val popupText = root.lookup("#$textId") as? Text
        popupText?.text = text

        val ownerStage = Stage.getWindows()
            .filterIsInstance<Stage>()
            .firstOrNull { it.isFocused }

        val stage = Stage()
        stage.initStyle(StageStyle.UNDECORATED)

        // Set the owner (this ensures the popup stays on top of the current window)
        if (ownerStage != null) {
            stage.initOwner(ownerStage)
        }

        stage.initModality(Modality.APPLICATION_MODAL)

        // Create scene with transparent background
        stage.initStyle(StageStyle.TRANSPARENT)
        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT
        stage.scene = scene

        stage.isResizable = false

        // Position the popup when it's shown
        stage.setOnShown {
            if (inUseContentPane != null) {
                // Get content pane's bounds in screen coordinates
                val bounds = inUseContentPane.localToScreen(inUseContentPane.boundsInLocal)

                // Calculate center position of the content pane
                val centerX = bounds.minX + bounds.width / 2
                val centerY = bounds.minY + bounds.height / 2

                // Position popup window with its center at the content pane's center
                stage.x = centerX - stage.width / 2
                stage.y = centerY - stage.height / 2
            } else {
                // Fallback to center on owner window if contentPane not provided
                stage.centerOnScreen()
            }
        }

        var dragOffsetX = 0.0
        var dragOffsetY = 0.0

        root.setOnMousePressed { event ->
            dragOffsetX = event.sceneX
            dragOffsetY = event.sceneY
        }

        root.setOnMouseDragged { event ->
            stage.x = event.screenX - dragOffsetX
            stage.y = event.screenY - dragOffsetY
        }

        stage.showAndWait()
    }

    fun showContentPopup(fxmlPath: String) {

        val loader = FXMLLoader(GeneralUtil::class.java.getResource(fxmlPath))
        val root = loader.load<Parent>()
        GUIUtil.applyDarkMode(root, GUIUtil.getDarkMode())

        val ownerStage = Stage.getWindows()
            .filterIsInstance<Stage>()
            .firstOrNull { it.isFocused }

        val stage = Stage()
        stage.initStyle(StageStyle.UNDECORATED)

        // Set the owner (this ensures the popup stays on top of the current window)
        if (ownerStage != null) {
            stage.initOwner(ownerStage)
        }

        stage.initModality(Modality.APPLICATION_MODAL)

        // Create scene with transparent background
        stage.initStyle(StageStyle.TRANSPARENT)
        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT
        stage.scene = scene

        stage.isResizable = false

        // Position the popup when it's shown
        stage.setOnShown {
            if (inUseContentPane != null) {
                // Get content pane's bounds in screen coordinates
                val bounds = inUseContentPane.localToScreen(inUseContentPane.boundsInLocal)

                // Calculate center position of the content pane
                val centerX = bounds.minX + bounds.width / 2
                val centerY = bounds.minY + bounds.height / 2

                // Position popup window with its center at the content pane's center
                stage.x = centerX - stage.width / 2
                stage.y = centerY - stage.height / 2
            } else {
                // Fallback to center on owner window if contentPane not provided
                stage.centerOnScreen()
            }
        }

        var dragOffsetX = 0.0
        var dragOffsetY = 0.0

        root.setOnMousePressed { event ->
            dragOffsetX = event.sceneX
            dragOffsetY = event.sceneY
        }

        root.setOnMouseDragged { event ->
            stage.x = event.screenX - dragOffsetX
            stage.y = event.screenY - dragOffsetY
        }

        stage.showAndWait()
    }


}