package ds.tetris

import ds.tetris.fx.view.MainView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import tornadofx.*

class TetrisApp : App(MainView::class) {
    init {
        //reloadViewsOnFocus()
        FX.layoutDebuggerShortcut = KeyCodeCombination(KeyCode.F12, KeyCombination.CONTROL_DOWN)
    }
}

fun main(args: Array<String>) = launch<TetrisApp>(args)
