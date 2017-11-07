package ds.tetris.game

class Score(private val callback: () -> Unit) {

    var score: Int = 0
    var level: Int = 0

    fun awardSpeedUp() {
        score += level
        callback()
    }

    fun awardLevelUp() {
        score += 100
        level++
        callback()
    }

    fun awardLinesWipe(count: Int) {
        score += when (count) {
            1 -> 100
            2 -> 250
            3 -> 500
            4 -> 1000
            else -> 0
        }
        callback()
    }

    fun awardStart() {
        level++
        callback()
    }

    val shouldLevelUp: Boolean get() = score / level > 5000
}