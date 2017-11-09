package ds.tetris.game

class Score(private val callback: Score.() -> Unit) {

    var score: Int = 0
        set(value) {
            field = value
            if (field / level > 2000)
                awardLevelUp()
        }

    var level: Int = 0

    fun awardSpeedUp() {
        score += level
        callback()
    }

    private fun awardLevelUp() {
        level++
        score += 100
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

}