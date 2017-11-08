package ds.tetris.game

import ds.tetris.game.Direction.*
import ds.tetris.game.figures.BitMatrix
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.Point

/**
 * This class represents the game area state and helper methods to manipulate with state
 */
class Board(private val view: GameView) {

    var area: BitMatrix = BitMatrix(AREA_HEIGHT, AREA_WIDTH) { x, y -> false }

    lateinit var currentFigure: Figure

    fun drawFigure() {
        with(currentFigure) {
            if (ghost == null) {
                // find ghost
                for (i in 0 until AREA_HEIGHT - position.y) {
                    val movement = Point(0, i)
                    if (!canMove(movement, currentFigure)) {
                        ghost = clone()
                        ghost!!.position = Point(position.x, position.y + i - 1)
                        break
                    }
                }
            }

            ghost?.points?.forEach {
                view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.STROKE)
            }

            points.forEach {
                view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.FILL)
            }
        }
    }

    private fun clearFigure(figure: Figure) {
        figure
            .points
            .forEach {
                view.clearBlockAt(it.x, it.y)

            }
        figure
            .ghost
            ?.points
            ?.forEach {
                view.clearBlockAt(it.x, it.y)
            }
    }

    /**
     * @return true if success
     */
    fun moveFigure(movement: Point, figure: Figure = currentFigure): Boolean {
        canMove(movement, figure) || return false
        clearFigure(figure)

        // invalidate ghost
        if (movement.x != 0)
            figure.ghost = null

        figure.position += movement
        drawFigure()
        return true
    }

    private fun canMove(movement: Point, figure: Figure): Boolean = figure
        .points
        .all {
            val nextPoint = it + movement
            !nextPoint.outOfArea() && !area[nextPoint]
        }

    private fun Point.outOfArea(): Boolean = x >= AREA_WIDTH || x < 0 || y >= AREA_HEIGHT || y < 0

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
                if (canMove(RIGHT.movement, this))
                    position += RIGHT.movement
                else if (canMove(LEFT.movement, this))
                    position += LEFT.movement
            }

        }

        if (newFigure.points.all { !area[it] }) {
            clearFigure(currentFigure)
            currentFigure = newFigure
            currentFigure.ghost = null
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

    fun startingPosition(figure:Figure) = Point((AREA_WIDTH - figure.matrix.width) / 2, 0)
}