package org.db_poultry.controller

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.PasswordField
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import org.kordamp.ikonli.javafx.FontIcon
import java.net.URL
import java.util.ResourceBundle

class BackupController : Initializable {

    @FXML
    private lateinit var backupAnchorPane: AnchorPane

    @FXML
    private lateinit var backupDatesComboBox: ComboBox<String>

    @FXML
    private lateinit var closeButton: FontIcon

    @FXML
    private lateinit var passPasswordField: PasswordField

    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    @FXML
    fun closePopup() {
        val stage = backupAnchorPane.scene.window as Stage
        stage.close()
    }



    @FXML
    fun confirm() {

    }
}