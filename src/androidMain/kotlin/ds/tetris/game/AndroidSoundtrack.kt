package ds.tetris.game

import android.content.Context
import com.soywiz.korio.android.withAndroidContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AndroidSoundtrack(private val context: Context, scope: CoroutineScope) : Soundtrack(scope) {

    override fun CoroutineScope.launchMultiplatform(task: suspend () -> Unit) {
        launch {
            withAndroidContext(context) {
                task()
            }
        }
    }
}