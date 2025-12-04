package org.db_poultry.controller.supply

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyRecordDAO.ReadSupplyRecord
import org.db_poultry.pojo.SupplyPOJO.SupplyComplete
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.SupplySingleton
import java.math.BigDecimal
import java.net.URL
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.ResourceBundle

/**
 * Data class representing an interval row in the supply report table.
 */
data class SupplyIntervalRow(
    val intervalLabel: String,
    val cost: String,
    val isZeroCost: Boolean = false
)

class SuppliesViewReportController : Initializable {

    @FXML
    lateinit var mainAnchorPane: AnchorPane

    @FXML
    lateinit var flockNameLabel: Label

    @FXML
    lateinit var supplyRecordsTableView: TableView<SupplyIntervalRow>

    @FXML
    lateinit var sortByComboBox: ComboBox<String>

    private var supplyRecords: List<SupplyComplete> = emptyList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // Set the supply name label
        flockNameLabel.text = GeneralUtil.capitalizeCase(SupplySingleton.getCurrentSupplyName())

        // Load supply records
        loadSupplyRecords()

        // Setup table columns dynamically
        setupTableColumns()

        // Setup combo box
        setupComboBox()

        // Initially populate table with weekly data
        populateTable("Weekly")
    }

    private fun loadSupplyRecords() {
        val supplyName = SupplySingleton.getCurrentSupplyName()
        val records = ReadSupplyRecord.getFromName(getConnection(), supplyName)
        supplyRecords = records?.sortedBy { it.date } ?: emptyList()
    }

    private fun setupTableColumns() {
        // Clear existing columns
        supplyRecordsTableView.columns.clear()

        // Create Interval column
        val intervalColumn = TableColumn<SupplyIntervalRow, String>("Week")
        intervalColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.intervalLabel)
        }
        intervalColumn.setCellFactory {
            object : TableCell<SupplyIntervalRow, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        textFill = Color.BLACK
                    } else {
                        text = item
                        val row = tableView.items.getOrNull(index)
                        textFill = if (row?.isZeroCost == true) Color.GRAY else Color.BLACK
                    }
                }
            }
        }
        intervalColumn.prefWidth = 400.0

        // Create Cost column
        val costColumn = TableColumn<SupplyIntervalRow, String>("Cost")
        costColumn.setCellValueFactory { cellData ->
            SimpleStringProperty(cellData.value.cost)
        }
        costColumn.setCellFactory {
            object : TableCell<SupplyIntervalRow, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        textFill = Color.BLACK
                    } else {
                        text = item
                        val row = tableView.items.getOrNull(index)
                        textFill = if (row?.isZeroCost == true) Color.GRAY else Color.BLACK
                    }
                }
            }
        }
        costColumn.prefWidth = 200.0

        supplyRecordsTableView.columns.addAll(intervalColumn, costColumn)
    }

    private fun setupComboBox() {
        sortByComboBox.items.clear()
        sortByComboBox.items.addAll("Weekly", "Monthly", "Yearly")
        sortByComboBox.value = "Weekly"

        sortByComboBox.setOnAction {
            val selectedInterval = sortByComboBox.value
            populateTable(selectedInterval)
            updateIntervalColumnHeader(selectedInterval)
        }
    }

    private fun updateIntervalColumnHeader(interval: String) {
        if (supplyRecordsTableView.columns.isNotEmpty()) {
            val headerText = when (interval) {
                "Weekly" -> "Week"
                "Monthly" -> "Month"
                "Yearly" -> "Year"
                else -> "Interval"
            }
            supplyRecordsTableView.columns[0].text = headerText
        }
    }

    private fun populateTable(interval: String) {
        val intervalRows = when (interval) {
            "Weekly" -> aggregateWeekly()
            "Monthly" -> aggregateMonthly()
            "Yearly" -> aggregateYearly()
            else -> aggregateWeekly()
        }

        val observableList = FXCollections.observableArrayList(intervalRows)
        supplyRecordsTableView.items = observableList
    }

    private fun aggregateWeekly(): List<SupplyIntervalRow> {
        if (supplyRecords.isEmpty()) return emptyList()

        // Group records by week (Monday to Sunday)
        val weeklyData = mutableMapOf<LocalDate, BigDecimal>()

        supplyRecords.forEach { record ->
            val recordDate = record.date?.toLocalDate() ?: return@forEach
            val weekStart = recordDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val cost = record.price ?: BigDecimal.ZERO
            weeklyData[weekStart] = weeklyData.getOrDefault(weekStart, BigDecimal.ZERO).add(cost)
        }

        // Generate all weeks from first to last record
        val allWeeks = generateWeeklyIntervals()

        return allWeeks.map { weekStart ->
            val weekEnd = weekStart.plusDays(6)
            val cost = weeklyData.getOrDefault(weekStart, BigDecimal.ZERO)
            val intervalLabel = formatWeeklyInterval(weekStart, weekEnd)
            SupplyIntervalRow(intervalLabel, formatCurrency(cost), cost.compareTo(BigDecimal.ZERO) == 0)
        }
    }

    private fun generateWeeklyIntervals(): List<LocalDate> {
        if (supplyRecords.isEmpty()) return emptyList()

        val firstDate = supplyRecords.minOfOrNull { it.date?.toLocalDate() ?: LocalDate.MAX } ?: return emptyList()
        val lastDate = supplyRecords.maxOfOrNull { it.date?.toLocalDate() ?: LocalDate.MIN } ?: return emptyList()

        val firstWeekStart = firstDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val lastWeekStart = lastDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val weeks = mutableListOf<LocalDate>()
        var currentWeek = firstWeekStart
        while (!currentWeek.isAfter(lastWeekStart)) {
            weeks.add(currentWeek)
            currentWeek = currentWeek.plusWeeks(1)
        }

        return weeks
    }

    private fun formatWeeklyInterval(weekStart: LocalDate, weekEnd: LocalDate): String {
        val startFormatter = DateTimeFormatter.ofPattern("MMMM d")
        val endDayFormatter = DateTimeFormatter.ofPattern("d")
        val endMonthDayFormatter = DateTimeFormatter.ofPattern("MMMM d")
        val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

        return if (weekStart.month == weekEnd.month) {
            // Same month: "July 21-27, 2025"
            "${weekStart.format(startFormatter)}-${weekEnd.format(endDayFormatter)}, ${weekEnd.format(yearFormatter)}"
        } else {
            // Different months: "July 28- August 3, 2025"
            "${weekStart.format(startFormatter)}- ${weekEnd.format(endMonthDayFormatter)}, ${weekEnd.format(yearFormatter)}"
        }
    }

    private fun aggregateMonthly(): List<SupplyIntervalRow> {
        if (supplyRecords.isEmpty()) return emptyList()

        // Group records by month
        val monthlyData = mutableMapOf<LocalDate, BigDecimal>()

        supplyRecords.forEach { record ->
            val recordDate = record.date?.toLocalDate() ?: return@forEach
            val monthStart = recordDate.withDayOfMonth(1)
            val cost = record.price ?: BigDecimal.ZERO
            monthlyData[monthStart] = monthlyData.getOrDefault(monthStart, BigDecimal.ZERO).add(cost)
        }

        // Generate all months from first to last record
        val allMonths = generateMonthlyIntervals()

        return allMonths.map { monthStart ->
            val cost = monthlyData.getOrDefault(monthStart, BigDecimal.ZERO)
            val intervalLabel = formatMonthlyInterval(monthStart)
            SupplyIntervalRow(intervalLabel, formatCurrency(cost), cost.compareTo(BigDecimal.ZERO) == 0)
        }
    }

    private fun generateMonthlyIntervals(): List<LocalDate> {
        if (supplyRecords.isEmpty()) return emptyList()

        val firstDate = supplyRecords.minOfOrNull { it.date?.toLocalDate() ?: LocalDate.MAX } ?: return emptyList()
        val lastDate = supplyRecords.maxOfOrNull { it.date?.toLocalDate() ?: LocalDate.MIN } ?: return emptyList()

        val firstMonthStart = firstDate.withDayOfMonth(1)
        val lastMonthStart = lastDate.withDayOfMonth(1)

        val months = mutableListOf<LocalDate>()
        var currentMonth = firstMonthStart
        while (!currentMonth.isAfter(lastMonthStart)) {
            months.add(currentMonth)
            currentMonth = currentMonth.plusMonths(1)
        }

        return months
    }

    private fun formatMonthlyInterval(monthStart: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return monthStart.format(formatter)
    }

    private fun aggregateYearly(): List<SupplyIntervalRow> {
        if (supplyRecords.isEmpty()) return emptyList()

        // Group records by year
        val yearlyData = mutableMapOf<Int, BigDecimal>()

        supplyRecords.forEach { record ->
            val recordDate = record.date?.toLocalDate() ?: return@forEach
            val year = recordDate.year
            val cost = record.price ?: BigDecimal.ZERO
            yearlyData[year] = yearlyData.getOrDefault(year, BigDecimal.ZERO).add(cost)
        }

        // Generate all years from first to last record
        val allYears = generateYearlyIntervals()

        return allYears.map { year ->
            val cost = yearlyData.getOrDefault(year, BigDecimal.ZERO)
            val intervalLabel = year.toString()
            SupplyIntervalRow(intervalLabel, formatCurrency(cost), cost.compareTo(BigDecimal.ZERO) == 0)
        }
    }

    private fun generateYearlyIntervals(): List<Int> {
        if (supplyRecords.isEmpty()) return emptyList()

        val firstYear = supplyRecords.minOfOrNull { it.date?.toLocalDate()?.year ?: Int.MAX_VALUE } ?: return emptyList()
        val lastYear = supplyRecords.maxOfOrNull { it.date?.toLocalDate()?.year ?: Int.MIN_VALUE } ?: return emptyList()

        return (firstYear..lastYear).toList()
    }

    private fun formatCurrency(amount: BigDecimal): String {
        return "₱${String.format("%,.2f", amount)}"
    }

    @FXML
    fun backToViewSupplies() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_home_supplies_grid.fxml")
    }


}