package ds.tetris.game

import ds.tetris.game.job.CoroutineJob
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

private const val DEFAULT_DELAY: Long = 30

class KeyCoroutine(
    context: CoroutineContext,
    private val startDelay: Long = DEFAULT_DELAY,
    private val delay: Long = DEFAULT_DELAY,
    private val callback: () -> Unit
) : CoroutineJob(context) {

    private var firstCall: Boolean = true

    override fun onStart() {
        firstCall = true
    }

    override fun provideJob(): Job = launch(context) {
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

