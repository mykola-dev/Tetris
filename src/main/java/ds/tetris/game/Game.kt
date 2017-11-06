package ds.tetris.game

import ds.tetris.game.figures.*
import kotlinx.coroutines.experimental.cancel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch

class Game {

    private val figures = arrayOf(
        IFigure::class.java,
        LFigure::class.java,
        LFlippedFigure::class.java,
        SFigure::class.java,
        SFlippedFigure::class.java,
        SquareFigure::class.java,
        TFigure::class.java
    )

    private lateinit var view: MainView
    private lateinit var board: Board
    private lateinit var score: Score

    private var isStarted: Boolean = false
    private val BASE_DELAY = 500L

    fun start(view: MainView) {
        !isStarted || return
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

        board = Board(view, randomFigure())
        startFall()
    }

    private fun startFall() = launch(JavaFx) {
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