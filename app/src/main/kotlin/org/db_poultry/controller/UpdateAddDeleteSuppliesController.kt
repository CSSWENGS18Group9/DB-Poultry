package org.db_poultry.controller

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord.createSupplyRecord
import org.db_poultry.controller.backend.CurrentSupplyInUse

import javafx.fxml.FXML
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextField

import java.math.BigDecimal
import java.sql.Date

import javafx.fxml.Initializable
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.net.URL
import java.util.ResourceBundle

class UpdateAddDeleteSuppliesController: Initializable {

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

        currentSupplyType = CurrentSupplyInUse.getCurrentSupply()
        if (currentSupplyType != null) {

            supplyNameLabel.text = capitalizeWords(currentSupplyType!!)
        } else {
            supplyNameLabel.text = "Backend Error - MUST FIX"
        }
    }

    private fun setSupplyImage() {
        val imageDir = CurrentSupplyInUse.getCurrentSupplyImageDir()
        if (imageDir != null) {
            var imagePath = javaClass.getResource(imageDir)?.toExternalForm()
            if (imagePath != null) {
                supplyTypeImageView.image = Image(imagePath, true)
            }
            else {
                imagePath = javaClass.getResource("/img/supply-img/default.png")?.toExternalForm()
                supplyTypeImageView.image = Image(imagePath, true)
                println("Image not found at path: $imageDir")
            }
        } else {
            println("Current supply image directory is null")
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

        createSupplyRecord(getConnection(), supplyID, sqlDate, added, consumed, false)

    }

    private fun capitalizeWords(input: String): String =
        input.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

}
