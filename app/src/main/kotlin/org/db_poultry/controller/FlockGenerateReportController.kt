package org.db_poultry.controller

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.flockDAO.ReadFlock

import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane

import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle

import java.sql.Date

class FlockGenerateReportController: Initializable {

    @FXML
    private lateinit var flockMortalityReportAnchorPane: AnchorPane

    @FXML
    private lateinit var flockStartDateComboBox: ComboBox<String>

    @FXML
    private lateinit var flockStartDateLbl: Label

    @FXML
    private lateinit var mortalityRateLbl: Label

    @FXML
    private lateinit var quantityStartedLbl: Label

    @FXML
    private lateinit var remChickenCountLbl: Label

    @FXML
    private lateinit var subGenerateReportAnchorPane: AnchorPane

    private var flockMap: HashMap<Date, org.db_poultry.pojo.FlockPOJO.FlockComplete>? = null

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // Load flock data
        flockMap = ReadFlock.allByDate(getConnection())

        val dateList = mutableListOf<String>()

        if (flockMap != null && flockMap!!.isNotEmpty()) {
            val dates = flockMap!!.keys.map { it.toString() }
            dateList.addAll(dates)
            println("Flock starting dates: $dates") // DEBUGGING
        } else {
            println("No data found.")
        }

        flockStartDateComboBox.items.clear()
        flockStartDateComboBox.items.addAll(dateList)
        flockStartDateComboBox.value = "Select a Flock"

        flockStartDateComboBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            if (newValue != null && newValue != "Select a Flock") {
                updateFlockReportLabels(newValue)
            } else {
                clearLabels()
            }
        }
    }

    private fun updateFlockReportLabels(selectedDateStr: String) {
        try {
            val selectedDate = Date.valueOf(selectedDateStr)
            val flockComplete = flockMap?.get(selectedDate)
            
            if (flockComplete != null) {
                val flock = flockComplete.flock
                val flockDetails = flockComplete.flockDetails
                
                // Set flock start date
                flockStartDateLbl.text = "${flock.startingDate}"
                
                // Set quantity started
                quantityStartedLbl.text = "${flock.startingCount}"
                
                // Calculate total deaths from flock details
                val totalDeaths = flockDetails.sumOf { it.depletedCount }
                
                // Calculate mortality rate
                val mortalityRate = if (flock.startingCount > 0) {
                    (totalDeaths.toDouble() / flock.startingCount.toDouble()) * 100
                } else {
                    0.0
                }
                mortalityRateLbl.text = "${"%.2f".format(mortalityRate)}%"

                // Calculate remaining chicken count
                val remainingCount = flock.startingCount - totalDeaths
                remChickenCountLbl.text = "$remainingCount"
                
                println("Updated labels for flock starting on: $selectedDateStr")
            } else {
                println("No flock data found for date: $selectedDateStr")
                clearLabels()
            }
        } catch (e: Exception) {
            println("Error updating flock report labels: ${e.message}")
            e.printStackTrace()
            clearLabels()
        }
    }
    
    private fun clearLabels() {
        flockStartDateLbl.text = "Start Date: -"
        quantityStartedLbl.text = "Starting Count: -"
        remChickenCountLbl.text = "Remaining: -"
        mortalityRateLbl.text = "Mortality Rate: -"
    }





}