/*
 * © 2022 Deviant Studio
 */

package ds.tetris.game.job

import ds.tetris.game.Direction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_DELAY: Long = 40
private const val DEFAULT_START_DELAY: Long = 100

class KeysJob(override val coroutineContext: CoroutineContext) : CoroutineScope {

    private val delay: Long = DEFAULT_DELAY
    private val startDelay: Long = DEFAULT_START_DELAY

    private var initial: Boolean = true

    var pressedKey: Direction? = null
        set(value) {
            if (field == null && value != null) {
                initial = true
            }
            field = value
        }

    val channel = produce<Direction> {
        while (isActive) {
            pressedKey?.let { key ->
                trySend(key)
                if (initial) {
                    initial = false
                    delay(startDelay)
                } else {
                    delay(delay)
                }
            }
        }
    }


}
