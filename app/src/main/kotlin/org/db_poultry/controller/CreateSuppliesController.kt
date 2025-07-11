package org.db_poultry.controller

import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType.createSupplyType
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import javafx.fxml.Initializable
import javafx.scene.layout.Pane
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.ResourceBundle
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

class CreateSuppliesController: Initializable {

    @FXML
    private lateinit var createSuppliesAnchorPane: AnchorPane

    @FXML
    private lateinit var btnConfirm: Button

    @FXML
    private lateinit var createSuppliesTextField: TextField

    @FXML
    private lateinit var createSuppliesTextFieldUnit: TextField

    @FXML
    private lateinit var uploadImagePane: Pane

    private var selectedImageFile: File? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
//        GeneralUtil.resizeImageViewToFit(createSuppliesAnchorPane, uploadFileImageView)
    }

    @FXML
    fun confirm() {
        val supplyName = createSuppliesTextField.text
        val supplyUnit = createSuppliesTextFieldUnit.text

        println("\nSupply Name: $supplyName")
        println("Supply Unit: $supplyUnit")

        if (supplyName.isBlank() || supplyUnit.isBlank()) {
            GeneralUtil.showPopup("error", "Supply name and unit cannot be empty.")
            println("Supply name and unit cannot be empty.")
            return
        }


        if (createSupplyType(getConnection(), supplyName, supplyUnit) != null) {
            undoSingleton.setUndoMode(undoTypes.doUndoSupplyType)
            GeneralUtil.showPopup("success", "Supply type created successfully.")
            println("Successfully created Supply type.")

            selectedImageFile?.let { file ->
                val resourcesDir = File("src/main/resources/img/supply-img")
                if (!resourcesDir.exists()) resourcesDir.mkdirs()
                val targetFile = File(resourcesDir, "${supplyName.lowercase()}.jpg")

                // Crop and compress all images to JPEG
                cropCenterSquareAndCompress(file, targetFile)
                println("Image copied to: ${targetFile.absolutePath}")
            }
        } else {
            GeneralUtil.showPopup("error", "Failed to create Supply type.")
            println("Failed to create Supply type.")
        }

        resetFields()
    }

    fun resetFields() {
        createSuppliesTextField.clear()
        createSuppliesTextFieldUnit.clear()
        uploadImagePane.style = "-fx-background-image: none;"
    }

    @FXML
    fun uploadSupplyImage() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select Image"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        )
        val file = fileChooser.showOpenDialog(uploadImagePane.scene.window)
        if (file != null) {
            selectedImageFile = file
            val imageUrl = file.toURI().toString()
            uploadImagePane.style = """
            -fx-background-image: url("$imageUrl");
            -fx-background-size: 117%;
            -fx-background-position: center center;
            -fx-border-color: black;
            -fx-border-width: 2;
        """.trimIndent()
        }
    }

    fun cropCenterSquareAndCompress(inputFile: File, outputFile: File, maxSizeBytes: Long = 2 * 1024 * 1024) {
        val original: BufferedImage = ImageIO.read(inputFile)
        val size = minOf(original.width, original.height)
        val x = (original.width - size) / 2
        val y = (original.height - size) / 2
        val cropped = original.getSubimage(x, y, size, size)

        val writers = ImageIO.getImageWritersByFormatName("jpg")
        val writer: ImageWriter = writers.next()
        var quality = 1.0f

        do {
            val param = writer.defaultWriteParam
            param.compressionMode = ImageWriteParam.MODE_EXPLICIT
            param.compressionQuality = quality

            FileOutputStream(outputFile).use { fos ->
                writer.output = ImageIO.createImageOutputStream(fos)
                writer.write(null, IIOImage(cropped, null, null), param)
            }

            if (outputFile.length() <= maxSizeBytes || quality < 0.1f) break
            quality -= 0.1f
        } while (true)

        writer.dispose()
    }

}