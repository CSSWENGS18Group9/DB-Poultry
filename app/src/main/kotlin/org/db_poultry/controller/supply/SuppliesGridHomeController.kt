package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.pojo.SupplyPOJO.SupplyType
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.SupplySingleton

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.fxml.Initializable
import javafx.scene.layout.TilePane
import org.db_poultry.util.PopupUtil
import java.io.File
import java.net.URL
import java.util.ResourceBundle
import kotlin.text.compareTo

class SuppliesGridHomeController: Initializable {

    @FXML
    private lateinit var mainAnchorPane: AnchorPane

    @FXML
    private lateinit var mainTilePane: TilePane

    @FXML
    private lateinit var currentAmountLabel: Label
    
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        loadSupplyGrid()
    }

    private fun loadSupplyGrid() {
        val supplyTypeList = ReadSupplyType.getSupplyTypeAscending(getConnection())

        // Refer to Initialize.kt for the feed type naming
        if (supplyTypeList != null && supplyTypeList.isNotEmpty()) {
            val feedTypes = listOf("starter feed", "grower feed", "booster feed", "finisher feed")

            val sortedList = supplyTypeList.sortedWith { supply1, supply2 ->
                val feedIndex1 = feedTypes.indexOf(supply1.name.lowercase())
                val feedIndex2 = feedTypes.indexOf(supply2.name.lowercase())
                val isFeed1 = feedIndex1 != -1
                val isFeed2 = feedIndex2 != -1

                when {
                    isFeed1 && isFeed2 -> feedIndex1.compareTo(feedIndex2)  // Order by feed sequence
                    isFeed1 && !isFeed2 -> -1  // Feed types come first
                    !isFeed1 && isFeed2 -> 1   // Non-feed types come after
                    else -> supply1.name.compareTo(supply2.name)  // Alphabetical within groups
                }
            }

            for (supplyType in sortedList) {
                val gridPane = createSupplyGridPane(supplyType)
                mainTilePane.children.add(gridPane)
            }
        } else {
            println("No supply types found")
        }
    }

    private fun createSupplyGridPane(supplyType: SupplyType): GridPane {
        val gridPane = GridPane().apply {
            prefHeight = 101.0
            prefWidth = 455.0
            styleClass.add("grid-supply")
        }

        // Set up column constraints (same as FXML)
        val col1 = ColumnConstraints().apply {
            hgrow = Priority.SOMETIMES
            minWidth = 10.0
            percentWidth = 25.0
        }
        val col2 = ColumnConstraints().apply {
            hgrow = Priority.SOMETIMES
            minWidth = 10.0
        }
        val col3 = ColumnConstraints().apply {
            hgrow = Priority.SOMETIMES
            minWidth = 10.0
            percentWidth = 30.0
        }
        gridPane.columnConstraints.addAll(col1, col2, col3)

        // Set up row constraints
        repeat(5) {
            val row = RowConstraints().apply {
                minHeight = 10.0
                prefHeight = 30.0
                vgrow = Priority.SOMETIMES
            }
            gridPane.rowConstraints.add(row)
        }

        // Create ImageView
        val imageView = ImageView().apply {
            fitHeight = 75.0
            fitWidth = 75.0
            isPickOnBounds = true
            isPreserveRatio = true

            val imageFileName = supplyType.imagePath.substringAfterLast('/')
            val resourcePath = "/img/supply-img/$imageFileName"
            val isDefaultSupplyType = SupplySingleton.isDefaultSupplyType(supplyType.name)

            val imageUrl = if (isDefaultSupplyType) {
                javaClass.getResource(resourcePath)
            } else {
                File(supplyType.imagePath).toURI().toURL()
            }

            image = if (imageUrl != null) {
                Image(imageUrl.toString(), true)
            } else {
                Image(javaClass.getResource("/img/supply-img/default.png")?.toString(), true)
            }
        }
        GridPane.setHalignment(imageView, HPos.CENTER)
        GridPane.setValignment(imageView, VPos.CENTER)
        GridPane.setRowSpan(imageView, 5)
        gridPane.add(imageView, 0, 0)

        // Create name label
        val nameLabel = Label(supplyType.name).apply {
            styleClass.add("supply-label")
        }
        GridPane.setHalignment(nameLabel, HPos.CENTER)
        GridPane.setRowSpan(nameLabel, 2)
        gridPane.add(nameLabel, 1, 1)

        // Create count label
        SupplySingleton.setCurrentSupply(supplyType.name)
        val currentCount = SupplySingleton.getCurrentAmount()
        val formattedCount = currentCount.stripTrailingZeros().toPlainString()

        val unit = if (supplyType.unit.isNotEmpty()) " (${supplyType.unit})" else ""
        currentAmountLabel = Label("Current Quantity: $formattedCount $unit").apply {
            styleClass.add("h5")
        }
        GridPane.setHalignment(currentAmountLabel, HPos.CENTER)
        gridPane.add(currentAmountLabel, 1, 3)

        // Create buttons
        val viewHistoryButton = Button("View History").apply {
            maxHeight = Double.MAX_VALUE
            maxWidth = Double.MAX_VALUE
            styleClass.addAll("main-button-reversed", "h6-bold")
            setOnAction { navigateToViewSupplies() }
            setOnMousePressed { event ->
                SupplySingleton.setCurrentSupply(supplyType.name)
            }
        }
        GridPane.setHalignment(viewHistoryButton, HPos.CENTER)
        GridPane.setMargin(viewHistoryButton, Insets(10.0, 10.0, 10.0, 10.0))
        gridPane.add(viewHistoryButton, 2, 1)

        val updateCountButton = Button("Update Count").apply {
            maxHeight = Double.MAX_VALUE
            maxWidth = Double.MAX_VALUE
            styleClass.addAll("main-button-reversed", "h6-bold")
            setOnAction { navigateToUpdateSupplies() }
            setOnMousePressed { event ->
                SupplySingleton.setCurrentSupply(supplyType.name)
            }
        }
        GridPane.setHalignment(updateCountButton, HPos.CENTER)
        GridPane.setMargin(updateCountButton, Insets(10.0, 10.0, 10.0, 10.0))
        gridPane.add(updateCountButton, 2, 3)

        return gridPane
    }

    @FXML
    private fun navigateToCreateSupplies() {
        PopupUtil.showContentPopup("/fxml/content_create_supplies.fxml")
    }

    @FXML
    private fun navigateToUpdateSupplies() {
        PopupUtil.showContentPopup("/fxml/content_update_supplies_add_delete.fxml")
    }

    @FXML
    private fun navigateToViewSupplies() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_view_supplies.fxml")
    }
}
