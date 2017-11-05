package ds.tetris.game

interface Matrix<T> /*: Iterable<Point>*/ {

    val array: Array<Array<T>>
    val width: Int get() = array[0].size

    operator fun get(row: Int, column: Int): T

    // todo
    fun rotate() {
        for (r in 0 until array.size) {
            val row = array[r]
            for (c in 0 until row.size) {

            }
        }
    }

}

class BitMatrix(builder: BitMatrix.() -> Unit) : Matrix<Boolean> {
    override lateinit var array: Array<Array<Boolean>>
    private val rows: MutableList<String> = mutableListOf()

    init {
        this.builder()
        array = rows.map {
            it.map { it == '1' }.toTypedArray()
        }.toTypedArray()
        rows.clear()
    }

    operator fun String.unaryPlus() {
        rows += this
    }

    override fun get(row: Int, column: Int): Boolean = array[row][column]

}

fun createTestMatrix() {
    val m = BitMatrix {
        +"0100"
        +"0100"
        +"0100"
        +"0100"
    }
}

