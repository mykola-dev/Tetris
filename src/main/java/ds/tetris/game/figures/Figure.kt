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
    var color: Int
    val points: Sequence<Point>

    fun clone(): Figure = javaClass.newInstance().also {
        it.matrix = matrix
        it.position = position
        it.color = color
        it.matrix = matrix.clone()
    }

    fun rotate() {
        matrix.rotate()
    }
}

abstract class BaseFigure(override var color: Int = colors.random) : Figure {
    override var position: Point = Point(0, 0)

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