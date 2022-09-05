package ds.tetris.ui

import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

object Palette {

    val primary = Color(0xffaaaaaa)
    val surface = Color(0xff3b3b3b)
    val level = Color(0xffffb640)
    val score = Color(0xff20baa0)
    val board = Color(0xff202020)

    val spectrumColors = arrayOf(
        Color(0xffDEDEDF),
        Color(0xffDEDE01),
        Color(0xff00DEDF),
        Color(0xff00DE01),
        Color(0xffDE00DF),
        Color(0xffDE0001),
        Color(0xff0000DF),
        board,
    )
}

expect suspend fun platformTypography(): Typography

private val colors = darkColors(
    primary = Palette.primary,
    surface = Palette.surface,
)

@Composable
fun TetrisTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalElevationOverlay provides null,
    ) {
        var typography by remember { mutableStateOf(Typography()) }
        LaunchedEffect(Unit) {
            typography = platformTypography()
        }
        MaterialTheme(colors, typography, content = content)
    }
}