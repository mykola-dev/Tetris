package ds.tetris.game

import ds.tetris.game.Direction.*
import ds.tetris.game.figures.*
import ds.tetris.game.job.KeyCoroutine
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancel
import kotlinx.coroutines.experimental.channels.ActorJob
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.selects.select
import kotlin.coroutines.experimental.CoroutineContext

private const val BASE_DELAY = 800L

private val figures = arrayOf(
    IFigure::class.java,
    IFigure::class.java,
    LFigure::class.java,
    LFlippedFigure::class.java,
    SFigure::class.java,
    SFlippedFigure::class.java,
    SquareFigure::class.java,
    TFigure::class.java
)

class Game(
    private val view: GameView,
    private val uiCoroutineContext: CoroutineContext
) {

    private val board: Board = Board(view)
    private val score: Score = Score {
        view.score = score
        view.level = level
    }
    private lateinit var nextFigure: Figure

    private var isStarted: Boolean = false
    var isPaused: Boolean = false

    private var stopper: Job = Job()

    private val uiContextProvider = {
        uiCoroutineContext + stopper
    }
    private var downKeyCoroutine: KeyCoroutine = KeyCoroutine(uiContextProvider, 30) {
        score.awardSpeedUp()
        gameActor.offer(Unit)
    }
    private var leftKeyCoroutine: KeyCoroutine = KeyCoroutine(uiContextProvider) {
        board.moveFigure(LEFT.movement)
    }
    private var rightKeyCoroutine: KeyCoroutine = KeyCoroutine(uiContextProvider) {
        board.moveFigure(RIGHT.movement)
    }

    private val gameActor: ActorJob<Unit> by lazy {
        actor<Unit>(uiContextProvider()) {
            log("actor started")
            while (isActive) {
                var falling = true
                nextFigure = randomFigure(false)
                drawPreview()
                board.drawFigure()
                while (falling) {
                    falling = select {
                        onReceive {
                            board.moveFigure(DOWN.movement)
                        }
                        onTimeout(calculateDelay()) {
                            board.moveFigure(DOWN.movement)
                        }
                    }
                }
                downKeyCoroutine.stop()
                if (gameOver()) {
                    isStarted = false
                    coroutineContext.cancel()
                } else {
                    board.fixFigure()

                    val lines = board.getFilledLinesIndices()
                    if (lines.isNotEmpty()) {
                        board.wipeLines(lines)
                        view.wipeLines(lines)
                        score.awardLinesWipe(lines.size)
                    }
                    nextFigure.position = board.startingPosition(nextFigure)
                    board.currentFigure = nextFigure
                }
            }
            view.gameOver()
            log("actor stopped")
        }
    }

    fun start() {
        if (isStarted) error("Can't start twice")
        isStarted = true

        view.clearArea()

        score.awardStart()

        board.currentFigure = IFigure()
        sceneFiller(board,view)
        gameActor.offer(Unit)
    }

    fun stop() {
        stopper.cancel()
    }

    fun pause() {
        gameActor.isActive || return
        isPaused = !isPaused
        if (!isPaused)
            gameActor.offer(Unit)
    }

    private fun drawPreview() {
        view.clearPreviewArea()
        nextFigure.points.forEach {
            view.drawPreviewBlockAt(it.x, it.y, nextFigure.color)
        }
        view.invalidate()
    }

    private fun calculateDelay(): Long = if (!isPaused) {
        (BASE_DELAY - score.level * 50).coerceAtLeast(1)
    } else {
        Long.MAX_VALUE / 2  // android workaround
    }

    private fun gameOver(): Boolean = board.currentFigure.position.y <= 0

    private fun randomFigure(bringToStart: Boolean = true): Figure {
        val cls = figures.random
        val figure = cls.newInstance()
        if (bringToStart)
            figure.position = board.startingPosition(figure)
        return figure
    }

    fun onLeftPressed() = leftKeyCoroutine.start()
    fun onRightPressed() = rightKeyCoroutine.start()
    fun onUpPressed() = board.rotateFigure()
    fun onDownPressed() = downKeyCoroutine.start()
    fun onDownReleased() = downKeyCoroutine.stop()
    fun onLeftReleased() = leftKeyCoroutine.stop()
    fun onRightReleased() = rightKeyCoroutine.stop()

}