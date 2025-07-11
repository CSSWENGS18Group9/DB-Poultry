package org.db_poultry.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.scene.image.ImageView
import org.kordamp.ikonli.javafx.FontIcon

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Locale

class GeneralUtil {
    companion object {

        // Mapping of FXML paths to sections
        private val fxmlSectionMapping = mapOf(
            "/fxml/main_layout.fxml" to "HOME",
            "/fxml/content_home.fxml" to "HOME",
            "/fxml/login.fxml" to "HOME",
            "/fxml/content_create_flock_details.fxml" to "FLOCK",
            "/fxml/content_create_flock.fxml" to "FLOCK",
            "/fxml/content_create_new_flock.fxml" to "FLOCK",
            "/fxml/content_create_supplies.fxml" to "SUPPLIES",
            "/fxml/content_generate_report.fxml" to "FLOCK",
            "/fxml/content_home_flock.fxml" to "FLOCK",
            "/fxml/content_home_supplies_grid.fxml" to "SUPPLIES",
            "/fxml/content_home_supplies.fxml" to "SUPPLIES",
            "/fxml/content_update_supplies_add_delete.fxml" to "SUPPLIES",
            "/fxml/content_update_supplies_retrieve.fxml" to "SUPPLIES",
            "/fxml/content_view_date_history.fxml" to "SUPPLIES",
            "/fxml/content_view_flock_details.fxml" to "FLOCK",
            "/fxml/content_view_flock.fxml" to "FLOCK",
            "/fxml/content_view_supplies.fxml" to "SUPPLIES",
            "/fxml/content_view_supply_history.fxml" to "SUPPLIES",
        )

        private var currentSection: String? = null

        private var sectionChangeCallback: ((String) -> Unit)? = null

        private var inUseContentPane: AnchorPane? = null

        @JvmStatic
        fun registerSectionChangeCallback(callback: (String) -> Unit) {
            sectionChangeCallback = callback
        }

        @JvmStatic
        fun getCurrentSection(): String? = currentSection

        @JvmStatic
        fun showPopup(status: String, text: String) {
            val fxmlPath = when (status.lowercase()) {
                "success" -> "/fxml/popup/popup_success.fxml"
                "error" -> "/fxml/popup/popup_error.fxml"
                else -> throw IllegalArgumentException("Invalid status: $status")
            }

            val loader = FXMLLoader(GeneralUtil::class.java.getResource(fxmlPath))
            val root = loader.load<Parent>()

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
                    val bounds = inUseContentPane!!.localToScreen(inUseContentPane!!.boundsInLocal)

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

        @JvmStatic
        fun loadContentView(contentAnchorPane: AnchorPane, fxmlPath: String) {
            inUseContentPane = contentAnchorPane

            try {

                if (!fxmlSectionMapping.containsKey(fxmlPath)) { //DEBUGGING
                    println("Warning: FXML $fxmlPath is not mapped to any section")
                    return
                }

                if (contentAnchorPane.children.isNotEmpty()) {
                    val currentRoot = contentAnchorPane.children[0]
                    val currentFXML = currentRoot.properties["fxmlPath"]
                    if (currentFXML != null && currentFXML == fxmlPath) {
                        println("Already in $fxmlPath, not switching.")
                        return
                    }
                }

                println("Loading content view: $fxmlPath")

                val loader = FXMLLoader(GeneralUtil::class.java.getResource(fxmlPath))
                loader.controllerFactory = ControllerManager.controllerFactory
                val view = loader.load<Parent>()

                view.properties["fxmlPath"] = fxmlPath

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

                val newSection = fxmlSectionMapping[fxmlPath]!!
                if (currentSection != newSection) {
                    currentSection = newSection
                    sectionChangeCallback?.invoke(newSection)
                    println("Section changed to: $newSection")
                }

            } catch (e: Exception) {
                println("Error loading content view: $fxmlPath")
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun navigateToMainContent(currentPane: AnchorPane, fxmlPath: String) {

            val mainContentPane = findMainContentPane(currentPane)
            if (mainContentPane != null) {
                loadContentView(mainContentPane, fxmlPath)
            } else {
                println("Warning: Could not find main content pane from current view")
            }
        }

        @JvmStatic
        private fun findMainContentPane(currentPane: AnchorPane): AnchorPane? {
            // Traverse up the scene graph to find the main content pane
            var parent = currentPane.parent
            while (parent != null) {
                if (parent is AnchorPane && parent.id == "contentAnchorPane") {
                    return parent
                }
                parent = parent.parent
            }
            return null
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

        @JvmStatic
        fun initializeIconSizeManager(mainPane: Node, fontIcon: FontIcon, baseIconSize: Double = 50.0) {
            mainPane.sceneProperty().addListener { _, oldScene, newScene ->
                if (oldScene == null && newScene != null) {
                    val iconSize = SimpleDoubleProperty(0.0)
                    iconSize.bind(
                        newScene.widthProperty().add(newScene.heightProperty())
                            .divide(1280.0 + 720.0)
                            .multiply(baseIconSize)
                    )
                    fontIcon.iconSizeProperty().bind(iconSize)
                    fontIcon.wrappingWidthProperty().bind(iconSize)
                }
            }
        }

        @JvmStatic
        fun formatDatePretty(date: LocalDate?): String {
            val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
            return date?.format(formatter) ?: String.format("No date provided")
        } 
    }
}