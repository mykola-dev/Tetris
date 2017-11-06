package ds.tetris.game

import ds.tetris.game.figures.Point
import java.util.Arrays

interface Matrix<T> /*: Iterable<Point>*/ {

    val array: Array<Array<T>>
    val width: Int get() = array[0].size
    val height: Int get() = array.size

    fun rotate(): Matrix<T>


    operator fun get(row: Int, column: Int): T = array[row][column]
    operator fun get(p: Point): T = array[p.y][p.x]
    operator fun set(p: Point, value: T) {
        array[p.y][p.x] = value
    }
}

class BitMatrix(override val array: Array<Array<Boolean>>) : Matrix<Boolean> {

    /**
     * @return new instance of rotated Matrix
     */
    override fun rotate(): BitMatrix {
        val copy = Arrays.copyOf(array.map { Arrays.copyOf(it, it.size) }.toTypedArray(), array.size)

        for (r in 0 until array.size) {
            val row = array[r]
            for (c in 0 until row.size) {
                copy[r][c] = array[row.size - c - 1][r]
            }
        }
        return BitMatrix(copy)
    }

    override fun toString(): String {
        return array.joinToString("") {
            it.map { if (it) '■' else '·' }.joinToString("") + "\n"
        }
    }

    companion object {

        operator fun invoke(builder: MatrixBuilder.() -> Unit): BitMatrix {
            val context = MatrixBuilder()
            context.builder()
            return BitMatrix(context.getArray())
        }

        operator fun invoke(width: Int, height: Int, f: (x: Int, y: Int) -> Boolean): BitMatrix {
            val array = Array(width, { x -> Array(height, { y -> f(x, y) }) })
            return BitMatrix(array)
        }
    }
}

class MatrixBuilder {
    private val rows: MutableList<String> = mutableListOf()
    operator fun String.unaryPlus() {
        rows += this
    }

    fun getArray() = rows
        .map {
            it.map { it == '1' }.toTypedArray()
        }.toTypedArray()
}

