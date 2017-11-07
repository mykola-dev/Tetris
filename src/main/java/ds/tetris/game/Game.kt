package ds.tetris.game

import ds.tetris.game.figures.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

private const val BASE_DELAY = 800L

class Game {

    private val figures = arrayOf(
        IFigure::class.java,
        IFigure::class.java,    // higher chance
        LFigure::class.java,
        LFlippedFigure::class.java,
        SFigure::class.java,
        SFlippedFigure::class.java,
        SquareFigure::class.java,
        TFigure::class.java
    )

    private lateinit var view: GameView
    private lateinit var board: Board
    private lateinit var score: Score

    private var isStarted: Boolean = false

    private var stopper: Job = Job()

    fun start(view: GameView) {
        //!isStarted || return
        stopper.cancel()
        stopper = Job()

        isStarted = true
        this.view = view
        view.clearArea()

        score = Score {
            view.score = score.score
            view.level = score.level
            if (score.shouldLevelUp)
                score.awardLevelUp()
        }
        score.awardStart()

        board = Board(view, IFigure())
        debug()
        startFall()
    }

    private fun debug() {
        with(board) {
            for (i in 0 until AREA_WIDTH - 1) {
                area[19, i] = true
                area[18, i] = true
                area[17, i] = true
                area[16, i] = true
                area[15, i] = true
                area[14, i] = true
                area[13, i] = true
                area[12, i] = true
                area[11, i] = true
                area[10, i] = true
            }
            area[17, 5] = false
            for (r in 0 until area.array.size) {
                val row = area.array[r]
                for (c in 0 until row.size) {
                    val item = row[c]
                    if (item)
                        view.drawBlockAt(c, r, 0xffffff)
                }
            }
        }
    }

    private fun startFall() = launch(JavaFx + stopper) {
        while (isActive) {
            var falling = true
            board.drawFigure()
            while (falling) {
                delay(calculateDelay())
                falling = board.moveFigure(Direction.DOWN)
            }
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

                board.currentFigure = randomFigure()
            }
        }
        view.gameOver()
    }

    private fun calculateDelay() = (BASE_DELAY - score.level * 30).coerceAtLeast(1)

    private fun gameOver(): Boolean = board.currentFigure.position.y <= 0

    private fun randomFigure(): Figure {
        val cls = figures.random
        val figure = cls.newInstance()
        figure.position = Point((AREA_WIDTH - figure.matrix.width) / 2, 0)
        println(figure)
        return figure
    }


    fun onLeftPressed() {
        if (isStarted) board.moveFigure(Direction.LEFT)
    }

    fun onRightPressed() {
        if (isStarted) board.moveFigure(Direction.RIGHT)
    }

    fun onUpPressed() {
        if (isStarted) board.rotateFigure()
    }

    fun onDownPressed() {
        if (isStarted) {
            if (board.moveFigure(Direction.DOWN))
                score.awardSpeedUp()
        }
    }
}