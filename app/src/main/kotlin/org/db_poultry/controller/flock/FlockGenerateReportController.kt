package org.db_poultry.controller.flock

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import org.db_poultry.controller.backend.FlockSingleton
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.reportDAO.ReadMortalityRate
import org.db_poultry.errors.generateErrorMessage
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import java.net.URL
import java.util.*

class FlockGenerateReportController : Initializable {

    @FXML
    private lateinit var flockMortalityReportAnchorPane: AnchorPane

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

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        setFlockMortalityReport()
    }

    private fun setFlockMortalityReport() {
        val currentFlockComp = FlockSingleton.getCurrentFlockComplete()
        updateFlockReportLabels(currentFlockComp)
    }

    private fun updateFlockReportLabels(flockComp: FlockComplete?) {
        val flock = flockComp?.flock ?: throw IllegalArgumentException("Flock data is null")

        try {
            // Set flock start date
            flockStartDateLbl.text = "${flock.startingDate}"

            // Set quantity started
            quantityStartedLbl.text = "${flock.startingCount}"

            // Use GetMortalityRate for calculation
            val mortalityRateData = ReadMortalityRate.calculateMortalityRateForFlock(
                getConnection(),
                flock.startingDate
            )

            if (mortalityRateData != null) {
                // Set mortality rate using the calculated value
                mortalityRateLbl.text = "${"%.2f".format(mortalityRateData.mortalityRate)}%"

                // Set remaining chicken count using the current count from MortalityRate
                remChickenCountLbl.text = "${mortalityRateData.curCount}"

                println("Updated labels for flock starting on: $flock.startingDate")
            } else {
                println("Failed to calculate mortality rate for flock starting on: $flock.startingDate")
            }
        } catch (e: Exception) {
            generateErrorMessage(
                "Error at `updateFlockReportLabels()` in `FlockGenerateReportController`",
                "Failed to update flock report labels for selected date: $flock.startingDate",
                "Check if the selected date is valid and flock data exists in the database",
                e
            )
        }
    }

}