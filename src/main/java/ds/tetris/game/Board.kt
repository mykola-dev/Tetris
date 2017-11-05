package ds.tetris.game

import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.Point

/**
 * This class represents the game area state and helper methods to manipulate with state
 */
class Board(val view: MainView, var currentFigure: Figure) {

    fun drawFigure() =
        currentFigure
            .points
            .forEach {
                view.drawBlockAt(it.x, it.y, currentFigure.color)
            }

    fun clearFigure() =
        currentFigure
            .points
            .forEach {
                view.clearBlockAt(it.x, it.y)
            }

    /**
     * @return true if success
     */
    fun moveFigure(dir: Direction): Boolean {
        canMove(dir) || return false
        clearFigure()
        with(currentFigure) {
            position = Point(position.x + dir.movement.x, position.y + dir.movement.y)
        }
        drawFigure()
        return true
    }

    private fun canMove(dir: Direction): Boolean {
        return true // todo
    }

    fun rotateFigure(f: Figure) {
        // todo
    }
}