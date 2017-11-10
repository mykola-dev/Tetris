/*
 * © 2017 Deviant Studio
 */

package ds.tetris.fx.util

import javafx.scene.control.Alert

fun showAlert(message: String) {
    val alert = Alert(Alert.AlertType.INFORMATION)
    alert.title = "Info"
    alert.headerText = null
    alert.contentText = message
    alert.showAndWait()
}
fun showError(message: String) {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.title = "Error"
    alert.headerText = null
    alert.contentText = message
    alert.showAndWait()
}