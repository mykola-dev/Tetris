package ds.tetris.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ds.tetris.game.Game
import ds.tetris.game.GameState
import ds.tetris.game.figures.Brick

@Composable
fun TetrisGame(game: Game) {
    val state by game.gameState.collectAsState()

    TetrisScreen(
        state,
        game::onReset,
        game::onResume,
        game::onLeftPressed,
        game::onLeftReleased
    )


}

@Composable
fun TetrisScreen(
    state: GameState,
    onReset: () -> Unit,
    onResume: () -> Unit,
    onLeftPress: () -> Unit,
    onLeftRelease: () -> Unit,
) {
    Surface {
        Column() {
            Row(Modifier.weight(1f)) {
                Board(state)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    NextFigure(state.next)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { onReset() },
                    ) {
                        Text("Restart")
                    }
                    Button(
                        onClick = { onResume() },
                    ) {
                        Text("Resume")
                    }

                    Text("Level: ${state.level}")

                    Text("Score: ${state.score}")
                }
            }

            val isource = remember { MutableInteractionSource() }
            val isLeftPressed = isource.collectIsPressedAsState()

            Row(Modifier.height(100.dp).padding(8.dp)) {
                Button(
                    onClick = { },
                    interactionSource = isource,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                    /* .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            onLeftPress()
                            awaitRelease()
                            onLeftRelease()
                        })
                    }*/
                ) {
                    Text("←")
                }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Button({}, modifier = Modifier.weight(1f).fillMaxWidth()) {
                        Text("↑")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button({}, modifier = Modifier.weight(1f).fillMaxWidth()) {
                        Text("↓")
                    }
                }
                Spacer(Modifier.width(8.dp))
                Button({}, modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Text("→")
                }
            }
        }
    }
}

@Composable
fun NextFigure(next: List<Brick>) {
    Canvas(Modifier.aspectRatio(3 / 4f).fillMaxWidth()) {
        drawRect(Color.Black)
    }
}

@Composable
fun Board(state: GameState) {
    Canvas(
        Modifier
            .fillMaxHeight()
            .aspectRatio(state.areaDimensions.run { width / height.toFloat() })
    ) {
        drawRect(Color.Blue)

    }
}
