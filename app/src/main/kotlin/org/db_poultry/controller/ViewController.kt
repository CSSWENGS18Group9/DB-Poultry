package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import org.db_poultry.controller.backend.CurrentFlockInUse
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import java.net.URL
import java.util.ResourceBundle

// TODO: Revise naming of this class to better reflect its purpose @Dattebayo2505
class ViewController: Initializable {

    @FXML
    lateinit var selectFlockAnchorPane: AnchorPane

    @FXML
    lateinit var mainFlowPane: FlowPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        loadFlockGrid()
    }

    private fun loadFlockGrid() {
        val flockMap = ReadFlock.allByID(DBConnect.getConnection())

        if (flockMap != null && flockMap.isNotEmpty()) {
            for ((flockId, flockComplete) in flockMap) {
                val button = createFlockButton(flockComplete)
                mainFlowPane.children.add(button)
            }
        } else {
            println("No flock records found")
        }
    }

    private fun createFlockButton(flockComplete: FlockComplete): Button {
        val flockComp = flockComplete
        val formattedDate = GeneralUtil.formatDatePretty(flockComp.flock.startingDate.toLocalDate())

        return Button(formattedDate).apply {
            alignment = Pos.CENTER
            maxHeight = -1.0
            maxWidth = -1.0
            isMnemonicParsing = false
            prefWidth = 225.0
            styleClass.addAll("flock-button", "h4-bold")
            setOnMouseClicked { navigateToFlockRelated() }
            setOnMousePressed { event ->
                CurrentFlockInUse.setCurrentFlockComplete(flockComp)
            }

        }
    }

    private fun navigateToFlockRelated() {
        val fxmlInUse = CurrentFlockInUse.getCurrentFlockFXML()

        // Refer to FlockHomeController.kt for the FXML reference
        when (fxmlInUse) {
            "create_flock_details" -> {
                navigateToCreateFlockDetails()
            }
            "view_flock_details" -> {
                navigateToViewFlockDetails()
            }
            "flock_generate_reports" -> {
                navigateToViewFlockGenerateReport()
            }
            else -> {
                println("Unknown FXML in use: $fxmlInUse")
            }
        }
    }

    @FXML
    fun navigateToCreateFlockDetails() {
        GeneralUtil.navigateToMainContent(selectFlockAnchorPane, "/fxml/content_create_flock_details.fxml")
    }

    @FXML
    fun navigateToViewFlockDetails() {
        GeneralUtil.navigateToMainContent(selectFlockAnchorPane, "/fxml/content_view_flock_details.fxml")
    }

    @FXML
    fun navigateToViewFlockGenerateReport() {
        GeneralUtil.navigateToMainContent(selectFlockAnchorPane, "/fxml/content_generate_report.fxml")

    }

}
