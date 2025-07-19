package org.db_poultry.controller

import javafx.beans.property.SimpleObjectProperty
import org.db_poultry.db.DBConnect.getConnection
import org.db_poultry.db.supplyTypeDAO.CreateSupplyType.createSupplyType
import org.db_poultry.util.GeneralUtil
import org.db_poultry.util.undoSingleton
import org.db_poultry.util.undoTypes
import org.db_poultry.util.SupplyTypeSingleton

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import javafx.fxml.Initializable
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.text.Text
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.ResourceBundle
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import kotlin.compareTo
import kotlin.div

class CreateSuppliesController: Initializable {

    @FXML
    private lateinit var createSuppliesAnchorPane: AnchorPane

    @FXML
    private lateinit var confirmButton: Button

    @FXML
    private lateinit var createSuppliesTextField: TextField

    @FXML
    private lateinit var createSuppliesTextFieldUnit: TextField

    @FXML
    private lateinit var uploadImageGridPane: GridPane

    @FXML
    private lateinit var addSupplyImageText: Text

    @FXML
    private lateinit var resetImageButton: Button

    private var selectedImageFile: File? = null
    private val selectedImageFileProperty = SimpleObjectProperty<File?>(null)


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initializeResetButton()
        initializeConfirmButton()
    }

    @FXML
    fun confirm() {
        val supplyName = createSuppliesTextField.text.trim()
        val supplyUnit = createSuppliesTextFieldUnit.text.trim()

        println("\nSupply Name: $supplyName")
        println("Supply Unit: $supplyUnit")

        if (supplyName.isBlank() || supplyUnit.isBlank()) {
            GeneralUtil.showPopup("error", "Supply name and unit cannot be empty.")
            println("Supply name and unit cannot be empty.")
            return
        }

        // Prepare path and cropped image, but don't save
        var imagePath: String? = null
        var croppedImage: BufferedImage? = null
        val resourcesDir = File("src/main/resources/img/supply-img")

        selectedImageFile?.let { file ->
            if (!resourcesDir.exists()) resourcesDir.mkdirs()
            // Prepare the image but don't write it yet
            croppedImage = prepareImage(file)
        }

        val tempFile = File(resourcesDir, "${supplyName.lowercase()}.jpg")
        if (!tempFile.exists()) tempFile.createNewFile()
        imagePath = tempFile.absolutePath

        if (createSupplyType(getConnection(), supplyName, supplyUnit,
                imagePath, SupplyTypeSingleton.getUIDefaultImagePath()) != null) {

            undoSingleton.setUndoMode(undoTypes.doUndoSupplyType)
            GeneralUtil.showPopup("success", "Supply type created successfully.")

            // Delete temp image
            imagePath?.let { path ->
                File(path).delete()
                println("Cleaned up temp image file")
            }

            // Save real file
            if (croppedImage != null && imagePath != null) {
                saveProcessedImage(croppedImage, File(imagePath))
                println("Image saved to: $imagePath")
            }

            println("Successfully created Supply type.")
        } else {
            imagePath?.let { path ->
                File(path).delete()
                println("Cleaned up image file due to failed supply type creation")
            }

            GeneralUtil.showPopup("error", "Failed to create Supply type.")
            println("Failed to create Supply type.")
        }

        resetFields()
    }

    private fun initializeConfirmButton() {
        val updateConfirmButtonState = {
            val supplyNameEmpty = createSuppliesTextField.text.trim().isEmpty()
            val supplyUnitEmpty = createSuppliesTextFieldUnit.text.trim().isEmpty()
            confirmButton.isDisable = supplyNameEmpty || supplyUnitEmpty
        }

        // Add listeners to both text fields
        createSuppliesTextField.textProperty().addListener { _, _, _ ->
            updateConfirmButtonState()
        }
        createSuppliesTextFieldUnit.textProperty().addListener { _, _, _ ->
            updateConfirmButtonState()
        }

        // Set initial state
        updateConfirmButtonState()
    }

    private fun initializeResetButton() {
        // Listener to enable/disable button
        selectedImageFileProperty.addListener { _, _, newValue ->
            resetImageButton.isDisable = newValue == null
        }
        resetImageButton.isDisable = true
    }

    fun resetFields() {
        createSuppliesTextField.clear()
        createSuppliesTextFieldUnit.clear()
        uploadImageGridPane.style = "-fx-background-image: none;"
    }

    @FXML
    fun uploadSupplyImage() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select Image"
        fileChooser.extensionFilters.addAll(
            FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        )
        val file = fileChooser.showOpenDialog(uploadImageGridPane.scene.window)
        if (file != null) {
            selectedImageFile = file
            selectedImageFileProperty.set(file)
            val imageUrl = file.toURI().toString()
            uploadImageGridPane.style = """
            -fx-background-image: url("$imageUrl");
            -fx-background-size: 117%;
            -fx-background-position: center center;
            -fx-border-color: black;
            -fx-border-width: 2;
        """.trimIndent()
        }

        addSupplyImageText.text = ""
    }

    @FXML
    fun resetImageFile() {
        uploadImageGridPane.style = """
            -fx-background-image: none;
            -fx-border-color: black;
            -fx-border-width: 2;
        """.trimIndent()


        selectedImageFile = null
        addSupplyImageText.text = "Add Supply Image"
        selectedImageFileProperty.set(null)
    }

    private fun prepareImage(inputFile: File): BufferedImage {
        val original: BufferedImage = ImageIO.read(inputFile)
        val size = minOf(original.width, original.height)
        val x = (original.width - size) / 2
        val y = (original.height - size) / 2
        return original.getSubimage(x, y, size, size)
    }

    private fun saveProcessedImage(image: BufferedImage, outputFile: File, maxSizeBytes: Long = 2 * 1024 * 1024) {
        // Convert to a color model compatible with JPEG
        val rgbImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        rgbImage.createGraphics().apply {
            drawImage(image, 0, 0, null)
            dispose()
        }

        var quality = 1.0f

        try {
            do {
                // Create new writer for each attempt
                val writer = ImageIO.getImageWritersByFormatName("jpg").next()
                val param = writer.defaultWriteParam
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = quality

                outputFile.outputStream().use { fos ->
                    ImageIO.createImageOutputStream(fos).use { ios ->
                        writer.output = ios
                        writer.write(null, IIOImage(rgbImage, null, null), param)
                        ios.flush()
                    }
                }

                writer.dispose()

                if (outputFile.length() <= maxSizeBytes || quality < 0.1f) break
                quality -= 0.1f
                println("Reducing quality to: $quality")
            } while (true)

            println("Final image size: ${outputFile.length()} bytes")
        } catch (e: Exception) {
            println("Error saving image: ${e.message}")
            e.printStackTrace()
        }
    }

}