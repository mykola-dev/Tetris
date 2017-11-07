package ds.tetris.game

import ds.tetris.game.figures.Point
import java.util.Arrays

interface Matrix<T> {

    val array: Array<Array<T>>
    val width: Int get() = array[0].size
    val height: Int get() = array.size

    /**
     * @return new instance of rotated Matrix
     */
    fun rotate()

    operator fun get(row: Int, column: Int): T = array[row][column]
    operator fun set(row: Int, column: Int, value: T) {
        array[row][column] = value
    }

    operator fun get(p: Point): T = this[p.y, p.x]
    operator fun set(p: Point, value: T) {
        this[p.y, p.x] = value
    }

}

class BitMatrix(override val array: Array<Array<Boolean>>) : Matrix<Boolean> {

    override fun rotate() {
        val copy = copyArray()

        for (r in 0 until array.size) {
            val row = array[r]
            for (c in 0 until row.size) {
                array[r][c] = copy[row.size - c - 1][r]
            }
        }
    }

    private fun copyArray() = Arrays.copyOf(array.map { Arrays.copyOf(it, it.size) }.toTypedArray(), array.size)

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

    fun clone(): BitMatrix = BitMatrix(copyArray())

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

