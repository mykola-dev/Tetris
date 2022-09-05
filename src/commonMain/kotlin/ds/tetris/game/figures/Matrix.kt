/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game.figures

import androidx.compose.ui.unit.IntOffset
import ds.tetris.game.PaintStyle

interface Matrix<T> {

    val array: Array<Array<T>>
    val width: Int get() = array[0].size
    val height: Int get() = array.size

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

    fun rotate(): BitMatrix {

        val newArray = Array(width) { Array(height) { false } }

        array.forEachIndexed { ri, r ->
            r.forEachIndexed { ci, _ ->
                newArray[ci][ri] = array[array.size - ri - 1][ci]
            }
        }

        return BitMatrix(newArray)
    }

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

class BoardMatrix(override val array: Array<Array<PaintStyle?>>) : Matrix<PaintStyle?> {
    companion object {
        operator fun invoke(width: Int, height: Int): BoardMatrix = BoardMatrix(Array(height) { Array(width) { null } })
    }

}