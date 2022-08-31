package ds.tetris.ui

import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

object Palette {

    val primary = Color(0xffaaaaaa)
    val surface = Color(0xff3b3b3b)
    val level = Color(0xffffb640)
    val score = Color(0xff20baa0)
    val board=Color(0xff202020)
}

private val colors = darkColors(
    primary = Palette.primary,
    surface = Palette.surface,

)

@Composable
fun TetrisTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalElevationOverlay provides null,
    ) {
        MaterialTheme(colors, content = content)
    }
}