/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import ds.tetris.game.Direction.*
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.FigureFactory
import ds.tetris.game.figures.figures
import ds.tetris.game.job.KeysJob
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
    private val soundtrack: Soundtrack,
    private val log: Napier,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {

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
            soundtrack.play(Sound.LEVEL_UP)
        }

        state.update {
            it.copy(
                score = points,
                level = level
            )
        }
    }

    private val isRunning: Boolean get() = state.value.state == GameState.State.STARTED

    private val keysJob = KeysJob(coroutineContext)
    private val downKey = Channel<Unit>()

    init {
        launch {
            keysJob.channel.consumeEach {
                if (isRunning) {
                    if (it == DOWN) downKey.trySend(Unit)
                    else currentFigure.tryMove(it)
                }
            }
        }

        start()
    }

    private var gameLoop: Job? = null

    private fun provideGameLoop() = launch {
        nextFigure = randomFigure()
        while (isActive) {
            var falling = true

            currentFigure = nextFigure
            currentFigure.moveToStart()
            nextFigure = randomFigure()

            draw()

            while (falling) {
                log.v("falling:")
                falling = select {
                    downKey.onReceive {
                        score.awardSpeedUp()
                        currentFigure.tryMove(DOWN)
                    }
                    onTimeout(calculateDelay()) {
                        log.v("tick")
                        currentFigure.tryMove(DOWN)
                    }
                }
            }

            keysJob.pressedKey = null

            if (isGameOver()) {
                state.update { it.copy(state = GameState.State.GAME_OVER) }
                soundtrack.play(Sound.GAME_OVER)
                gameLoop?.cancel()
            } else {
                board.bakeFigure(currentFigure)

                val lines = board.getFilledRowsIndices()
                if (lines.isNotEmpty()) {
                    log.v("wipe ${lines.size} lines")
                    soundtrack.play(Sound.WIPE, lines.size)
                    //board.wipeLines(lines)
                    score.awardLinesWipe(lines.size)
                    state.update { it.copy(wipedLines = lines) }
                }
            }
        }

    }

    fun togglePause() {
        soundtrack.play(Sound.ROTATE)
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
        keysJob.pressedKey = LEFT
        playMoveSound()
    }

    fun onRightPressed() {
        keysJob.pressedKey = RIGHT
        playMoveSound()
    }

    fun onDownPressed() {
        keysJob.pressedKey = DOWN
        playMoveSound()
    }

    fun onUpPressed() {
        currentFigure.rotateFigure()
        soundtrack.play(Sound.ROTATE)
    }

    fun onLeftReleased() {
        keysJob.pressedKey = null
    }

    fun onRightReleased() {
        keysJob.pressedKey = null
    }

    fun onDownReleased() {
        keysJob.pressedKey = null
    }

    fun onWipingDone() {
        board.wipeLines(state.value.wipedLines)
        currentFigure.calculateDistance()
        state.update { it.copy(wipedLines = emptySet(), bricks =  board.getBricks() + currentFigure.allBricks) }
    }

    private fun playMoveSound() {
        soundtrack.play(Sound.MOVE, (Random.nextInt(4) + 1))
    }

    /**
     * @return if falling
     */
    private fun Figure.tryMove(direction: Direction): Boolean {
        if (!isRunning) return true
        if (state.value.wipedLines.isNotEmpty()) return true    // todo remove?
        if (!canMove(direction.movement)) return false

        this.offset += direction.movement

        draw()

        return true
    }

    private fun Figure.calculateDistance() {
        for (i in 0 until board.height - offset.y) {
            val movement = IntOffset(0, i)
            if (!this.canMove(movement)) {
                this.distance = i - 1
                break
            }
        }
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

    private fun Figure.rotateFigure() {
        rotate()

        // edge cases
        while (!getPoints().all { it.x >= 0 }) {
            offset += RIGHT.movement
        }
        while (!getPoints().all { it.x < board.width }) {
            offset += LEFT.movement
        }
        while (!getPoints().all { it.y < board.height }) {
            offset += UP.movement
        }

        // try to fix unexpected collisions todo refactor
        if (board.collides(this)) {
            if (canMove(RIGHT.movement))
                offset += RIGHT.movement
            else if (canMove(LEFT.movement))
                offset += LEFT.movement
        }

        draw()

    }

    private fun draw() {
        //log.v("draw")
        currentFigure.calculateDistance()
        state.update { it.copy(bricks =  board.getBricks().filter { it.offset.y !in state.value.wipedLines } + currentFigure.allBricks, next = nextFigure.allBricks) }
    }
}