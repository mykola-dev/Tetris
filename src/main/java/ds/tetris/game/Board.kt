package ds.tetris.game

import ds.tetris.game.Direction.*
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.Point

/**
 * This class represents the game area state and helper methods to manipulate with state
 */
class Board(private val view: MainView, var currentFigure: Figure) {

    private var area: BitMatrix = BitMatrix(AREA_HEIGHT, AREA_WIDTH) { x, y -> false }

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
        val newFigure = currentFigure.clone().apply {
            rotate()

            // edge cases
            while (!points.all { it.x >= 0 }) {
                position += RIGHT.movement
            }
            while (!points.all { it.x < AREA_WIDTH }) {
                position += LEFT.movement
            }
            while (!points.all { it.y < AREA_HEIGHT }) {
                position += UP.movement
            }

            // try to fix unexpected collisions
            if (!points.all { !area[it] }) {
                if (canMove(RIGHT))
                    position += RIGHT.movement
                else if (canMove(LEFT))
                    position += LEFT.movement
            }

        }

        if (newFigure.points.all { !area[it] }) {
            clearFigure()
            currentFigure = newFigure
            drawFigure()
        }
    }

    fun fixFigure() {
        currentFigure.points.forEach {
            area[it] = true
        }
    }

    fun getFilledLinesIndices(): List<Int> = area.array
        .mapIndexed { i, row -> i to row }
        .filter { it.second.all { it } }
        .map { it.first }

    fun wipeLines(lines: List<Int>) {
        val cropped = area.array.filter { !it.all { it } }.toMutableList()
        repeat(lines.size) {
            cropped.add(0, Array(AREA_WIDTH) { false })
        }
        area = BitMatrix(cropped.toTypedArray())
    }
}