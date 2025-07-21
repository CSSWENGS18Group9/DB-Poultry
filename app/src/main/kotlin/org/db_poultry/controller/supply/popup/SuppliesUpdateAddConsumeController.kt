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
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.SupplyTypeSingleton
import java.io.File
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

    private var currentSupplyType: String? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setSupplyName()
        setAmounts()
    }

    private fun setSupplyName() {

        currentSupplyType = SupplyTypeSingleton.getCurrentSupply()
        if (currentSupplyType != null) {

            supplyNameLabel.text = capitalizeWords(currentSupplyType!!)
        } else {
            supplyNameLabel.text = "Backend Error - MUST FIX"
        }
    }

    private fun setAmounts() {
        currentAmountLabel.text = SupplyTypeSingleton.getCurrentAmount()?.toString() ?: "0.00"
        updatedCurrentAmountLabel.text = currentAmountLabel.text
        updatedCurrentAmountLabel.style = "-fx-text-fill: black;"
        confirmButton.isDisable = true

        val updateButtonAndLabel = {
            val currentAmount = SupplyTypeSingleton.getCurrentAmount() ?: BigDecimal.ZERO
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

            updatedCurrentAmountLabel.style = when {
                !isAmountEntered -> "-fx-text-fill: black;"
                updatedAmount > currentAmount -> "-fx-text-fill: #606C38;"
                else -> "-fx-text-fill: #8F250C;"
            }
        }

        addAmountSupplyTextField.textProperty().addListener { _ -> updateButtonAndLabel() }
        deductAmountSupplyTextField.textProperty().addListener { _ -> updateButtonAndLabel() }
        dateDatePicker.valueProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                dateTodayLabel.text = GeneralUtil.formatDatePretty(newValue)
            } else {
                dateTodayLabel.text = ""
            }
            updateButtonAndLabel()
        }
        updateButtonAndLabel()
    }

    @FXML
    fun confirm() {
        val amountAdd: Double? = addAmountSupplyTextField.text.toDoubleOrNull()
        val amountDel: Double? = deductAmountSupplyTextField.text.toDoubleOrNull()
        val date = dateDatePicker.value

        if ((amountAdd == null || amountDel == null) && date == null) {
            println("Please fill in either the amount to add or delete and select a date.")
            return
        }

        val supplyID = ReadSupplyType.getSupplyTypeByName(getConnection(), currentSupplyType.toString())?.supplyTypeId ?: return

        val sqlDate = Date.valueOf(date)
        val added = BigDecimal(amountAdd!!)
        val consumed = BigDecimal(amountDel!!)

        val result = createSupplyRecord(getConnection(), supplyID, sqlDate, added, consumed, false)
        if (result != null) {
            undoSingleton.setUndoMode(undoTypes.doUndoSupplyRecord)
            PopupUtil.showPopup("success", "Supply record created successfully.")
            println("Successfully created supply record.")
        } else {
            PopupUtil.showPopup("error", "Failed to create supply record.")
            println("DEBUG: createSupplyRecord returned null")
        }
    }

    private fun capitalizeWords(input: String): String =
        input.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

    @FXML
    fun setDateToToday() {
        val today = java.time.LocalDate.now()
        dateDatePicker.value = today
        dateTodayLabel.text = GeneralUtil.formatDatePretty(today)
    }

    @FXML
    fun closePopup() {
        val stage = supplyNameLabel.scene.window as Stage
        stage.close()
    }
}
