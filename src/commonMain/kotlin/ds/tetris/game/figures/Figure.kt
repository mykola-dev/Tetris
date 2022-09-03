/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game.figures

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import ds.tetris.game.PaintStyle
import ds.tetris.game.colors

/**
 * Used in UI layer
 */
data class Brick(
    val offset: IntOffset,
    val style: PaintStyle,
    val isFigure: Boolean = false
)

internal data class Figure(
    val matrix: BitMatrix,
    var offset: IntOffset,
    val color: Color = colors.random(),
) {

    var distance: Int = 0

    fun rotate(): Figure {
        val newMatrix = matrix.rotate()
        val newOffset = IntOffset((matrix.width - newMatrix.width) / 2, (matrix.height - newMatrix.height) / 2) + offset
        return copy(
            matrix = newMatrix,
            offset = newOffset
        )
    }

    fun getPoints(): List<IntOffset> = buildList {
        matrix.array.forEachIndexed { ri, r ->
            r.forEachIndexed { ci, item ->
                if (item) add(IntOffset(ci + offset.x, ri + offset.y))
            }
        }
    }

    private fun getGhostPoints(): List<IntOffset> {
        if (distance == 0) return emptyList()
        val points = getPoints()
        return points.map { it.copy(y = it.y + distance) }.filter { it !in points }
    }

    val allBricks: List<Brick>
        get() = getPoints().map { Brick(it, PaintStyle.Fill(color), true) } + getGhostPoints().map { Brick(it, PaintStyle.Stroke(color)) }
}

internal object FigureFactory {

    fun create(): Figure = Figure(figures.random(), IntOffset(0, 0))

}