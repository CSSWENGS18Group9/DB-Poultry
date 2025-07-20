package org.db_poultry.controller.flock.popup

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import org.db_poultry.controller.backend.CurrentFlockInUse
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes
import java.net.URL
import java.sql.Date
import java.util.ResourceBundle

class FlockCreateDetailsController : Initializable {

    @FXML
    private lateinit var flockNameLabel: Label

    @FXML
    private lateinit var depleteCountCFDTextField: TextField

    @FXML
    private lateinit var cFDDatePicker: DatePicker

    private var flockDate: Date? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setFlock()
    }

    private fun setFlock() {
        val currentFlock = CurrentFlockInUse.Companion.getCurrentFlockComplete()
        flockDate = currentFlock?.flock?.startingDate
        val dateToDisplay = GeneralUtil.formatDatePretty(flockDate?.toLocalDate())
        flockNameLabel.text  = "Current Flock Start Date: $dateToDisplay"
    }

    @FXML
    fun confirm() {
        val detailLocalDate = cFDDatePicker.value

        if (detailLocalDate == null) {
            println("Please select a flock detail date.")
            return
        }

        val detailDate = Date.valueOf(detailLocalDate)
        val depletedCount = depleteCountCFDTextField.text.toInt()

        println("Flock Date: $flockDate")
        println("Detail Date: $detailDate")
        println("Depleted Count: $depletedCount")

        if (CreateFlockDetails.createFlockDetails(DBConnect.getConnection(), flockDate, detailDate, depletedCount) != null) {
            undoSingleton.setUndoMode(undoTypes.doUndoFlockDetail)
            PopupUtil.showPopup("success", "Flock details created successfully.")
            println("Successfully created Flock.")
        } else {
            PopupUtil.showPopup("error", "Failed to create flock details.")
            println("Failed to create Flock.")
        }
    }

}