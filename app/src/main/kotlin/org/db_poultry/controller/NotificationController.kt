package org.db_poultry.controller

import org.controlsfx.control.Notifications
import org.db_poultry.util.GeneralUtil

import javafx.geometry.Pos
import javafx.util.Duration

object NotificationController {

    /**
     * Displays a notification with the given title and message.
     *
     * @param title The title of the notification.
     * @param message The message of the notification.
     */
    fun showNotification(title: String, message: String) {
        Notifications.create()
            .title(title)
            .text(message)
            .graphic(null)
            .hideAfter(Duration.seconds(5.0))
            .owner(GeneralUtil.getMainContentPane())
            .position(Pos.BOTTOM_RIGHT)
            .showConfirm()
    }
}