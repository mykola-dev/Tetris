/*
 * © 2022 Deviant Studio
 */

package ds.tetris.game.job

import ds.tetris.game.Direction
import ds.tetris.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_DELAY: Long = 40
private const val DEFAULT_START_DELAY: Long = 120

class KeysProducer(override val coroutineContext: CoroutineContext) : CoroutineScope {

    operator fun invoke(direction: Direction?) {
        log.v("key=$direction")
        inputChannel.trySend(direction)
    }

    private val inputChannel = Channel<Direction?>()

    val outputChannel = produce<Direction> {
        var initial = true
        var currentKey: Direction? = null
        while (isActive) {
            currentKey = select {
                inputChannel.onReceive {
                    if (currentKey == null) initial = true
                    it
                }

                val delay = when {
                    currentKey == null -> Long.MAX_VALUE
                    initial && currentKey != Direction.DOWN -> DEFAULT_START_DELAY
                    else -> DEFAULT_DELAY
                }
                onTimeout(delay) {
                    initial = false
                    currentKey
                }
            }
            if (currentKey != null) trySend(currentKey)

        }
    }


}
