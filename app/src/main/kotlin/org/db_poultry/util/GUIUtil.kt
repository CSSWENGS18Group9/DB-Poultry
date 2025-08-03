package org.db_poultry.util

import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField

object GUIUtil {

    fun setupPassword(passTextField: TextField, passPasswordField: PasswordField,
                      showPassButton: Button
    ): () -> Unit {
        var isPasswordShown = false
        passTextField.isVisible = false

        val toggleAction = {
            if (!isPasswordShown) {
                passTextField.text = passPasswordField.text
                passPasswordField.isVisible = false
                passTextField.isVisible = true
            } else {
                passPasswordField.text = passTextField.text
                passTextField.isVisible = false
                passPasswordField.isVisible = true
            }
            isPasswordShown = !isPasswordShown
        }

        showPassButton.setOnAction { toggleAction() }
        return toggleAction
    }
}