package ds.tetris.game.job

import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.Job

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
        job?.cancel(CancellationException("job ${javaClass.simpleName} stopped"))
        onStop()
    }

    open fun onStart() {}
    open fun onStop() {}
    abstract protected fun provideJob(): Job
}