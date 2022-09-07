package ds.tetris.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
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
        onRightPress = game::onRightPressed,
        onUpPress = game::onUpPressed,
        onDownPress = game::onDownPressed,
        onKeyRelease = game::onKeyReleased,
        onToggleSound = game::toggleSound,
        onWipingDone = game::onWipingDone,
        onRotationDone = game::onRotationDone,
        onToggleAnimation = game::toggleAnimation
    )


}

@Composable
fun TetrisScreen(
    state: GameState,
    onReset: () -> Unit,
    onResume: () -> Unit,
    onLeftPress: () -> Unit,
    onRightPress: () -> Unit,
    onUpPress: () -> Unit,
    onDownPress: () -> Unit,
    onKeyRelease: () -> Unit,
    onToggleSound: () -> Unit,
    onWipingDone: () -> Unit,
    onRotationDone: () -> Unit,
    onToggleAnimation: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Surface(
        Modifier
            .focusRequester(focusRequester)
            .onPreviewKeyEvent {
                // doesn't work in browser
                if (it.key in listOf(Key.DirectionLeft, Key.DirectionRight, Key.DirectionUp, Key.DirectionDown)) {
                    //log.v("${it.key} ${it.type} ${it.utf16CodePoint} ${it.nativeKeyEvent}")
                    when (it.type) {
                        KeyEventType.KeyDown -> {
                            when (it.key) {
                                Key.DirectionLeft -> onLeftPress()
                                Key.DirectionRight -> onRightPress()
                                Key.DirectionUp -> onUpPress()
                                Key.DirectionDown -> onDownPress()
                            }
                        }
                        KeyEventType.KeyUp -> {
                            onKeyRelease()
                        }
                    }
                    true
                } else false
            }
    ) {
        LaunchedEffect(Unit) {
            // required for keyboard input
            focusRequester.requestFocus()
        }

        Column {
            Row {
                Board(
                    state.bricks,
                    state.figure,
                    state.wipedLines,
                    state.rotationPivot,
                    state.areaDimensions,
                    state.state == GameState.State.GAME_OVER,
                    onWipingDone,
                    onRotationDone,
                    state.animationEnabled,
                    Modifier.weight(1f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.requiredWidth(120.dp)) {
                    NextFigure(state.next)

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
                        Text(if (state.soundEnabled) "Sound" else "S̶o̶u̶n̶d̶") // todo icon
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Level: ${state.level}", color = Palette.level)
                    Spacer(Modifier.height(8.dp))
                    Text("Score: ${state.score}", color = Palette.score)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Smooth")
                        Switch(state.animationEnabled, { onToggleAnimation() })
                    }
                }
            }

            Row(
                Modifier
                    .height(160.dp)
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton(Icons.Default.KeyboardArrowLeft, onLeftPress, onKeyRelease, Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    ControlButton(Icons.Default.KeyboardArrowUp, onUpPress, {}, Modifier.weight(1f))
                    ControlButton(Icons.Default.KeyboardArrowDown, onDownPress, onKeyRelease, Modifier.weight(1f))
                }

                ControlButton(Icons.Default.KeyboardArrowRight, onRightPress, onKeyRelease, Modifier.weight(1f))
            }
        }
    }

}

@Composable
fun ControlButton(
    icon: ImageVector,
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
        Icon(icon, null)
    }
}


