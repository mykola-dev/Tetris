/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.unit.IntOffset
import ds.tetris.game.figures.BoardMatrix
import ds.tetris.game.figures.Brick
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.Matrix

internal class Board(val width: Int = AREA_WIDTH, val height: Int = AREA_HEIGHT) {

    var matrix: Matrix<PaintStyle?> = BoardMatrix(width, height)

    fun bakeFigure(figure: Figure) {
        figure.getPoints().forEach {
            matrix[it] = PaintStyle.Fill(figure.color)
        }
    }

    fun getBricks(): List<Brick> = matrix.array.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, item ->
            item?.let { Brick(IntOffset(colIndex, rowIndex), it) }
        }
    }.filterNotNull()

    fun getFilledRowsIndices(): Set<Int> = matrix.array
        .mapIndexed { i, row -> i to row }
        .filter { it.second.all { it != null } }
        .map { it.first }
        .toSet()


    fun clear() {
        matrix = BoardMatrix(width, height)
    }

    fun collides(figure: Figure): Boolean = figure.getPoints().any { matrix[it] != null }

    fun wipeLines(lines: Set<Int>) {
        val newMatrix = matrix.array.filterIndexed { i, _ -> i !in lines }.toMutableList()
        repeat(lines.size) {
            newMatrix.add(0, Array(width) { null })
        }
        matrix = BoardMatrix(newMatrix.toTypedArray())
    }


}