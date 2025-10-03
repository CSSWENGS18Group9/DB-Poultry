package org.db_poultry.controller.flock.popup

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import org.db_poultry.controller.NotificationController
import org.db_poultry.util.FlockSingleton
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDetailsDAO.CreateFlockDetails
import org.db_poultry.util.GUIUtil
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes
import java.net.URL
import java.sql.Date
import java.util.ResourceBundle

class FlockCreateDetailsController : Initializable {

    @FXML
    private lateinit var updateFlockAnchorPane: AnchorPane

    @FXML
    private lateinit var flockNameLabel: Label

    @FXML
    private lateinit var currentChickenLabel: Label

    @FXML
    private lateinit var updatedChickenLabel: Label

    @FXML
    private lateinit var depleteCountCFDTextField: TextField

    @FXML
    private lateinit var dateDatePicker: DatePicker

    @FXML
    private lateinit var setTodayButton: Button

    @FXML
    private lateinit var dateTodayLabel: Label

    @FXML
    private lateinit var confirmBtn: Button

    private var flockDate: Date? = null
    private var currentChickenCount: Int = 0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setFlock()
        setupDynamicUpdates()
    }

    private fun setFlock() {
        val currentFlock = FlockSingleton.getCurrentFlockComplete()
        flockDate = currentFlock?.flock?.startingDate
        currentChickenCount = FlockSingleton.getCurrentCount()

        val dateToDisplay = GeneralUtil.formatDatePretty(flockDate?.toLocalDate())
        flockNameLabel.text = "Flock Start: $dateToDisplay"

        currentChickenLabel.text = currentChickenCount.toString()
        updatedChickenLabel.text = currentChickenCount.toString()
        if(GUIUtil.getDarkMode()) { updatedChickenLabel.style = "-fx-text-fill: white;" }
        else { updatedChickenLabel.style = "-fx-text-fill: black;" }
        confirmBtn.isDisable = true
    }

    private fun setupDynamicUpdates() {
        val updateButtonAndLabel = {
            val depletedText = depleteCountCFDTextField.text
            val depletedAmount = depletedText.toIntOrNull() ?: 0
            val updatedCount = currentChickenCount - depletedAmount
            updatedChickenLabel.text = updatedCount.toString()

            val isCountEntered = depleteCountCFDTextField.text.isNotBlank()
            val isDateSet = dateDatePicker.value != null
            confirmBtn.isDisable = !(isCountEntered && isDateSet)

            // Disable setTodayButton if date picker already has today's date
            val today = java.time.LocalDate.now()
            setTodayButton.isDisable = dateDatePicker.value == today

            if(GUIUtil.getDarkMode()) {
                updatedChickenLabel.style = when {
                    !isCountEntered -> "-fx-text-fill: white;"
                    updatedCount < currentChickenCount -> "-fx-text-fill: #FF0000;"
                    else -> "-fx-text-fill: #D4FF43;"
                }
            } else {
                updatedChickenLabel.style = when {
                    !isCountEntered -> "-fx-text-fill: black;"
                    updatedCount < currentChickenCount -> "-fx-text-fill: #8F250C;"
                    else -> "-fx-text-fill: #606C38;"
                }
            }
        }

        depleteCountCFDTextField.textProperty().addListener { _ -> updateButtonAndLabel() }
        dateDatePicker.valueProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                dateTodayLabel.text = "Flock record created at: " + GeneralUtil.formatDatePretty(newValue)
            } else {
                dateTodayLabel.text = "Flock record created at:"
            }
            updateButtonAndLabel()
        }
        updateButtonAndLabel()
    }

    @FXML
    fun setDateToToday() {
        val today = java.time.LocalDate.now()
        dateDatePicker.value = today
        dateTodayLabel.text = "Flock record created at: " + GeneralUtil.formatDatePretty(today)
    }

    @FXML
    fun confirm() {
        val detailLocalDate = dateDatePicker.value

        if (detailLocalDate == null) {
            PopupUtil.showPopup("error", "Please select a flock detail date.")
            return
        }

        val depleteCountText = depleteCountCFDTextField.text
        if (depleteCountText.isBlank()) {
            PopupUtil.showPopup("error", "Please enter a depleted count.")
            return
        }

        val depletedCount = depleteCountText.toIntOrNull()
        if (depletedCount == null || depletedCount < 0) {
            PopupUtil.showPopup("error", "Please enter a valid positive number for depleted count.")
            return
        }

        val detailDate = Date.valueOf(detailLocalDate)

        if (CreateFlockDetails.createFlockDetails(DBConnect.getConnection(), flockDate, detailDate, depletedCount) != null) {
            undoSingleton.setUndoMode(undoTypes.doUndoFlockDetail)

            NotificationController.setNotification(
                "info",
                "Flock Update Records Undo",
                "Flock details created successfully. Can be undone."
            )

            PopupUtil.showPopup("success", "Flock details created successfully.")
            println("Successfully created Flock details.")
        } else {
            NotificationController.setNotification(
                "error",
                "Flock Update Records Error",
                "Failed to create flock details with date: $detailDate and depleted count: $depletedCount"
            )

            NotificationController.showNotification()
            PopupUtil.showPopup("error", "Failed to create flock details.")
            println("Failed to create Flock details.")
        }

        GeneralUtil.refreshPage(null, "/fxml/content_home_flock_grid.fxml")
        closePopup()
    }

    @FXML
    fun closePopup() {
        val stage = updateFlockAnchorPane.scene.window as javafx.stage.Stage
        stage.close()
    }
}