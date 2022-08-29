/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game.job

import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive
import kotlin.coroutines.CoroutineContext


private const val DEFAULT_DELAY: Long = 40
private const val DEFAULT_START_DELAY: Long = 100

class KeyCoroutine(
    private val scope: CoroutineScope,
    private val delay: Long = DEFAULT_DELAY,
    private val startDelay: Long = DEFAULT_START_DELAY,
    private val callback: () -> Unit
) : CoroutineJob(), CoroutineScope by scope {

    private var firstCall: Boolean = true

    override fun onStart() {
        firstCall = true
    }

    override fun provideJob(): Job = launch(Dispatchers.Main) {
        while (isActive) {
            callback()
            if (firstCall) {
                firstCall = false
                delay(startDelay)
            } else {
                delay(delay)
            }
        }
    }
}

