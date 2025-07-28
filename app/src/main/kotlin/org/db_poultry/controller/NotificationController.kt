package org.db_poultry.controller

import org.controlsfx.control.Notifications
import org.db_poultry.util.GeneralUtil

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.util.Duration

object NotificationController {

    private var status: String = "info" // Default status
    private var title: String = "Notification"
    private var message: String = "This is a notification message."

    fun setNotification(status: String = "info", title: String, message: String) {
        this.status = status
        this.title = title
        this.message = message
    }

    /**
     * Displays a notification with the given title and message.
     *
     * @param title The title of the notification.
     * @param message The message of the notification.
     */
    fun showNotification() {
        val imageView: ImageView = when (status) {
            "success" -> ImageView(Image(javaClass.getResourceAsStream("/img/notification/success.png")))
            "error" -> ImageView(Image(javaClass.getResourceAsStream("/img/notification/error.png")))
            "warning" -> ImageView(Image(javaClass.getResourceAsStream("/img/notification/warning.png")))

            else -> {
                ImageView(Image(javaClass.getResourceAsStream("/img/notification/info.png")))
            }
        }

        imageView.fitWidth = 60.0
        imageView.fitHeight = 60.0
        imageView.isPreserveRatio = true

        val transparentWrap = StackPane(imageView)
        transparentWrap.style = "-fx-background-color: transparent; -fx-padding: 10;"

        Notifications.create()
            .title(title)
            .text(message)
            .graphic(transparentWrap)
            .hideAfter(Duration.seconds(7.0))
            .owner(GeneralUtil.getMainContentPane())
            .position(Pos.BOTTOM_RIGHT)
            .styleClass("h5")
            .show()
    }
}