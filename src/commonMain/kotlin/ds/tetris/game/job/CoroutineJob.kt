/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game.job

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

@Suppress("MemberVisibilityCanPrivate")
abstract class CoroutineJob {

    private var job: Job? = null

    val isRunning get() = job?.isActive == true

    fun start() {
        if (isRunning) return
        onStart()
        job = provideJob()
    }

    fun stop() {
        job?.cancel(CancellationException("job ${this::class.simpleName} stopped"))
        onStop()
    }

    open fun onStart() {}
    open fun onStop() {}
    protected abstract fun provideJob(): Job
}