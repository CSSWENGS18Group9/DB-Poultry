package org.db_poultry.controller

import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.App
import org.db_poultry.controller.util.recordFlockDetails
import org.db_poultry.db.DBConnect
import org.db_poultry.db.cleanTables

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import java.sql.Date
import java.time.LocalDate
import javafx.scene.control.Button
import javafx.scene.control.ComboBox

import java.net.URL
import java.util.ResourceBundle

class CreateFlockDetailsController: Initializable {

    init {
        cleanTables(DBConnect.getConnection())
    }

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var cFDComboBox: ComboBox<String>

    @FXML
    private lateinit var datePicker: DatePicker

    @FXML
    private lateinit var shapeBg: Rectangle

    @FXML
    private lateinit var textBody1: Text

    @FXML
    private lateinit var textBody2: Text

    @FXML
    private lateinit var textBody3: Text

    @FXML
    private lateinit var textField: TextField

    @FXML
    private lateinit var textHeader: Text

    @FXML
    private lateinit var depleteCountCFDtextField: TextField

    @FXML
    private lateinit var cFDDatePicker: DatePicker

    @FXML
    private lateinit var confirmBtn: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        
        val flockMap = ReadFlock.allByID(DBConnect.getConnection())

        val dateList = mutableListOf<String>()

        if (flockMap != null && flockMap.isNotEmpty()) {
            val dates = flockMap.values.map { it.flock.startingDate.toString() }
            dateList.addAll(dates)
            println("Flock starting dates: $dates") // DEBUGGING
        } else {
            println("No data found.")
        }
        
        cFDComboBox.items.clear()
        cFDComboBox.items.addAll(dateList)
        cFDComboBox.value = "Select a flock"
    }

    @FXML
    fun confirm() {
        // still need to pass connection. will leave alone for now

        val flockDateStr = cFDComboBox.value
        val detailLocalDate = cFDDatePicker.value

        if (flockDateStr == null || flockDateStr == "Select a flock" || detailLocalDate == null) {
            println("Please select a flock and a detail date.")
            return
        }

        val flockDate = Date.valueOf(flockDateStr)
        val detailDate = Date.valueOf(detailLocalDate)
        val depletedCount = depleteCountCFDtextField.text.toInt()

        println("Flock Date: $flockDate")
        println("Detail Date: $detailDate")
        println("Depleted Count: $depletedCount")

        val feedback = recordFlockDetails(DBConnect.getConnection(), flockDate, detailDate, depletedCount)

        when (feedback) {
            1 -> println("Successfully created new flock")
            0, -1 -> println("Error creating new flock")
        }

    }

}
