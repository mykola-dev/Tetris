package ds.tetris.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ds.tetris.game.Game
import ds.tetris.game.GameState

@Composable
fun TetrisGame(game: Game) {
    val state by game.gameState.collectAsState()

    TetrisScreen(
        state = state,
        onReset = game::onReset,
        onResume = game::togglePause,
        onLeftPress = game::onLeftPressed,
        onLeftRelease = game::onLeftReleased,
        onRightPress = game::onRightPressed,
        onRightRelease = game::onRightReleased,
        onUpPress = game::onUpPressed,
        onDownPress = game::onDownPressed,
        onDownRelease = game::onDownReleased,
        onToggleSound = game::toggleSound,
        onWipingDone = game::onWipingDone
    )


}

@Composable
fun TetrisScreen(
    state: GameState,
    onReset: () -> Unit,
    onResume: () -> Unit,
    onLeftPress: () -> Unit,
    onLeftRelease: () -> Unit,
    onRightPress: () -> Unit,
    onRightRelease: () -> Unit,
    onUpPress: () -> Unit,
    onDownPress: () -> Unit,
    onDownRelease: () -> Unit,
    onToggleSound: () -> Unit,
    onWipingDone: () -> Unit,
) {
    Surface {
        Column {
            Row {
                Board(
                    state.bricks,
                    state.wipedLines,
                    state.areaDimensions,
                    state.state == GameState.State.GAME_OVER,
                    onWipingDone,
                    Modifier.weight(1f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.requiredWidth(120.dp)) {
                    NextFigure(state.next)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { onReset() },
                    ) {
                        Text("Restart")
                    }
                    Button(
                        onClick = { onResume() },
                        enabled = state.state != GameState.State.GAME_OVER
                    ) {
                        val text = if (state.state == GameState.State.STARTED) "Pause" else "Resume"
                        Text(text)
                    }

                    Button(onToggleSound) {
                        Text(if (state.soundEnabled) "Sound" else "S̶o̶u̶n̶d̶")
                    }

                    Spacer(Modifier.height(32.dp))

                    Text("Level: ${state.level}", color = Palette.level)

                    Text("Score: ${state.score}", color = Palette.score)
                }
            }

            Row(
                Modifier
                    .height(160.dp)
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton("←", onLeftPress, onLeftRelease, Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    ControlButton("↑", onUpPress, {}, Modifier.weight(1f))
                    ControlButton("↓", onDownPress, onDownRelease, Modifier.weight(1f))
                }

                ControlButton("→", onRightPress, onRightRelease, Modifier.weight(1f))
            }
        }
    }

}

@Composable
fun ControlButton(
    text: String,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iSource = remember { MutableInteractionSource() }
    val isPressed by iSource.collectIsPressedAsState()
    LaunchedEffect(isPressed) {
        if (isPressed) {
            onPress()
        } else {
            onRelease()
        }
    }
    Button({}, interactionSource = iSource, modifier = modifier.fillMaxSize()) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}


