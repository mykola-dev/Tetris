package ds.tetris.game.figures

import ds.tetris.game.BitMatrix
import ds.tetris.game.colors
import ds.tetris.game.random
import kotlin.coroutines.experimental.buildSequence

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

interface Figure {
    var position: Point
    var matrix: BitMatrix
    val color: Int
    val points: Sequence<Point>

}

abstract class BaseFigure : Figure {
    override lateinit var position: Point
    override val color: Int = colors.random

    override val points: Sequence<Point> = buildSequence {
        for (r in 0 until matrix.array.size) {
            val row = matrix.array[r]
            for (c in 0 until row.size) {
                val item = row[c]
                if (item)
                    yield(Point(c + position.x, r + position.y))
            }
        }
    }

    override fun toString(): String = "${javaClass.simpleName}\n$matrix"
}

