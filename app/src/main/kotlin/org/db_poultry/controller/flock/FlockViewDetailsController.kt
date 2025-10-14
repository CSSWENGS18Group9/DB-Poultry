package org.db_poultry.controller.flock

import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.chart.BarChart
import javafx.scene.chart.PieChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.flockDetailsDAO.ReadFlockDetails
import org.db_poultry.db.reportDAO.ReadMortalityRate
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import org.db_poultry.pojo.FlockPOJO.FlockDetails
import org.db_poultry.util.FlockSingleton
import org.db_poultry.util.GeneralUtil
import java.net.URL
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class FlockViewDetailsController : Initializable {

    @FXML
    lateinit var mainAnchorPane: AnchorPane

    @FXML
    lateinit var dataGridPane: GridPane

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

    private var currentFlockComplete: FlockComplete? = null
    private var cumulativeDepletion: List<Int> = emptyList()
    private var startingDate: Date? = null
    private var startingCount: Int? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM d")

    // Map to store formatted strings to actual LocalDate objects
    private val dateStringToLocalDateMap = mutableMapOf<String, LocalDate>()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        setupLabels()
        loadFlockDetails()
        setupTableColumns()
        clearTableSelection()
        setupComboBoxes()
        setupCharts()
        initializeViewState()
    }

    private fun setupLabels() {
        val currentFlock = FlockSingleton.getCurrentFlockComplete()
        currentFlockComplete = currentFlock

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

    private fun setupTableColumns() {
        colDate.cellValueFactory = PropertyValueFactory("fdDate")
        colDepletion.cellValueFactory = PropertyValueFactory("depletedCount")
    }

    private fun loadFlockDetails() {
        val latestDetail = ReadFlockDetails.getMostRecent(getConnection(), startingDate)

        if (latestDetail != null) {
            val flockDetailsList = currentFlockComplete!!.flockDetails.sortedBy { it.fdDate }
            computeCumulativeDepletion(flockDetailsList)
            populateTable(flockDetailsList)
            setupChickenCountColumn()
        }
        // If latestDetail is null, flockDetails is empty (newly created flock)
        // Charts will be displayed empty, which is handled in setupCharts()
    }

    private fun computeCumulativeDepletion(flockDetailsList: List<FlockDetails>) {
        cumulativeDepletion = flockDetailsList.runningFold(0) { acc, detail ->
            acc + detail.depletedCount
        }.drop(1)
    }

    private fun setupChickenCountColumn() {
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

    private fun populateTable(flockDetailsList: List<FlockDetails>) {
        val observableList = FXCollections.observableArrayList(flockDetailsList)
        flockRecordsTableView.items = observableList
    }

    private fun clearTableSelection() {
        flockRecordsTableView.selectionModel.clearSelection()
        flockRecordsTableView.focusModel.focus(-1)
    }

    private fun setupCharts() {
        val flockDetailsList = currentFlockComplete?.flockDetails?.sortedBy { it.fdDate }

        if (flockDetailsList != null && flockDetailsList.isNotEmpty()) {
            // Flock has details, show actual data
            setupMortalityBarChart(flockDetailsList)
            setupMortalityPieChart()
        } else {
            // Flock is newly created with no details yet
            // Show empty bar chart and pie chart with no mortalities
            setupEmptyMortalityBarChart()
            setupMortalityPieChartNoMortalities()
        }
    }

    private fun setupEmptyMortalityBarChart(startDate: LocalDate? = null, endDate: LocalDate? = null, fillMissingDays: Boolean = false) {
        // Clear any existing data
        mortalityBarChart.data.clear()

        // Create an empty series
        val series = XYChart.Series<String, Number>()
        series.name = "Daily Mortality"

        if (fillMissingDays && startDate != null && endDate != null) {
            // Fill in all days in the range with zero values
            val formatter = DateTimeFormatter.ofPattern("MMM dd")
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt()

            for (i in 0..daysBetween) {
                val currentDate = startDate.plusDays(i.toLong())
                series.data.add(XYChart.Data(currentDate.format(formatter), 0))
            }
        }

        // Add the empty series to the chart
        mortalityBarChart.data.add(series)

        // Make the chart more readable
        mortalityBarChart.animated = false
        mortalityBarChart.isLegendVisible = true
    }

    private fun setupMortalityBarChart(flockDetailsList: List<FlockDetails>, fillMissingDays: Boolean = false, startDate: LocalDate? = null, endDate: LocalDate? = null) {
        // Clear any existing data
        mortalityBarChart.data.clear()

        // Create a series for the mortality data
        val series = XYChart.Series<String, Number>()
        series.name = "Daily Mortality"

        // Format for date display
        val formatter = DateTimeFormatter.ofPattern("MMM dd")

        if (fillMissingDays && startDate != null && endDate != null) {
            // Fill in missing days with zero depletion
            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate).toInt()

            for (i in 0..daysBetween) {
                val currentDate = startDate.plusDays(i.toLong())

                // Check if there's already data for this date
                val existingDetail = flockDetailsList.find { detail ->
                    detail.fdDate?.toLocalDate()?.equals(currentDate) == true
                }

                if (existingDetail != null) {
                    // Use the existing data
                    series.data.add(XYChart.Data(existingDetail.fdDate.toLocalDate().format(formatter), existingDetail.depletedCount))
                } else {
                    // No data for this date, add a zero entry
                    series.data.add(XYChart.Data(currentDate.format(formatter), 0))
                }
            }
        } else {
            // Add data points for each flock detail (skip null dates)
            flockDetailsList.forEach { detail ->
                if (detail.fdDate != null) {
                    val dateStr = detail.fdDate.toLocalDate().format(formatter)
                    series.data.add(XYChart.Data(dateStr, detail.depletedCount))
                }
            }
        }

        // Add the series to the chart
        mortalityBarChart.data.add(series)

        // Make the chart more readable
        mortalityBarChart.animated = false
        mortalityBarChart.isLegendVisible = true
    }

    private fun setupMortalityPieChart() {
        // Clear any existing data
        mortalityPieChart.data.clear()

        val mortalityRateData = ReadMortalityRate.calculateMortalityRateForFlock(
            getConnection(),
            currentFlockComplete!!.flock.startingDate
        )

        val totalDead = startingCount!! - mortalityRateData.curCount
        val totalAlive = mortalityRateData.curCount

        // Create pie chart slices
        val deadSlice = PieChart.Data("Dead ($totalDead)", totalDead.toDouble())
        val aliveSlice = PieChart.Data("Alive ($totalAlive)", totalAlive.toDouble())

        // Add slices to chart
        mortalityPieChart.data.addAll(deadSlice, aliveSlice)

        // Apply custom colors to the pie slices
        applyCustomColorsToChart()

        // Set properties for better display
        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = true
    }

    private fun setupMortalityPieChartNoMortalities() {
        // Clear any existing data
        mortalityPieChart.data.clear()

        // For a newly created flock with no details, show all chickens as alive
        val totalAlive = startingCount ?: 0
        val totalDead = 0

        // Only add slices with non-zero values to prevent label overlap
        // When there are no mortalities, only show the "Alive" slice
        val aliveSlice = PieChart.Data("Alive ($totalAlive)", totalAlive.toDouble())

        // Add only the alive slice since dead count is 0
        mortalityPieChart.data.add(aliveSlice)

        // Set properties for better display - disable animation on first render
        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = false

        // Apply custom colors after the chart is fully rendered to prevent label overlap
        Platform.runLater {
            // Apply green color to the alive slice
            if (mortalityPieChart.data.isNotEmpty()) {
                val pieNode = mortalityPieChart.data[0].node
                pieNode?.style = "-fx-pie-color: rgba(40, 167, 69, 1);"
            }
        }
    }

    private fun applyCustomColorsToChart() {
        // Apply custom colors to the first two slices of the pie chart
        val colors = listOf("rgba(220, 53, 69, 1)", "rgba(40, 167, 69, 1)") // Red for dead, green for alive

        mortalityPieChart.data.forEachIndexed { index, data ->
            if (index < colors.size) {
                val color = colors[index]
                val pieNode = data.node
                pieNode?.style = "-fx-pie-color: $color;"
            }
        }
    }

    private fun setupComboBoxes() {
        // Initialize frequency ComboBox
        selectFreqComboBox.items.addAll("Daily", "Weekly")
        selectFreqComboBox.value = "Daily"

        // Set up listeners
        selectFreqComboBox.setOnAction { onFrequencyChanged() }
        startDateComboBox.setOnAction { onStartDateChanged() }
        endDateComboBox.setOnAction { onEndDateChanged() }

        // Initialize date ComboBoxes
        populateStartDateComboBox()
    }

    private fun onFrequencyChanged() {
        // Reset date selections
        startDateComboBox.value = null
        endDateComboBox.value = null
        endDateComboBox.items.clear()

        // Repopulate start date based on frequency
        populateStartDateComboBox()

        // Hide charts until valid date range is selected
        hideCharts()
    }

    private fun populateStartDateComboBox() {
        startDateComboBox.items.clear()
        dateStringToLocalDateMap.clear()

        val flockStart = startingDate?.toLocalDate() ?: return
        val frequency = selectFreqComboBox.value ?: "Daily"

        if (frequency == "Daily") {
            // Add the flock starting date
            val flockStartFormatted = flockStart.format(dateFormatter)
            startDateComboBox.items.add(flockStartFormatted)
            dateStringToLocalDateMap[flockStartFormatted] = flockStart

            // Add all dates from FlockDetails that have non-null fdDate
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
        } else { // Weekly
            // For weekly, we need to group dates into weeks
            // Get all available dates (starting date + all flock detail dates)
            val allDates = mutableListOf(flockStart)

            val flockDetailDates = currentFlockComplete?.flockDetails
                ?.filter { it.fdDate != null }
                ?.map { it.fdDate.toLocalDate() }
                ?.distinct() ?: emptyList()

            allDates.addAll(flockDetailDates)
            allDates.sort()

            // Group dates by week (starting from flock start date)
            val weekStartDates = mutableSetOf<LocalDate>()
            allDates.forEach { date ->
                // Calculate which week this date belongs to
                val daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(flockStart, date)
                val weekNumber = (daysSinceStart / 7).toInt()
                val weekStartDate = flockStart.plusWeeks(weekNumber.toLong())
                weekStartDates.add(weekStartDate)
            }

            // Add week start dates to combo box
            weekStartDates.sorted().forEach { weekStart ->
                val formattedDate = weekStart.format(dateFormatter)
                startDateComboBox.items.add(formattedDate)
                dateStringToLocalDateMap[formattedDate] = weekStart
            }
        }
    }

    private fun onStartDateChanged() {
        val selectedStart = startDateComboBox.value
        if (selectedStart == null) {
            endDateComboBox.items.clear()
            endDateComboBox.value = null
            hideCharts()
            return
        }

        // Clear previous end date selection
        val previousEndDate = endDateComboBox.value
        endDateComboBox.items.clear()

        // Populate end date ComboBox based on start date
        populateEndDateComboBox(selectedStart)

        // Check if previous end date is still valid
        if (previousEndDate != null && endDateComboBox.items.contains(previousEndDate)) {
            endDateComboBox.value = previousEndDate
            updateChartsWithDateRange()
        } else {
            // Clear end date if start date is higher than previous end date
            endDateComboBox.value = null
            hideCharts()
        }
    }

    private fun populateEndDateComboBox(startDateStr: String) {
        val flockStart = startingDate?.toLocalDate() ?: return

        val startDate = dateStringToLocalDateMap[startDateStr] ?: return
        val frequency = selectFreqComboBox.value ?: "Daily"

        if (frequency == "Daily") {
            // End date should be within 7 days from start date
            // Add all dates from start+1 to start+7, even if there's no data
            for (i in 1..7) {
                val date = startDate.plusDays(i.toLong())
                val formattedDate = date.format(dateFormatter)
                endDateComboBox.items.add(formattedDate)
                dateStringToLocalDateMap[formattedDate] = date
            }
        } else { // Weekly
            // End date should be 6 days after start (completing the week) or the last day with data in that week
            val weekEnd = startDate.plusDays(6)

            // Check if there's any data in this week
            val datesInWeek = currentFlockComplete?.flockDetails
                ?.filter { it.fdDate != null }
                ?.map { it.fdDate.toLocalDate() }
                ?.filter { date ->
                    !date.isBefore(startDate) && !date.isAfter(weekEnd)
                } ?: emptyList()

            if (datesInWeek.isNotEmpty()) {
                // Use the actual week end date if there's data
                val formattedDate = weekEnd.format(dateFormatter)
                endDateComboBox.items.add(formattedDate)
                dateStringToLocalDateMap[formattedDate] = weekEnd
            }

            // Add subsequent week end dates where there's data
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

    private fun onEndDateChanged() {
        val startDate = startDateComboBox.value
        val endDate = endDateComboBox.value

        if (startDate != null && endDate != null) {
            updateChartsWithDateRange()
        } else {
            hideCharts()
        }
    }

    private fun updateChartsWithDateRange() {
        val startDateStr = startDateComboBox.value ?: return
        val endDateStr = endDateComboBox.value ?: return

        val startDate = dateStringToLocalDateMap[startDateStr] ?: return
        val endDate = dateStringToLocalDateMap[endDateStr] ?: return
        val frequency = selectFreqComboBox.value ?: "Daily"

        // Filter flock details by date range (skip null dates)
        val filteredDetails = currentFlockComplete?.flockDetails
            ?.filter {
                it.fdDate != null
            }
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
            // No data in the selected range, show empty charts
            setupEmptyMortalityBarChart(startDate, endDate, frequency == "Daily")
            setupMortalityPieChartForDateRange(startDate, endDate)
            showCharts()
        }
    }

    private fun setupMortalityPieChartForDateRange(rangeStartDate: LocalDate, rangeEndDate: LocalDate) {
        // Clear any existing data
        mortalityPieChart.data.clear()

        // Force layout update to ensure old nodes are removed
        mortalityPieChart.layout()

        // Get the chicken count at the start of the range (skip null dates)
        val allDetailsSorted = currentFlockComplete?.flockDetails
            ?.filter { it.fdDate != null }
            ?.sortedBy { it.fdDate } ?: emptyList()
        val detailsBeforeRange = allDetailsSorted.filter {
            it.fdDate.toLocalDate().isBefore(rangeStartDate)
        }
        val deathsBeforeRange = detailsBeforeRange.sumOf { it.depletedCount }
        val countAtRangeStart = (startingCount ?: 0) - deathsBeforeRange

        // If no deaths in this range, show all as alive
        val totalDeathsInRange = 0
        val aliveAtRangeEnd = countAtRangeStart

        // Only add slices with non-zero values to prevent label overlap
        if (totalDeathsInRange > 0) {
            // Both slices when there are deaths
            val deadSlice = PieChart.Data("Dead ($totalDeathsInRange)", totalDeathsInRange.toDouble())
            val aliveSlice = PieChart.Data("Alive ($aliveAtRangeEnd)", aliveAtRangeEnd.toDouble())
            mortalityPieChart.data.addAll(deadSlice, aliveSlice)
        } else {
            // Only alive slice when there are no deaths
            val aliveSlice = PieChart.Data("Alive ($aliveAtRangeEnd)", aliveAtRangeEnd.toDouble())
            mortalityPieChart.data.add(aliveSlice)
        }

        // Set properties for better display
        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = true

        // Apply custom colors after the chart is fully rendered to prevent label overlap
        Platform.runLater {
            if (totalDeathsInRange > 0) {
                applyCustomColorsToChart()
            } else {
                // Only color the alive slice
                if (mortalityPieChart.data.isNotEmpty()) {
                    val pieNode = mortalityPieChart.data[0].node
                    pieNode?.style = "-fx-pie-color: rgba(40, 167, 69, 1);"
                }
            }
        }
    }

    private fun setupMortalityPieChartForRange(flockDetailsList: List<FlockDetails>) {
        // Clear any existing data
        mortalityPieChart.data.clear()

        // Calculate total deaths in the selected range
        val totalDeathsInRange = flockDetailsList.sumOf { it.depletedCount }

        // Get the chicken count at the start of the range (skip null dates)
        val allDetailsSorted = currentFlockComplete?.flockDetails
            ?.filter { it.fdDate != null }
            ?.sortedBy { it.fdDate } ?: emptyList()
        val detailsBeforeRange = allDetailsSorted.filter {
            it.fdDate.toLocalDate().isBefore(flockDetailsList.first().fdDate.toLocalDate())
        }
        val deathsBeforeRange = detailsBeforeRange.sumOf { it.depletedCount }
        val countAtRangeStart = (startingCount ?: 0) - deathsBeforeRange
        val aliveAtRangeEnd = countAtRangeStart - totalDeathsInRange

        // Create pie chart slices
        val deadSlice = PieChart.Data("Dead ($totalDeathsInRange)", totalDeathsInRange.toDouble())
        val aliveSlice = PieChart.Data("Alive ($aliveAtRangeEnd)", aliveAtRangeEnd.toDouble())

        // Add slices to chart
        mortalityPieChart.data.addAll(deadSlice, aliveSlice)

        // Apply custom colors to the pie slices
        applyCustomColorsToChart()

        // Set properties for better display
        mortalityPieChart.labelsVisible = true
        mortalityPieChart.animated = true
    }

    private fun hideCharts() {
        mortalityBarChart.isVisible = false
        mortalityPieChart.isVisible = false
    }

    private fun showCharts() {
        mortalityBarChart.isVisible = true
        mortalityPieChart.isVisible = true
    }

    private fun initializeViewState() {
        // Show table by default, hide charts and combo boxes
        flockRecordsTableView.isVisible = true
        comboBoxGridPane.isVisible = false
        mortalityBarChart.isVisible = false
        mortalityPieChart.isVisible = false
    }

    @FXML
    fun onSwitchDataButtonClicked() {
        if (flockRecordsTableView.isVisible) {
            // Currently showing table, switch to charts
            flockRecordsTableView.isVisible = false
            comboBoxGridPane.isVisible = true
            mortalityBarChart.isVisible = true
            mortalityPieChart.isVisible = true
            switchDataButton.text = "View Table Data"
        } else {
            // Currently showing charts, switch to table
            flockRecordsTableView.isVisible = true
            comboBoxGridPane.isVisible = false
            mortalityBarChart.isVisible = false
            mortalityPieChart.isVisible = false
            switchDataButton.text = "View Chart Data"
        }
    }

    @FXML
    fun backToViewFlocks() {
        GeneralUtil.navigateToMainContent(mainAnchorPane, "/fxml/content_home_flock_grid.fxml")
    }
}
