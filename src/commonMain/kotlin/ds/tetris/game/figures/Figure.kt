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
)

internal data class Figure(
    val matrix: BitMatrix,
    var offset: IntOffset,
    val color: Color = colors.random(),
    var ghost: Figure? = null,

    ) {
    fun rotate() {
        matrix.rotate()
    }

    val points: List<IntOffset> = buildList {
        for (r in matrix.array.indices) {
            val row = matrix.array[r]
            for (c in row.indices) {
                val item = row[c]
                if (item) {
                    add(IntOffset(c + offset.x, r + offset.y))
                }
            }
        }
    }

    val allBricks: List<Brick> = points.map { Brick(it, PaintStyle.Fill(color)) } + (ghost?.points?.map { Brick(it, PaintStyle.Stroke(color)) } ?: emptyList())
}

internal object FigureFactory {

    fun create(): Figure = Figure(figures.random(), IntOffset(0, 0))

    //fun createNext(): Figure = Figure(figures.random(), Point(0, 0))
}