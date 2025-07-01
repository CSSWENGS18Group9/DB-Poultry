package org.db_poultry.controller

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.pojo.SupplyPOJO.SupplyType
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete


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

    /**
     * Test function that brute-force adds sample VBoxes to test layout
     */
    private fun testAddVBoxes() {
        val testSupplyNames = listOf(
            "Corn", "Rice", "Wheat", "Soybean", "Vitamins", 
            "Medicine", "Feed A", "Feed B", "Supplement", 
            "Water Treatment", "Calcium", "Protein Mix"
        )
        
        println("Adding ${testSupplyNames.size} test VBoxes...")
        
        for (i in testSupplyNames.indices) {
            val testVBox = createTestVBox(testSupplyNames[i], i + 1)
            mainFlowPane.children.add(testVBox)
            println("Added test VBox: ${testSupplyNames[i]}")
        }
        
        println("Total VBoxes in FlowPane: ${mainFlowPane.children.size}")
    }

    /**
     * Creates a test VBox with sample data
     */
    private fun createTestVBox(supplyName: String, count: Int): VBox {
        val vbox = VBox().apply {
            alignment = Pos.CENTER
            prefHeight = 200.0
            prefWidth = 200.0
            spacing = 10.0
            styleClass.add("grid")
        }

        // Create test image (solid color rectangle as placeholder)
        val imageView = ImageView().apply {
            fitHeight = 150.0
            fitWidth = 200.0
            isPickOnBounds = true
            isPreserveRatio = true
            
            try {
                val imageUrl = javaClass.getResource("/img/supply-img/default.png")
                if (imageUrl != null) {
                    image = Image(imageUrl.toString())
                } else {
                    println("No default image found for: $supplyName")
                }
            } catch (e: Exception) {
                println("Failed to load image for: $supplyName - ${e.message}")
            }
            
            VBox.setMargin(this, Insets(10.0, 0.0, 0.0, 0.0))
        }

        // Create supply name label
        val nameLabel = Label(supplyName).apply {
            styleClass.add("supply-label")
        }

        // Create count label with test data
        val countLabel = Label("Current Count: ${count * 25}").apply {
        }

        // Add all children to VBox
        vbox.children.addAll(imageView, nameLabel, countLabel)

        return vbox
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
        // Create VBox with same properties as FXML
        val vbox = VBox().apply {
            alignment = Pos.CENTER
            prefHeight = 200.0
            prefWidth = 200.0
            spacing = 10.0
            styleClass.add("grid")
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
                Image(imageUrl.toString())
            } else {
                Image(javaClass.getResource("/img/supply-img/default.png")?.toString())
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

}
