/*
 * © 2017 Deviant Studio
 */

package ds.tetris.android

import android.util.Log

inline fun <T> profile(name: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    Log.i("profile", "$name ---> ${System.currentTimeMillis() - start}ms")
    return result
}