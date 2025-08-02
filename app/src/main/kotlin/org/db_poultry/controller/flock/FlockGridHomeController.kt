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
import org.db_poultry.util.FlockSingleton
import org.db_poultry.db.DBConnect
import org.db_poultry.db.flockDAO.ReadFlock
import org.db_poultry.pojo.FlockPOJO.FlockComplete
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

    private var currentPage = 1
    private var totalPages = 1
    private var allFlockRecords: List<Pair<Date, FlockComplete>> = emptyList()


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        loadAllFlockRecords()
        setSpinner()
        loadCurrentPage()
    }

    private fun loadAllFlockRecords() {
        val flockMap = ReadFlock.allByDate(DBConnect.getConnection())
        allFlockRecords = flockMap?.toList()?.sortedByDescending { it.first } ?: emptyList()

        totalPages = if (allFlockRecords.isNotEmpty()) {
            (allFlockRecords.size + 6) / 7
        } else {
            1
        }
    }

    private fun loadCurrentPage() {
        resetMainTilePane()

        if (allFlockRecords.isEmpty()) return

        val recordsToShow = getRecordsForPage(currentPage)

        for ((_, flockComplete) in recordsToShow) {
            val gridPane = createFlockGridPane(flockComplete)
            mainTilePane.children.add(gridPane)
        }
    }

    private fun getRecordsForPage(page: Int): List<Pair<Date, FlockComplete>> {
        val startIndex = (page - 1) * 7
        return allFlockRecords.drop(startIndex).take(7)
    }

    private fun resetMainTilePane() {
        val childrenToKeep = mainTilePane.children.filter { child ->
            child == createFlockGridPane /* ||  child == exampleFlockGridPane */ // DEBUGGING TO ENABLE
        }

        mainTilePane.children.clear()
        mainTilePane.children.addAll(childrenToKeep)
    }

    private fun setSpinner() {
        flockPageSpinner.styleClass.add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL)
        val valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxOf(1, totalPages), 1)
        flockPageSpinner.valueFactory = valueFactory
        flockPageSpinner.editor.alignment = javafx.geometry.Pos.CENTER

        // Add listener for page changes
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

        // Set up column constraints
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

        // Set up row constraints
        repeat(5) {
            val row = RowConstraints().apply {
                minHeight = 10.0
                prefHeight = 30.0
                vgrow = Priority.SOMETIMES
            }
            gridPane.rowConstraints.add(row)
        }

        // Create date label
        val formattedDate = GeneralUtil.formatDatePretty(flockComplete.flock.startingDate.toLocalDate())
        val dateLabel = Label(formattedDate).apply {
            styleClass.add("supply-label")
        }
        GridPane.setHalignment(dateLabel, HPos.CENTER)
        GridPane.setRowSpan(dateLabel, 2)
        gridPane.add(dateLabel, 0, 1)

        // Create current count label
        val currentCount = flockComplete.flock.startingCount -
                flockComplete.flockDetails.sumOf { it.depletedCount }
        val countLabel = Label("Current Count: $currentCount").apply {
            styleClass.add("h5")
        }
        GridPane.setHalignment(countLabel, HPos.CENTER)
        gridPane.add(countLabel, 0, 3)

        // Create buttons
        val viewHistoryButton = Button("View History").apply {
            maxHeight = Double.MAX_VALUE
            maxWidth = Double.MAX_VALUE
            styleClass.addAll("main-button-reversed", "h6-bold")
            setOnAction { navigateToViewFlockDetails() }
            setOnMousePressed { event ->
                FlockSingleton.setCurrentFlockComplete(flockComplete)
            }
        }
        GridPane.setHalignment(viewHistoryButton, HPos.CENTER)
        GridPane.setMargin(viewHistoryButton, Insets(10.0, 10.0, 10.0, 10.0))
        gridPane.add(viewHistoryButton, 1, 1)

        val updateRecordButton = Button("Update Record").apply {
            maxHeight = Double.MAX_VALUE
            maxWidth = Double.MAX_VALUE
            styleClass.addAll("main-button-reversed", "h6-bold")
            setOnAction { navigateToCreateFlockDetails() }
            setOnMousePressed { event ->
                FlockSingleton.setCurrentFlockComplete(flockComplete)
            }
        }
        GridPane.setHalignment(updateRecordButton, HPos.CENTER)
        GridPane.setMargin(updateRecordButton, Insets(10.0, 10.0, 10.0, 10.0))
        gridPane.add(updateRecordButton, 1, 3)

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