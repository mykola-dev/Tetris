package ds.tetris.game

interface GameView {
    var score: Int
    var level: Int

    fun drawBlockAt(x: Int, y: Int, color: Int)
    fun clearBlockAt(x: Int, y: Int)
    fun gameOver()
    fun clearArea()
    suspend fun wipeLines(lines: List<Int>)
}