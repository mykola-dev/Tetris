/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game.figures

import androidx.compose.ui.unit.IntOffset
import ds.tetris.game.PaintStyle

interface Matrix<T> {

    val array: Array<Array<T>>
    val width: Int
    val height: Int

    fun rotate() {
        val copy = array.copyOf().map { it.copyOf() }

        for (r in array.indices) {
            val row = array[r]
            for (c in row.indices) {
                array[r][c] = copy[row.size - c - 1][r]
            }
        }
    }

    operator fun get(row: Int, column: Int): T = array[row][column]
    operator fun set(row: Int, column: Int, value: T) {
        array[row][column] = value
    }

    operator fun get(p: IntOffset): T = this[p.y, p.x]
    operator fun set(p: IntOffset, value: T) {
        this[p.y, p.x] = value
    }

}

class BitMatrix(override val array: Array<Array<Boolean>>) : Matrix<Boolean> {

    override val width: Int get() = array[0].size
    override val height: Int get() = array.size


    override fun toString(): String {
        return array.joinToString("") {
            it.map { on -> if (on) '■' else '·' }.joinToString("") + "\n"
        }
    }

    companion object {

        operator fun invoke(builder: BitMatrixBuilder.() -> Unit): BitMatrix {
            val context = BitMatrixBuilder()
            context.builder()
            return BitMatrix(context.getArray())
        }

        fun createArea(width: Int, height: Int): BitMatrix {
            val array = Array(height) { y -> Array(width) { x -> false } }
            return BitMatrix(array)
        }
    }

}

class BitMatrixBuilder {
    private val rows: MutableList<String> = mutableListOf()
    operator fun String.unaryPlus() {
        rows += this
    }

    fun getArray() = rows
        .map { row ->
            row.map { it == '1' }.toTypedArray()
        }
        .toTypedArray()
}

class BoardMatrix(override val width: Int, override val height: Int) : Matrix<PaintStyle?> {
    override val array: Array<Array<PaintStyle?>> = Array(height) { y -> Array(width) { x -> null } }
}