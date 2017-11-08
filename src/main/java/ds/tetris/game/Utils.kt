package ds.tetris.game

import java.text.SimpleDateFormat
import java.util.Date

val <T> Array<T>.random get() = this[(Math.random() * size).toInt()]

fun log(s: String) = println("${getDate()}: $s")
private fun getDate(): String = SimpleDateFormat("kk:mm:ss.SS").format(Date())