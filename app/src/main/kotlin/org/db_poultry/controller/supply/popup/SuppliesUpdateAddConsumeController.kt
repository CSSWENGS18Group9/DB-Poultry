package org.db_poultry.controller.supply.popup

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord.createSupplyRecord
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes

import javafx.fxml.FXML
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.math.BigDecimal
import java.sql.Date
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import org.db_poultry.controller.NotificationController
import org.db_poultry.util.GUIUtil
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.SupplySingleton
import java.net.URL
import java.util.ResourceBundle
import kotlin.toString


class SuppliesUpdateAddConsumeController: Initializable {

    @FXML
    private lateinit var updateSuppliesAnchorPane: AnchorPane

    @FXML
    private lateinit var supplyNameLabel: Label

    @FXML
    private lateinit var currentAmountLabel: Label

    @FXML
    private lateinit var updatedCurrentAmountLabel: Label

    @FXML
    private lateinit var setTodayButton: Button
    
    @FXML
    private lateinit var dateDatePicker: DatePicker

    @FXML
    private lateinit var dateTodayLabel: Label

    @FXML
    private lateinit var addAmountSupplyTextField: TextField

    @FXML
    private lateinit var deductAmountSupplyTextField: TextField

    @FXML
    private lateinit var confirmButton: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setSupplyName()
        setAmounts()
    }

    private fun setSupplyName() {
        supplyNameLabel.text = GeneralUtil.capitalizeCase(SupplySingleton.getCurrentSupplyName())
    }

    private fun setAmounts() {
        currentAmountLabel.text = SupplySingleton.getCurrentAmount().toString()
        updatedCurrentAmountLabel.text = currentAmountLabel.text
        if(GUIUtil.getDarkMode()) { updatedCurrentAmountLabel.style = "-fx-text-fill: white;"
        } else { updatedCurrentAmountLabel.style = "-fx-text-fill: black;" }

        confirmButton.isDisable = true

        val updateButtonAndLabel = {
            val currentAmount = SupplySingleton.getCurrentAmount()
            val addText = addAmountSupplyTextField.text
            val deductText = deductAmountSupplyTextField.text
            val addAmount = addText.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val deductAmount = deductText.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val updatedAmount = currentAmount + addAmount - deductAmount
            updatedCurrentAmountLabel.text = updatedAmount.toPlainString()

            val isAmountEntered = addAmountSupplyTextField.text.isNotBlank() || deductAmountSupplyTextField.text.isNotBlank()
            val isDateSet = dateDatePicker.value != null
            confirmButton.isDisable = !(isAmountEntered && isDateSet)

            // Disable setTodayButton if date picker already has today's date
            val today = java.time.LocalDate.now()
            setTodayButton.isDisable = dateDatePicker.value == today

            if(GUIUtil.getDarkMode()) {
                updatedCurrentAmountLabel.style = when {
                    !isAmountEntered -> "-fx-text-fill: white;"
                    updatedAmount > currentAmount -> "-fx-text-fill: #D4FF43;"
                    else -> "-fx-text-fill: #FF0000;"
                }
            } else {
                updatedCurrentAmountLabel.style = when {
                    !isAmountEntered -> "-fx-text-fill: black;"
                    updatedAmount > currentAmount -> "-fx-text-fill: #606C38;"
                    else -> "-fx-text-fill: #8F250C;"
                }
            }
        }

        addAmountSupplyTextField.textProperty().addListener { _ -> updateButtonAndLabel() }
        deductAmountSupplyTextField.textProperty().addListener { _ -> updateButtonAndLabel() }
        dateDatePicker.valueProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                dateTodayLabel.text = "Supply created at: " + GeneralUtil.formatDatePretty(newValue)
            } else {
                dateTodayLabel.text = "Supply created at:"
            }
            updateButtonAndLabel()
        }
        updateButtonAndLabel()
    }

    @FXML
    fun confirm() {
        val amountAdd = addAmountSupplyTextField.text.toDoubleOrNull() ?: 0.0
        val amountDel = deductAmountSupplyTextField.text.toDoubleOrNull() ?: 0.0
        val date = dateDatePicker.value

        if (date == null) {
            println("Please select a date.")
            return
        }

        val supplyID = SupplySingleton.getCurrentSupplyID()

        val sqlDate = Date.valueOf(date)
        val added = BigDecimal(amountAdd)
        val consumed = BigDecimal(amountDel)

        val result = createSupplyRecord(getConnection(), supplyID, sqlDate, added, consumed, false)
        if (result != null) {
            undoSingleton.setUndoMode(undoTypes.doUndoSupplyRecord)
            NotificationController.setNotification(
                "error",
                "Supply-Add Undo",
                "Supply record for '${SupplySingleton.getCurrentSupplyName()}' removed successfully."
            )
            PopupUtil.showPopup("success", "Supply record created successfully.")
            println("Successfully created supply record.")
        } else {
            PopupUtil.showPopup("error", "Failed to create supply record.")
            println("DEBUG: createSupplyRecord returned null")
        }

//        updatePaneState()
        closePopup()
    }

    private fun updatePaneState() {
        currentAmountLabel.text = SupplySingleton.getCurrentAmount().toString()
        addAmountSupplyTextField.clear()
        deductAmountSupplyTextField.clear()
        dateDatePicker.value = null

    }

    @FXML
    fun setDateToToday() {
        val today = java.time.LocalDate.now()
        dateDatePicker.value = today
        dateTodayLabel.text = "Supply created at: " + GeneralUtil.formatDatePretty(today)
    }

    @FXML
    fun closePopup() {
        val stage = supplyNameLabel.scene.window as Stage
        stage.close()
    }
}
