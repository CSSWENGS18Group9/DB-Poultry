package org.db_poultry.controller.flock

import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.chart.BarChart
import javafx.scene.chart.PieChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.GridPane
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.db.reportDAO.ReadMortalityRate
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import org.db_poultry.pojo.FlockPOJO.FlockDetails
import org.db_poultry.util.GeneralUtil
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Base controller containing shared logic for displaying flock data with charts and tables.
 * This prevents code duplication between HomeController and FlockViewDetailsController.
 */
abstract class BaseFlockViewController {

    @FXML
    lateinit var flockNameLabel: Label

    @FXML
    lateinit var dateStartedLabel: Label

    @FXML
    lateinit var quantityStartedLabel: Label

    @FXML
    lateinit var mortalityRateLabel: Label

    @FXML
    lateinit var currentCountLabel: Label

    @FXML
    lateinit var mortalityBarChart: BarChart<String, Number>

    @FXML
    lateinit var mortalityPieChart: PieChart

    @FXML
    lateinit var flockRecordsTableView: TableView<FlockDetails>

    @FXML
    lateinit var colDate: TableColumn<FlockDetails, Date>

    @FXML
    lateinit var colDepletion: TableColumn<FlockDetails, Int>

    @FXML
    lateinit var colChickenCount: TableColumn<FlockDetails, Int>

    @FXML
    lateinit var selectFreqComboBox: ComboBox<String>

    @FXML
    lateinit var startDateComboBox: ComboBox<String>

    @FXML
    lateinit var endDateComboBox: ComboBox<String>

    @FXML
    lateinit var switchDataButton: Button

    @FXML
    lateinit var comboBoxGridPane: GridPane

    protected var currentFlockComplete: FlockComplete? = null
    protected var cumulativeDepletion: List<Int> = emptyList()
    protected var startingDate: Date? = null
    protected var startingCount: Int? = null
    protected val dateFormatter = DateTimeFormatter.ofPattern("MMMM d")

    // Map to store formatted strings to actual LocalDate objects
    protected val dateStringToLocalDateMap = mutableMapOf<String, LocalDate>()

    /**
     * Abstract method to get the flock to display.
     * Subclasses implement this to return either the current flock or the latest flock.
     */
    protected abstract fun getFlockToDisplay(): FlockComplete?

    protected fun initializeFlockData() {
        currentFlockComplete = getFlockToDisplay()

        if (currentFlockComplete == null) {
            // No flock to display
            return
        }

        setupLabels()
        loadFlockDetails()
        setupTableColumns()
        clearTableSelection()
        setupComboBoxes()
        setupCharts()
        initializeViewState()
    }

    protected fun setupLabels() {
        val startDate = currentFlockComplete?.flock?.startingDate
        val startCount = currentFlockComplete?.flock?.startingCount ?: 0

        startingDate = startDate
        startingCount = startCount

        val mortalityRateData = ReadMortalityRate.calculateMortalityRateForFlock(
            getConnection(),
            currentFlockComplete!!.flock.startingDate
        )

        flockNameLabel.text = GeneralUtil.formatDatePretty(startingDate?.toLocalDate())
        dateStartedLabel.text = startingDate.toString()
        quantityStartedLabel.text = startingCount.toString()
        mortalityRateLabel.text = "${"%.2f".format(mortalityRateData.mortalityRate)}%"
        currentCountLabel.text = "${mortalityRateData.curCount}"
    }

    protected fun setupTableColumns() {
        colDate.cellValueFactory = PropertyValueFactory("fdDate")
        colDepletion.cellValueFactory = PropertyValueFactory("depletedCount")
    }

    protected fun loadFlockDetails() {
        val latestDetail = ReadFlockDetails.getMostRecent(getConnection(), startingDate)

        if (latestDetail != null) {
            val flockDetailsList = currentFlockComplete!!.flockDetails.sortedBy { it.fdDate }
            computeCumulativeDepletion(flockDetailsList)
            populateTable(flockDetailsList)
            setupChickenCountColumn()
        }
    }

    protected fun computeCumulativeDepletion(flockDetailsList: List<FlockDetails>) {
        cumulativeDepletion = flockDetailsList.runningFold(0) { acc, detail ->
            acc + detail.depletedCount
        }.drop(1)
    }

    protected fun setupChickenCountColumn() {
        colChickenCount.setCellValueFactory { cellData ->
            val rowIndex = flockRecordsTableView.items.indexOf(cellData.value)

            val remainingChickens: Int = if (rowIndex >= 0 && rowIndex < cumulativeDepletion.size) {
                (startingCount ?: 0) - cumulativeDepletion[rowIndex]
            } else {
                startingCount ?: 0
            }

            SimpleIntegerProperty(remainingChickens).asObject()
        }
    }

    protected fun populateTable(flockDetailsList: List<FlockDetails>) {
        val observableList = FXCollections.observableArrayList(flockDetailsList)
        flockRecordsTableView.items = observableList
    }

    protected fun clearTableSelection() {
        flockRecordsTableView.selectionModel.clearSelection()
        flockRecordsTableView.focusModel.focus(-1)
    }

    protected fun setupCharts() {
        val flockDetailsList = currentFlockComplete?.flockDetails?.sortedBy { it.fdDate }

        if (flockDetailsList != null && flockDetailsList.isNotEmpty()) {
            setupMortalityBarChart(flockDetailsList)
            setupMortalityPieChart()
        } else {
            setupEmptyMortalityBarChart()
            setupMortalityPieChartNoMortalities()
        }
    }

    protected fun setupEmptyMortalityBarChart(startDate: LocalDate? = null, endDate: LocalDate? = null, fillMissingDays: Boolean = false) {
        mortalityBarChart.data.clear()

        val series = XYChart.Series<String, Number>()
        series.name = "Daily Mortality"

        if (fillMissingDays && startDate != null && endDate != null) {
            val formatter = DateTimeFormatter.ofPattern("MMM dd")
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt()

            for (i in 0..daysBetween) {
                val currentDate = startDate.plusDays(i.toLong())
                series.data.add(XYChart.Data(currentDate.format(formatter), 0))
            }
        }

        mortalityBarChart.data.add(series)
        mortalityBarChart.animated = false
        mortalityBarChart.isLegendVisible = true
    }

    protected fun setupMortalityBarChart(flockDetailsList: List<FlockDetails>, fillMissingDays: Boolean = false, startDate: LocalDate? = null, endDate: LocalDate? = null) {
        mortalityBarChart.data.clear()

        val series = XYChart.Series<String, Number>()
        series.name = "Daily Mortality"

        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        if (fillMissingDays && startDate != null && endDate != null) {
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt()

            for (i in 0..daysBetween) {
                val currentDate = startDate.plusDays(i.toLong())

                val existingDetail = flockDetailsList.find { detail ->
                    detail.fdDate?.toLocalDate()?.equals(currentDate) == true
                }

                if (existingDetail != null) {
                    series.data.add(XYChart.Data(existingDetail.fdDate.toLocalDate().format(formatter), existingDetail.depletedCount))
                } else {
                    series.data.add(XYChart.Data(currentDate.format(formatter), 0))
                }
            }
        } else {
            flockDetailsList.forEach { detail ->
                if (detail.fdDate != null) {
                    val dateStr = detail.fdDate.toLocalDate().format(formatter)
                    series.data.add(XYChart.Data(dateStr, detail.depletedCount))
                }
            }
        }

        mortalityBarChart.data.add(series)
        mortalityBarChart.animated = false
        mortalityBarChart.isLegendVisible = true
    }

    protected fun setupMortalityPieChart() {
        mortalityPieChart.data.clear()

        val mortalityRateData = ReadMortalityRate.calculateMortalityRateForFlock(
            getConnection(),
            currentFlockComplete!!.flock.startingDate
        )

        val totalDead = startingCount!! - mortalityRateData.curCount
        val totalAlive = mortalityRateData.curCount

        val deadSlice = PieChart.Data("Dead ($totalDead)", totalDead.toDouble())
        val aliveSlice = PieChart.Data("Alive ($totalAlive)", totalAlive.toDouble())

        mortalityPieChart.data.addAll(deadSlice, aliveSlice)
        applyCustomColorsToChart()

        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = true
    }

    protected fun setupMortalityPieChartNoMortalities() {
        mortalityPieChart.data.clear()

        val totalAlive = startingCount ?: 0
        val aliveSlice = PieChart.Data("Alive ($totalAlive)", totalAlive.toDouble())

        mortalityPieChart.data.add(aliveSlice)

        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = false

        Platform.runLater {
            if (mortalityPieChart.data.isNotEmpty()) {
                val pieNode = mortalityPieChart.data[0].node
                pieNode?.style = "-fx-pie-color: rgba(40, 167, 69, 1);"
            }
        }
    }

    protected fun applyCustomColorsToChart() {
        val colors = listOf("rgba(220, 53, 69, 1)", "rgba(40, 167, 69, 1)")

        mortalityPieChart.data.forEachIndexed { index, data ->
            if (index < colors.size) {
                val color = colors[index]
                val pieNode = data.node
                pieNode?.style = "-fx-pie-color: $color;"
            }
        }
    }

    protected fun setupComboBoxes() {
        selectFreqComboBox.items.addAll("Daily", "Weekly")
        selectFreqComboBox.value = "Daily"

        selectFreqComboBox.setOnAction { onFrequencyChanged() }
        startDateComboBox.setOnAction { onStartDateChanged() }
        endDateComboBox.setOnAction { onEndDateChanged() }

        populateStartDateComboBox()
    }

    protected fun onFrequencyChanged() {
        startDateComboBox.value = null
        endDateComboBox.value = null
        endDateComboBox.items.clear()

        populateStartDateComboBox()
        hideCharts()
    }

    protected fun populateStartDateComboBox() {
        startDateComboBox.items.clear()
        dateStringToLocalDateMap.clear()

        val flockStart = startingDate?.toLocalDate() ?: return
        val frequency = selectFreqComboBox.value ?: "Daily"

        if (frequency == "Daily") {
            val flockStartFormatted = flockStart.format(dateFormatter)
            startDateComboBox.items.add(flockStartFormatted)
            dateStringToLocalDateMap[flockStartFormatted] = flockStart

            val flockDetailDates = currentFlockComplete?.flockDetails
                ?.filter { it.fdDate != null }
                ?.map { it.fdDate.toLocalDate() }
                ?.distinct()
                ?.sorted() ?: emptyList()

            flockDetailDates.forEach { date ->
                val formattedDate = date.format(dateFormatter)
                if (!dateStringToLocalDateMap.containsKey(formattedDate)) {
                    startDateComboBox.items.add(formattedDate)
                    dateStringToLocalDateMap[formattedDate] = date
                }
            }
        } else {
            val allDates = mutableListOf(flockStart)

            val flockDetailDates = currentFlockComplete?.flockDetails
                ?.filter { it.fdDate != null }
                ?.map { it.fdDate.toLocalDate() }
                ?.distinct() ?: emptyList()

            allDates.addAll(flockDetailDates)
            allDates.sort()

            val weekStartDates = mutableSetOf<LocalDate>()
            allDates.forEach { date ->
                val daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(flockStart, date)
                val weekNumber = (daysSinceStart / 7).toInt()
                val weekStartDate = flockStart.plusWeeks(weekNumber.toLong())
                weekStartDates.add(weekStartDate)
            }

            weekStartDates.sorted().forEach { weekStart ->
                val formattedDate = weekStart.format(dateFormatter)
                startDateComboBox.items.add(formattedDate)
                dateStringToLocalDateMap[formattedDate] = weekStart
            }
        }
    }

    protected fun onStartDateChanged() {
        val selectedStart = startDateComboBox.value
        if (selectedStart == null) {
            endDateComboBox.items.clear()
            endDateComboBox.value = null
            hideCharts()
            return
        }

        val previousEndDate = endDateComboBox.value
        endDateComboBox.items.clear()

        populateEndDateComboBox(selectedStart)

        if (previousEndDate != null && endDateComboBox.items.contains(previousEndDate)) {
            endDateComboBox.value = previousEndDate
            updateChartsWithDateRange()
        } else {
            endDateComboBox.value = null
            hideCharts()
        }
    }

    protected fun populateEndDateComboBox(startDateStr: String) {
        val flockStart = startingDate?.toLocalDate() ?: return
        val startDate = dateStringToLocalDateMap[startDateStr] ?: return
        val frequency = selectFreqComboBox.value ?: "Daily"

        if (frequency == "Daily") {
            for (i in 1..7) {
                val date = startDate.plusDays(i.toLong())
                val formattedDate = date.format(dateFormatter)
                endDateComboBox.items.add(formattedDate)
                dateStringToLocalDateMap[formattedDate] = date
            }
        } else {
            val weekEnd = startDate.plusDays(6)

            val datesInWeek = currentFlockComplete?.flockDetails
                ?.filter { it.fdDate != null }
                ?.map { it.fdDate.toLocalDate() }
                ?.filter { date ->
                    !date.isBefore(startDate) && !date.isAfter(weekEnd)
                } ?: emptyList()

            if (datesInWeek.isNotEmpty()) {
                val formattedDate = weekEnd.format(dateFormatter)
                endDateComboBox.items.add(formattedDate)
                dateStringToLocalDateMap[formattedDate] = weekEnd
            }

            val allDates = currentFlockComplete?.flockDetails
                ?.filter { it.fdDate != null }
                ?.map { it.fdDate.toLocalDate() }
                ?.filter { it.isAfter(weekEnd) }
                ?.distinct()
                ?.sorted() ?: emptyList()

            val addedWeekEnds = mutableSetOf<LocalDate>()
            allDates.forEach { date ->
                val daysSinceFlockStart = java.time.temporal.ChronoUnit.DAYS.between(flockStart, date)
                val weekNumber = (daysSinceFlockStart / 7).toInt()
                val weekStartForThisDate = flockStart.plusWeeks(weekNumber.toLong())
                val weekEndForThisDate = weekStartForThisDate.plusDays(6)

                if (weekStartForThisDate.isAfter(startDate) && !addedWeekEnds.contains(weekEndForThisDate)) {
                    val formattedDate = weekEndForThisDate.format(dateFormatter)
                    endDateComboBox.items.add(formattedDate)
                    dateStringToLocalDateMap[formattedDate] = weekEndForThisDate
                    addedWeekEnds.add(weekEndForThisDate)
                }
            }
        }
    }

    protected fun onEndDateChanged() {
        val startDate = startDateComboBox.value
        val endDate = endDateComboBox.value

        if (startDate != null && endDate != null) {
            updateChartsWithDateRange()
        } else {
            hideCharts()
        }
    }

    protected fun updateChartsWithDateRange() {
        val startDateStr = startDateComboBox.value ?: return
        val endDateStr = endDateComboBox.value ?: return

        val startDate = dateStringToLocalDateMap[startDateStr] ?: return
        val endDate = dateStringToLocalDateMap[endDateStr] ?: return
        val frequency = selectFreqComboBox.value ?: "Daily"

        val filteredDetails = currentFlockComplete?.flockDetails
            ?.filter { it.fdDate != null }
            ?.filter {
                val detailDate = it.fdDate.toLocalDate()
                !detailDate.isBefore(startDate) && !detailDate.isAfter(endDate)
            }
            ?.sortedBy { it.fdDate }

        if (filteredDetails != null && filteredDetails.isNotEmpty()) {
            setupMortalityBarChart(filteredDetails, frequency == "Daily", startDate, endDate)
            setupMortalityPieChartForRange(filteredDetails)
            showCharts()
        } else {
            setupEmptyMortalityBarChart(startDate, endDate, frequency == "Daily")
            setupMortalityPieChartForDateRange(startDate, endDate)
            showCharts()
        }
    }

    protected fun setupMortalityPieChartForDateRange(rangeStartDate: LocalDate, rangeEndDate: LocalDate) {
        mortalityPieChart.data.clear()
        mortalityPieChart.layout()

        val allDetailsSorted = currentFlockComplete?.flockDetails
            ?.filter { it.fdDate != null }
            ?.sortedBy { it.fdDate } ?: emptyList()
        val detailsBeforeRange = allDetailsSorted.filter {
            it.fdDate.toLocalDate().isBefore(rangeStartDate)
        }
        val deathsBeforeRange = detailsBeforeRange.sumOf { it.depletedCount }
        val countAtRangeStart = (startingCount ?: 0) - deathsBeforeRange

        val totalDeathsInRange = 0
        val aliveAtRangeEnd = countAtRangeStart

        if (totalDeathsInRange > 0) {
            val deadSlice = PieChart.Data("Dead ($totalDeathsInRange)", totalDeathsInRange.toDouble())
            val aliveSlice = PieChart.Data("Alive ($aliveAtRangeEnd)", aliveAtRangeEnd.toDouble())
            mortalityPieChart.data.addAll(deadSlice, aliveSlice)
        } else {
            val aliveSlice = PieChart.Data("Alive ($aliveAtRangeEnd)", aliveAtRangeEnd.toDouble())
            mortalityPieChart.data.add(aliveSlice)
        }

        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = true

        Platform.runLater {
            if (totalDeathsInRange > 0) {
                applyCustomColorsToChart()
            } else {
                if (mortalityPieChart.data.isNotEmpty()) {
                    val pieNode = mortalityPieChart.data[0].node
                    pieNode?.style = "-fx-pie-color: rgba(40, 167, 69, 1);"
                }
            }
        }
    }

    protected fun setupMortalityPieChartForRange(flockDetailsList: List<FlockDetails>) {
        mortalityPieChart.data.clear()

        val totalDeathsInRange = flockDetailsList.sumOf { it.depletedCount }

        val allDetailsSorted = currentFlockComplete?.flockDetails
            ?.filter { it.fdDate != null }
            ?.sortedBy { it.fdDate } ?: emptyList()
        val detailsBeforeRange = allDetailsSorted.filter {
            it.fdDate.toLocalDate().isBefore(flockDetailsList.first().fdDate.toLocalDate())
        }
        val deathsBeforeRange = detailsBeforeRange.sumOf { it.depletedCount }
        val countAtRangeStart = (startingCount ?: 0) - deathsBeforeRange
        val aliveAtRangeEnd = countAtRangeStart - totalDeathsInRange

        val deadSlice = PieChart.Data("Dead ($totalDeathsInRange)", totalDeathsInRange.toDouble())
        val aliveSlice = PieChart.Data("Alive ($aliveAtRangeEnd)", aliveAtRangeEnd.toDouble())

        mortalityPieChart.data.addAll(deadSlice, aliveSlice)
        applyCustomColorsToChart()

        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = true
    }

    protected fun hideCharts() {
        mortalityBarChart.isVisible = false
        mortalityPieChart.isVisible = false
    }

    protected fun showCharts() {
        mortalityBarChart.isVisible = true
        mortalityPieChart.isVisible = true
    }

    protected fun initializeViewState() {
        flockRecordsTableView.isVisible = true
        comboBoxGridPane.isVisible = false
        mortalityBarChart.isVisible = false
        mortalityPieChart.isVisible = false
    }

    @FXML
    fun onSwitchDataButtonClicked() {
        if (flockRecordsTableView.isVisible) {
            flockRecordsTableView.isVisible = false
            comboBoxGridPane.isVisible = true
            mortalityBarChart.isVisible = true
            mortalityPieChart.isVisible = true
            switchDataButton.text = "View Table Data"
        } else {
            flockRecordsTableView.isVisible = true
            comboBoxGridPane.isVisible = false
            mortalityBarChart.isVisible = false
            mortalityPieChart.isVisible = false
            switchDataButton.text = "View Chart Data"
        }
    }
}

