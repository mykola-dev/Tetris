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
        score += 10 * count * (1 + count / 2f).toInt()
        callback()
    }

    fun awardStart() {
        level++
        callback()
    }

    val shouldLevelUp: Boolean get() = score / level > 1000
}