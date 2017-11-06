package ds.tetris

import ds.tetris.presentation.view.MainViewImpl
import ds.tetris.util.log
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import tornadofx.*

class TetrisApp : App(MainViewImpl::class) {
    init {
        //reloadViewsOnFocus()
        FX.layoutDebuggerShortcut = KeyCodeCombination(KeyCode.F12, KeyCombination.CONTROL_DOWN)
    }

    override fun start(stage: Stage) {
        super.start(stage)
        log("APP START")
    }
}

fun main(args: Array<String>) = launch<TetrisApp>(args)
