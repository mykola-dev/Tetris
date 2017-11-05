package ds.tetris.game

interface MainView {
    var score: Int

    fun drawBlockAt(x: Int, y: Int, color: Int)
    fun clearBlockAt(x: Int, y: Int)
}