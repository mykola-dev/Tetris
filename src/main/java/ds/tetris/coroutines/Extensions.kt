package ds.tetris.coroutines

import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

// https://github.com/Kotlin/kotlinx.coroutines/issues/114
suspend fun coroutineContext(): CoroutineContext = suspendCoroutineOrReturn { cont -> cont.context }