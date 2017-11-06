package ds.tetris.game

import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.Point

/**
 * This class represents the game area state and helper methods to manipulate with state
 */
class Board(private val view: MainView, var currentFigure: Figure) {

    private val area: BitMatrix = BitMatrix(AREA_HEIGHT, AREA_WIDTH) { x, y -> false }

    fun drawFigure() = currentFigure
        .points
        .forEach {
            view.drawBlockAt(it.x, it.y, currentFigure.color)
        }

    private fun clearFigure() = currentFigure
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

    private fun canMove(dir: Direction): Boolean = currentFigure
        .points
        .all {
            val nextPoint = it + dir.movement
            !outOfArea(nextPoint) && !area[nextPoint]
        }

    private fun outOfArea(p: Point): Boolean = p.x >= AREA_WIDTH || p.x < 0 || p.y >= AREA_HEIGHT || p.y < 0

    fun rotateFigure() {
        // todo
    }

    fun fixFigure() {
        currentFigure.points.forEach {
            area[it] = true
        }
        println("Area:\n$area")
    }
}