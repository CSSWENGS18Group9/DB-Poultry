package org.db_poultry.controller.supply

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.ReadSupplyType

import javafx.fxml.FXML
import javafx.scene.control.Label

import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import org.db_poultry.db.supplyRecordDAO.CreateSupplyRecord
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.util.GeneralUtil
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

        // Disalble confirm button if all counts are zero
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

    @FXML
    fun confirm() {
        for (supplyTypeID in feedSupplyTypeIDList) {
            CreateSupplyRecord.createSupplyRecord(getConnection(), supplyTypeID,
                sqlDate, BigDecimal.ZERO, BigDecimal.ZERO, true)
        }

        setFeedCounts()
    }
}
