import androidx.compose.ui.window.Window
import ds.tetris.di.initKoin
import ds.tetris.game.Game
import ds.tetris.ui.TetrisGame
import ds.tetris.ui.TetrisTheme
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.GlobalContext.get


fun main() {
    initKoin()

    val game: Game = get().get()

    onWasmReady {
        Window("Tetris") {
            TetrisTheme {
                TetrisGame(game)
            }
        }
    }
}
