package ds.tetris.util

import javafx.scene.paint.Color

fun Int.toColor(): Color {
    val red = this shr 16 and 0xFF
    val green = this shr 8 and 0xFF
    val blue = this and 0xFF
    return Color.rgb(red, green, blue)
}