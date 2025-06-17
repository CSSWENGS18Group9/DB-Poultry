package org.db_poultry.controller

import org.db_poultry.util.SceneSwitcher
import org.db_poultry.util.GeneralUtil

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.AnchorPane

class ViewSuppliesController {

    @FXML
    private lateinit var currAmountCol: TableColumn<Any, Any>

    @FXML
    private lateinit var currDateLbl: Label

    @FXML
    private lateinit var dateHistoryBtn: Button

    @FXML
    private lateinit var qtyAddedCol: TableColumn<Any, Any>

    @FXML
    private lateinit var qtyDepletedCol: TableColumn<Any, Any>

    @FXML
    private lateinit var supplyHistoryBtn: Button

    @FXML
    private lateinit var supplyTypeCol: TableColumn<Any, Any>

    @FXML
    private lateinit var viewSuppliesLbl: Label

    @FXML
    private lateinit var viewSuppliesTable: TableView<Any>

    @FXML
    private lateinit var viewSupplyAnchorPane: AnchorPane

    @FXML
    fun switchToViewSupplyHistory(event: ActionEvent) {
        GeneralUtil.loadContentView(viewSupplyAnchorPane, "/fxml/content_view_supply_history.fxml")
    }

    @FXML
    fun switchToViewDateHistory(event: ActionEvent) {
        GeneralUtil.loadContentView(viewSupplyAnchorPane, "/fxml/content_view_date_history.fxml")
    }
}