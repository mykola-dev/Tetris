/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ds.tetris.game.Direction.*
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.FigureFactory
import ds.tetris.game.job.KeysProducer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.selects.select
import kotlin.random.Random

private const val BASE_DELAY = 800L

class Game(
    scope: CoroutineScope,
    private val soundtrack: Soundtrack,
    private val log: Napier,
) : CoroutineScope by scope {

    private val board: Board = Board()
    private val state = MutableStateFlow(
        GameState(areaDimensions = IntSize(board.width, board.height))
    )
    val gameState: StateFlow<GameState> = state.asStateFlow()

    private lateinit var currentFigure: Figure
    private lateinit var nextFigure: Figure

    private val score: Score = Score {
        val currLevel = state.value.level
        if (currLevel < level && currLevel != 0 && state.value.soundEnabled) {
            soundtrack.levelUp()
        }

        state.update {
            it.copy(
                score = points,
                level = level
            )
        }
    }

    private val isRunning: Boolean get() = state.value.state == GameState.State.STARTED

    private val keysProducer = KeysProducer(coroutineContext)
    private val downKey = Channel<Unit>()

    private var gameLoop: Job? = null

    init {
        log.v("init game $this")
        launch {
            keysProducer.outputChannel.consumeEach {
                if (isRunning) {
                    if (it == DOWN) downKey.trySend(Unit)
                    else currentFigure.tryMove(it)
                }
            }
        }

        soundtrack.init()
        start()
    }

    private fun provideGameLoop() = launch {
        nextFigure = randomFigure()
        while (isActive) {
            var falling = true

            currentFigure = nextFigure
            currentFigure.moveToStart()
            nextFigure = randomFigure()

            draw()

            while (falling) {
                falling = select {
                    downKey.onReceive {
                        score.awardSpeedUp()
                        log.v("triggered down")
                        currentFigure.tryMove(DOWN)
                    }
                    onTimeout(calculateDelay()) {
                        currentFigure.tryMove(DOWN)
                    }
                }
            }

            keysProducer(null)  // break fast falling. doesn't work with physical keys due to a handler bug

            if (isGameOver()) {
                state.update { it.copy(state = GameState.State.GAME_OVER) }
                soundtrack.gameOver()
                gameLoop?.cancel()
            } else {
                board.bakeFigure(currentFigure)

                val lines = board.getFilledRowsIndices()
                if (lines.isNotEmpty()) {
                    soundtrack.wipe(lines.size)
                    score.awardLinesWipe(lines.size)
                    state.update { it.copy(wipedLines = lines) }
                }
            }
        }

    }

    fun togglePause() {
        soundtrack.pause()
        when (state.value.state) {
            GameState.State.PAUSED -> {
                state.update { it.copy(state = GameState.State.STARTED) }
            }
            GameState.State.STARTED -> {
                state.update { it.copy(state = GameState.State.PAUSED) }
            }
            else -> {}
        }
    }

    fun onReset() {
        start()
    }

    fun toggleSound() {
        val soundEnabled = !state.value.soundEnabled
        soundtrack.enabled = soundEnabled
        state.update { it.copy(soundEnabled = soundEnabled) }
    }

    private fun start() {
        log.i("starting...")
        score.reset()
        gameLoop?.cancel()
        board.clear()
        gameLoop = provideGameLoop()
        state.update { it.copy(state = GameState.State.STARTED) }

        //sceneFiller(board)   // todo
    }

    private fun calculateDelay(): Long = (BASE_DELAY - score.level * 50).coerceAtLeast(1)

    private fun isGameOver(): Boolean = currentFigure.offset.y <= 0

    private fun randomFigure(): Figure = FigureFactory.create()

    fun onLeftPressed() {
        keysProducer(LEFT)
        soundtrack.move()
    }

    fun onRightPressed() {
        keysProducer(RIGHT)
        soundtrack.move()
    }

    fun onDownPressed() {
        keysProducer(DOWN)
        soundtrack.move()
    }

    fun onUpPressed() {
        val newFigure = currentFigure.rotateFigure()

        val x = currentFigure.matrix.height / 2f + newFigure.offset.x
        val y = currentFigure.matrix.height / 2f + currentFigure.offset.y

        val offset = Offset(x, y)

        state.update { it.copy(rotationPivot = offset) }
        soundtrack.rotate()
    }

    fun onKeyReleased() {
        keysProducer(null)
    }

    fun onWipingDone() {
        board.wipeLines(state.value.wipedLines)
        currentFigure.calculateDistance()
        state.update {
            it.copy(
                wipedLines = emptySet(),
                figure = currentFigure.allBricks,
                bricks = board.getBricks()
            )
        }
    }

    fun onRotationDone() {
        currentFigure = currentFigure.rotateFigure().apply { calculateDistance() }
        state.update {
            it.copy(
                rotationPivot = null,
                figure = currentFigure.allBricks
            )
        }
    }

    /**
     * @return if still falling
     */
    private fun Figure.tryMove(direction: Direction): Boolean {
        if (!isRunning) return true
        if (state.value.wipedLines.isNotEmpty()) return true
        if (state.value.rotationPivot != null) return true
        if (!canMove(direction.movement)) return false

        this.offset += direction.movement

        currentFigure.calculateDistance()
        state.update {
            it.copy(figure = currentFigure.allBricks)
        }

        return true
    }

    private fun Figure.calculateDistance() {
        distance = (0 until board.height - offset.y)
            .firstOrNull {
                val movement = IntOffset(0, it + 1)
                !this.canMove(movement)
            }
            ?: 0

    }

    private fun Figure.canMove(movement: IntOffset): Boolean = this
        .getPoints()
        .all {
            val nextPoint = it + movement
            !nextPoint.outOfArea() && board.matrix[nextPoint] == null
        }

    private fun IntOffset.outOfArea(): Boolean = x >= board.width || x < 0 || y >= board.height || y < 0

    private fun Figure.moveToStart() {
        this.offset = IntOffset((board.width - this.matrix.width) / 2, 0)
    }

    private fun Figure.rotateFigure(): Figure {
        val figure = currentFigure.rotate()
        with(figure) {
            // edge cases
            while (getPoints().any { it.x < 0 }) {
                offset += RIGHT.movement
            }
            while (getPoints().any { it.x >= board.width }) {
                offset += LEFT.movement
            }
            while (getPoints().any { it.y >= board.height }) {
                offset += UP.movement
            }
            while (getPoints().any { it.y < 0 }) {
                offset += DOWN.movement
            }

            // try to fix bricks collisions
            if (board.collides(this)) {
                if (canMove(RIGHT.movement))
                    offset += RIGHT.movement
                else if (canMove(LEFT.movement))
                    offset += LEFT.movement
            }
        }
        return figure
    }

    private fun draw() {
        currentFigure.calculateDistance()
        state.update {
            it.copy(
                bricks = board.getBricks().filter { it.offset.y !in state.value.wipedLines },
                figure = currentFigure.allBricks,
                next = nextFigure.allBricks
            )
        }
    }

    fun toggleAnimation() {
        state.update { it.copy(animationEnabled = !it.animationEnabled) }
    }
}