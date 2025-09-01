package org.db_poultry.controller.flock

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.TilePane
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.Priority
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import org.db_poultry.util.FlockSingleton
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.pojo.FlockPOJO.FlockComplete
import org.db_poultry.pojo.FlockPOJO.Flock
import org.db_poultry.util.PopupUtil
import java.net.URL
import java.sql.Date
import java.util.ResourceBundle

class FlockGridHomeController: Initializable {

    @FXML
    private lateinit var selectFlockAnchorPane: AnchorPane

    @FXML
    private lateinit var mainTilePane: TilePane

    @FXML
    private lateinit var createFlockGridPane: GridPane

    @FXML
    private lateinit var exampleFlockGridPane: GridPane

    @FXML
    private lateinit var flockPageSpinner: Spinner<Int>

    @FXML
    private lateinit var yearComboBox: ComboBox<String>

    @FXML
    private lateinit var monthComboBox: ComboBox<String>

    @FXML
    private lateinit var dayTextField: TextField

    private var currentPage = 1
    private var totalPages = 1
    private var allFlockRecords: List<Pair<Date, FlockComplete>> = emptyList()
    private var filteredFlockRecords: List<Pair<Date, FlockComplete>> = emptyList()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initializeComboBoxes()
        loadAllFlockRecords()
        setSpinner()
        loadCurrentPage()
        setupDateFilterListeners()
    }

    private fun initializeComboBoxes() {
        // Initialize year combo box
        yearComboBox.items.addAll("All Year", "2020", "2021", "2022", "2023", "2024", "2025")
        yearComboBox.value = "All Year"

        // Initialize month combo box
        monthComboBox.items.addAll(
            "All Month", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        monthComboBox.value = "All Month"
    }

    private fun setupDateFilterListeners() {
        yearComboBox.setOnAction { applyDateFilter() }
        monthComboBox.setOnAction { applyDateFilter() }

        dayTextField.textProperty().addListener { _, oldValue, newValue ->
            println("Day text changed from '$oldValue' to '$newValue'")
            applyDateFilter()
        }

        // Focus lost listener as backup
        dayTextField.focusedProperty().addListener { _, _, focused ->
            if (!focused) {
                println("Day field lost focus, current text: '${dayTextField.text}'")
                applyDateFilter()
            }
        }
    }

    private fun applyDateFilter() {
        val year = yearComboBox.value
        val month = monthComboBox.value
        val day = dayTextField.text?.trim()

        val connection = DBConnect.getConnection()

        val filteredFlocks: List<Flock>? = when {
            // Full date search (day, month, year all specified)
            !day.isNullOrEmpty() && month != "All Month" && year != "All Year" -> {
                val searchQuery = "$month $day, $year"
                println("Searching for flocks with query: $searchQuery")
                ReadFlock.searchFlocks(connection, searchQuery)
            }
            // Month and year specified
            month != "All Month" && year != "All Year" -> {
                val searchQuery = "$month $year"
                ReadFlock.searchFlocks(connection, searchQuery)
            }
            // Only year specified
            year != "All Year" -> {
                ReadFlock.searchFlocks(connection, year)
            }
            // Only month specified
            month != "All Month" -> {
                ReadFlock.searchFlocks(connection, month)
            }
            // No filters applied
            else -> null
        }

        if (filteredFlocks != null) {
            // Convert filtered flocks to FlockComplete and create pairs
            filteredFlockRecords = filteredFlocks.mapNotNull { flock ->
                val flockMap = ReadFlock.allByDate(connection)
                val flockComplete = flockMap?.get(flock.startingDate)
                if (flockComplete != null) {
                    Pair(flock.startingDate, flockComplete)
                } else null
            }.sortedByDescending { it.first }
        } else {
            // No filter, use all records
            filteredFlockRecords = allFlockRecords
        }

        // Update pagination
        totalPages = if (filteredFlockRecords.isNotEmpty()) {
            (filteredFlockRecords.size + 6) / 7
        } else {
            1
        }

        currentPage = 1
        setSpinner()
        loadCurrentPage()
    }

    private fun loadAllFlockRecords() {
        val flockMap = ReadFlock.allByDate(DBConnect.getConnection())
        allFlockRecords = flockMap?.toList()?.sortedByDescending { it.first } ?: emptyList()
        filteredFlockRecords = allFlockRecords

        totalPages = if (allFlockRecords.isNotEmpty()) {
            (allFlockRecords.size + 6) / 7
        } else {
            1
        }
    }

    private fun loadCurrentPage() {
        resetMainTilePane()

        if (filteredFlockRecords.isEmpty()) return

        val recordsToShow = getRecordsForPage(currentPage)

        for ((_, flockComplete) in recordsToShow) {
            val gridPane = createFlockGridPane(flockComplete)
            mainTilePane.children.add(gridPane)
        }
    }

    private fun getRecordsForPage(page: Int): List<Pair<Date, FlockComplete>> {
        val startIndex = (page - 1) * 7
        return filteredFlockRecords.drop(startIndex).take(7)
    }

    private fun resetMainTilePane() {
        val childrenToKeep = mainTilePane.children.filter { child ->
            child == createFlockGridPane
        }

        mainTilePane.children.clear()
        mainTilePane.children.addAll(childrenToKeep)
    }

    private fun setSpinner() {
        flockPageSpinner.styleClass.add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL)
        val valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxOf(1, totalPages), currentPage)
        flockPageSpinner.valueFactory = valueFactory
        flockPageSpinner.editor.alignment = javafx.geometry.Pos.CENTER

        flockPageSpinner.valueProperty().addListener { _, _, newValue ->
            currentPage = newValue as Int
            loadCurrentPage()
        }
    }

    private fun createFlockGridPane(flockComplete: FlockComplete): GridPane {
        val gridPane = GridPane().apply {
            prefHeight = 101.0
            prefWidth = 455.0
            styleClass.add("grid-supply")
        }

        val col1 = ColumnConstraints().apply {
            hgrow = Priority.SOMETIMES
            minWidth = 10.0
            maxWidth = 313.0
            prefWidth = 228.0
        }
        val col2 = ColumnConstraints().apply {
            hgrow = Priority.SOMETIMES
            minWidth = 10.0
            maxWidth = 222.0
            percentWidth = 30.0
            prefWidth = 222.0
        }
        gridPane.columnConstraints.addAll(col1, col2)

        repeat(5) {
            val row = RowConstraints().apply {
                minHeight = 10.0
                prefHeight = 30.0
                vgrow = Priority.SOMETIMES
            }
            gridPane.rowConstraints.add(row)
        }

        val formattedDate = GeneralUtil.formatDatePretty(flockComplete.flock.startingDate.toLocalDate())
        val dateLabel = Label(formattedDate).apply {
            styleClass.add("supply-label")
        }
        GridPane.setHalignment(dateLabel, HPos.CENTER)
        GridPane.setRowSpan(dateLabel, 2)
        gridPane.add(dateLabel, 0, 1)

        val currentCount = flockComplete.flock.startingCount -
                flockComplete.flockDetails.sumOf { it.depletedCount }
        val countLabel = Label("Current Count: $currentCount").apply {
            styleClass.add("h5")
        }
        GridPane.setHalignment(countLabel, HPos.CENTER)
        gridPane.add(countLabel, 0, 3)

        val viewHistoryButton = Button("View History").apply {
            maxHeight = Double.MAX_VALUE
            maxWidth = Double.MAX_VALUE
            styleClass.addAll("main-button-reversed", "h6-bold")
            setOnAction { navigateToViewFlockDetails() }
            setOnMousePressed {
                FlockSingleton.setCurrentFlockComplete(flockComplete)
            }
        }
        GridPane.setHalignment(viewHistoryButton, HPos.CENTER)
        GridPane.setMargin(viewHistoryButton, Insets(20.0, 10.0, 20.0, 10.0))

        val isRecentMostFlock = filteredFlockRecords.isNotEmpty() &&
                filteredFlockRecords.first().second.flock.startingDate == flockComplete.flock.startingDate

        if (isRecentMostFlock) {
            gridPane.add(viewHistoryButton, 1, 1)

            val updateRecordButton = Button("Update Record").apply {
                maxHeight = Double.MAX_VALUE
                maxWidth = Double.MAX_VALUE
                styleClass.addAll("main-button-reversed", "h6-bold")
                setOnAction { navigateToCreateFlockDetails() }
                setOnMousePressed {
                    FlockSingleton.setCurrentFlockComplete(flockComplete)
                }
            }
            GridPane.setHalignment(updateRecordButton, HPos.CENTER)
            GridPane.setMargin(updateRecordButton, Insets(10.0, 10.0, 10.0, 10.0))
            gridPane.add(updateRecordButton, 1, 3)
        } else {
            GridPane.setRowSpan(viewHistoryButton, 3)
            gridPane.add(viewHistoryButton, 1, 1)
        }

        return gridPane
    }

    @FXML
    fun navigateToCreateFlock() {
        PopupUtil.showContentPopup("/fxml/content_create_new_flock.fxml")
    }

    @FXML
    fun navigateToCreateFlockDetails() {
        PopupUtil.showContentPopup("/fxml/content_create_flock_details.fxml")
    }

    @FXML
    fun navigateToViewFlockDetails() {
        GeneralUtil.navigateToMainContent(selectFlockAnchorPane, "/fxml/content_view_flock_details.fxml")
    }
}