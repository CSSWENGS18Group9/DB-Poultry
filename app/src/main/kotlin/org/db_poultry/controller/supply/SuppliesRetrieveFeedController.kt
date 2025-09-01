package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType


import javafx.fxml.FXML
import javafx.scene.control.Label

import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import org.db_poultry.controller.NotificationController
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.PopupUtil
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes
import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.sql.Date
import java.util.ResourceBundle

class SuppliesRetrieveFeedController: Initializable {

    @FXML
    private lateinit var currCountBoosterLabel: Label

    @FXML
    private lateinit var currCountStarterLabel: Label

    @FXML
    private lateinit var currCountGrowerLabel: Label

    @FXML
    private lateinit var currCountFinisherLabel: Label

    @FXML
    private lateinit var dateDatePicker: DatePicker

    @FXML
    private lateinit var dateTodayLabel: Label

    @FXML
    private lateinit var setTodayButton: Button

    @FXML
    private lateinit var confirmButton: Button

    private lateinit var sqlDate: Date
    private val feedArrayList: List<String> = listOf("booster feed", "starter feed", "grower feed", "finisher feed")
    private lateinit var feedSupplyTypeIDList: MutableList<Int>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        feedSupplyTypeIDList = mutableListOf()
        setFeedCounts()
        setDataLabelListener()

    }

    private fun setFeedCounts() {
        val feedCountMap = HashMap<String, BigDecimal>()

        for (feed in feedArrayList) {
            val supplyTypeID = ReadSupplyType.getSupplyTypeByName(getConnection(), feed).supplyTypeId
            feedSupplyTypeIDList.add(supplyTypeID)
            val count = ReadSupplyRecord.getMostRecentFromID(getConnection(), supplyTypeID)?.current ?: BigDecimal.ZERO
            feedCountMap[feed] = count
        }

        currCountBoosterLabel.text = "Current Count: ${feedCountMap["booster feed"] ?: "ERROR"}"
        currCountStarterLabel.text = "Current Count: ${feedCountMap["starter feed"] ?: "ERROR"}"
        currCountGrowerLabel.text = "Current Count: ${feedCountMap["grower feed"] ?: "ERROR"}"
        currCountFinisherLabel.text = "Current Count: ${feedCountMap["finisher feed"] ?: "ERROR"}"

        // Disable confirm button if all counts are zero
        val allCountsZero = feedCountMap.values.all { it.compareTo(BigDecimal.ZERO) == 0 }
        confirmButton.isDisable = allCountsZero

    }

    private fun setDataLabelListener() {
        dateDatePicker.valueProperty().addListener { _, _, newValue ->
            if (newValue != null) {
                sqlDate = Date.valueOf(newValue)
                dateTodayLabel.text = "Feed Retrieval: " + GeneralUtil.formatDatePretty(newValue)
                setTodayButton.isDisable = newValue == LocalDate.now()
            } else {
                dateTodayLabel.text = "Feed Retrieval:"
                setTodayButton.isDisable = false
            }
        }
    }

    @FXML
    fun setDateToToday() {
        val today = LocalDate.now()
        dateDatePicker.value = today
        dateTodayLabel.text = "Feed Retrieval: " + GeneralUtil.formatDatePretty(today)
        sqlDate = Date.valueOf(today)
    }

    private fun isDateValid(): Boolean {
        val selectedDate = dateDatePicker.value
        if (selectedDate == null) {
            PopupUtil.showPopup("error", "Please select a date for feed retrieval.")
            return false
        }

        for (supplyTypeID in feedSupplyTypeIDList) {
            val latestSupply = ReadSupplyRecord.getLatest(getConnection(), supplyTypeID)
            if (latestSupply != null) {
                val latestDate = latestSupply.date.toLocalDate()
                if (selectedDate <= latestDate) {
                    PopupUtil.showPopup(
                        "error",
                        "Selected date must be after the latest supply record date (${GeneralUtil.formatDatePretty(latestDate)}).")
                    return false
                }
            }
        }

        return true
    }

    @FXML
    fun confirm() {

        if (!isDateValid()) {
            return
        }

        undoSingleton.setIsFeedRetrieval(true)
        undoSingleton.setUndoMode(undoTypes.doUndoSupplyRecord)

        val results = feedSupplyTypeIDList.map { supplyTypeID ->
            CreateSupplyRecord.createSupplyRecord(getConnection(), supplyTypeID,
                sqlDate, BigDecimal.ZERO, BigDecimal.ZERO, true)
        }

//
//        // Check if any of the results are null, indicating failure
//        if (results.any { it == null }) {
//            PopupUtil.showPopup("error", "Failed to create supply records for feed retrieval.")
//
//            /*
//            For resetting the non-null created supply records,
//            since all feed retrievals should be done at once,
//            reset all those that were retrieved.
//            */
//            results.forEach { result ->
//                if (result != null) {
//                    undoSingleton.undo(getConnection())
//                }
//            }
//            return
//        }

        NotificationController.setNotification(
            "info",
            "Chicken Feed Retrieval Undo",
            "Supply record for '${feedArrayList.joinToString(", ")}' retrieval was undone."
        )
        PopupUtil.showPopup("success", "Feed retrieval transaction successful.")

        setFeedCounts()
    }
}
