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
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.SupplyTypeSingleton
import java.io.File
import java.net.URL
import java.util.ResourceBundle
import kotlin.toString

class SuppliesUpdateAddConsumeController: Initializable {

    @FXML
    private lateinit var supplyNameLabel: Label

    @FXML
    private lateinit var supplyTypeImageView: ImageView

    @FXML
    private lateinit var datepickerDate: DatePicker

    @FXML
    private lateinit var addAmountSupplyTextField: TextField

    @FXML
    private lateinit var deductAmountSupplyTextField: TextField

    private var currentSupplyType: String? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        setSupplyName()
        setSupplyImage()

    }

    private fun setSupplyName() {

        currentSupplyType = SupplyTypeSingleton.getCurrentSupply()
        if (currentSupplyType != null) {

            supplyNameLabel.text = capitalizeWords(currentSupplyType!!)
        } else {
            supplyNameLabel.text = "Backend Error - MUST FIX"
        }
    }

    private fun setSupplyImage() {
        val supplyName = SupplyTypeSingleton.getCurrentSupply()
        val imageDir = SupplyTypeSingleton.getCurrentSupplyImageDir()
        val isDefaultSupplyType = supplyName != null && SupplyTypeSingleton.isDefaultSupplyType(supplyName)

        val imageUrl: URL? = if (isDefaultSupplyType) {
            val imageFileName = imageDir?.substringAfterLast('/')
            val resourcePath = "/img/supply-img/$imageFileName"
            javaClass.getResource(resourcePath)
        } else {
            if (imageDir != null) File(imageDir).toURI().toURL() else null
        }

        supplyTypeImageView.image = if (imageUrl != null) {
            Image(imageUrl.toString(), true)
        } else {
            Image(javaClass.getResource("/img/supply-img/default.png")?.toString(), true)
        }
    }


    @FXML
    fun confirm() {
        val amountAdd: Double? = addAmountSupplyTextField.text.toDoubleOrNull()
        val amount_del: Double? = deductAmountSupplyTextField.text.toDoubleOrNull()
        val date = datepickerDate.value

        if ((amountAdd == null || amount_del == null) && date == null) {
            println("Please fill in either the amount to add or delete and select a date.")
            return
        }

        val supplyID = ReadSupplyType.getSupplyTypeByName(getConnection(), currentSupplyType.toString())?.supplyTypeId ?: return

        val sqlDate = Date.valueOf(date)
        val added = BigDecimal(amountAdd!!)
        val consumed = BigDecimal(amount_del!!)

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

}
