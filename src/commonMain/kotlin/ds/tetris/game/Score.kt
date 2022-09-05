/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

class Score(private val callback: Score.() -> Unit) {

    var points: Int = 0
        set(value) {
            field = value
            if (field / level > 2000)
                awardLevelUp()
        }

    var level: Int = 1

    fun awardSpeedUp() {
        points += level
        callback()
    }

    private fun awardLevelUp() {
        level++
        points += 100
        callback()
    }

    fun awardLinesWipe(count: Int) {
        points += when (count) {
            1 -> 100
            2 -> 250
            3 -> 500
            4 -> 1000
            else -> 0
        }
        callback()
    }

    fun reset() {
        level = 1
        points = 0
        callback()
    }

}