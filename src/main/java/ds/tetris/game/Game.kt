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

    private var isStarted: Boolean = false
    private var stepDelay = 500L

    fun start(view: MainView) {
        this.view = view
        board = Board(view, randomFigure())
        !isStarted || return
        isStarted = true
        startFall()
    }

    private fun startFall() = launch(JavaFx) {
        while (isActive) {
            var falling = true
            board.drawFigure()
            while (falling) {
                delay(stepDelay)
                falling = board.moveFigure(Direction.DOWN)
            }
            if (gameOver()) {
                coroutineContext.cancel()
            } else {
                board.fixFigure()
                board.currentFigure = randomFigure()
            }
        }
        view.gameOver()
    }

    private fun gameOver(): Boolean = board.currentFigure.position.y <= 0

    private fun randomFigure(): Figure {
        val cls = figures.random
        val figure = cls.newInstance()
        figure.position = Point((AREA_WIDTH - figure.matrix.width) / 2, 0)
        println(figure)
        return figure
    }


    fun onLeftPressed() {
        board.moveFigure(Direction.LEFT)
    }

    fun onRightPressed() {
        board.moveFigure(Direction.RIGHT)
    }

    fun onUpPressed() {
        board.rotateFigure()
    }

    fun onDownPressed() {
        board.moveFigure(Direction.DOWN)
    }
}