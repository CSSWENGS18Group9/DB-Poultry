package org.db_poultry.controller

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.pojo.SupplyPOJO.SupplyType
import org.db_poultry.controller.backend.CurrentSupplyInUse

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.geometry.Insets
import javafx.geometry.Pos
import java.sql.Date

import javafx.fxml.Initializable
import org.db_poultry.util.GeneralUtil
import java.net.URL
import java.util.ResourceBundle

class SuppliesGridHomeController: Initializable {

    @FXML
    private lateinit var mainAnchorPane: AnchorPane

    @FXML
    private lateinit var mainFlowPane: FlowPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        loadSupplyGrid()
//        testAddVBoxes() // For testing purposes, remove in production

    }

    private fun loadSupplyGrid() {

        val supplyTypeList = ReadSupplyType.getAllSupplyTypes(getConnection())

        if (supplyTypeList != null && supplyTypeList.isNotEmpty()) {
            for (supplyType in supplyTypeList) {
                val vbox = createSupplyVBox(supplyType)
                mainFlowPane.children.add(vbox)
            }
        } else {
            println("No supply types found")
        }
    }

    private fun createSupplyVBox(supplyType: SupplyType): VBox {
        // Create VBox with same properties as example FXML (refer to content_home_supplies_grid.fxml)
        val vbox = VBox().apply {
            alignment = Pos.CENTER
            prefHeight = 200.0
            prefWidth = 200.0
            spacing = 10.0
            styleClass.add("grid")
            setOnMouseClicked { navigateToUpdateSupplies() }
            setOnMousePressed { event ->
                CurrentSupplyInUse.setCurrentSupply(supplyType.name)
                CurrentSupplyInUse.setCurrentSupplyImageDir("/img/supply-img/${supplyType.name}.jpg")
            }

        }

        val imageView = ImageView().apply {
            fitHeight = 150.0
            fitWidth = 200.0
            isPickOnBounds = true
            isPreserveRatio = true

            val supportedExtensions = listOf("jpg", "png", "jpeg", "gif", "bmp")
            val basePath = "/img/supply-img/${supplyType.name}"
            var imageUrl: URL? = null

            for (ext in supportedExtensions) {
                val path = "$basePath.$ext"
                imageUrl = javaClass.getResource(path)
                if (imageUrl != null) break
            }

            image = if (imageUrl != null) {
                Image(imageUrl.toString(), true)
            } else {
                Image(javaClass.getResource("/img/supply-img/default.png")?.toString(), true)
            }
            
            VBox.setMargin(this, Insets(10.0, 0.0, 0.0, 0.0))
        }

        // Create supply name label
        val nameLabel = Label(supplyType.name).apply {
            styleClass.add("supply-label")
        }

        val currentCount = ReadSupplyRecord.getCurrentCountForDate(
            getConnection(),
            supplyType.supplyTypeId,
            Date(System.currentTimeMillis())
        )

        
        val countLabel = Label("Current Count: $currentCount")

        // Add all children to VBox
        vbox.children.addAll(imageView, nameLabel, countLabel)

        return vbox
    }

    private fun navigateToUpdateSupplies() {
        GeneralUtil.loadContentView(mainAnchorPane, "/fxml/content_update_supplies_add_delete.fxml")
    }

}
