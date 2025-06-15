package org.db_poultry.controller

import org.db_poultry.util.GeneralUtil

import javafx.fxml.FXML
import javafx.fxml.Initializable

import java.util.ResourceBundle
import java.net.URL

import java.time.LocalDate

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.image.ImageView
import javafx.scene.shape.SVGPath

class MainLayoutController : Initializable {

    @FXML
    private lateinit var mainAnchorPane: AnchorPane

    @FXML
    private lateinit var sidebarAnchorPane: AnchorPane

    @FXML
    private lateinit var sideBarGridPane: GridPane

    @FXML
    private lateinit var contentAnchorPane: AnchorPane

    @FXML
    private lateinit var mainSplitPane: SplitPane

    @FXML
    private lateinit var mainGridPane: GridPane

    @FXML
    private lateinit var sideBarImageView: ImageView

    @FXML
    private lateinit var sidebarHomeBtn: Button

    @FXML
    private lateinit var sidebarSuppliesBtn: Button

    @FXML
    private lateinit var sidebarFlockBtn: Button

    @FXML
    private lateinit var sideBarDateLabel: Label
    
    @FXML
    private lateinit var homeImageView: ImageView

    @FXML
    private lateinit var suppliesImageView: ImageView

    @FXML
    private lateinit var flockImageView: ImageView

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // Set today's date
        val today = LocalDate.now()
        sideBarDateLabel.text = today.toString()

        GeneralUtil.initializeFontSizeManager(mainAnchorPane)

        GeneralUtil.resizeImageViewToFit(mainAnchorPane, sideBarImageView)
        GeneralUtil.resizeImageViewToFit(mainAnchorPane,  homeImageView, 0.05, 0.07)
        GeneralUtil.resizeImageViewToFit(mainAnchorPane, suppliesImageView, 0.05, 0.07)
        GeneralUtil.resizeImageViewToFit(mainAnchorPane, flockImageView, 0.05, 0.07)

        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }
    


    @FXML
    private fun navigateToHome() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home.fxml")
    }
    
    @FXML
    private fun navigateToSupplies() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_supplies.fxml")
    }
    
    @FXML
    private fun navigateToFlock() {
        GeneralUtil.loadContentView(contentAnchorPane, "/fxml/content_home_flock.fxml")
    }
}