import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import ds.tetris.di.initKoin
import ds.tetris.game.Game
import ds.tetris.ui.TetrisGame
import ds.tetris.ui.TetrisTheme
import org.koin.core.context.GlobalContext.get

fun main() {
    initKoin()
    val game: Game = get().get()
    singleWindowApplication(
        title = "Tetris MP",
        state = WindowState(size = DpSize(450.dp, 800.dp))
    ) {
        TetrisTheme {
            TetrisGame(game)
        }
    }
}


