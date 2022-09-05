package ds.tetris.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import org.jetbrains.skiko.loadBytesFromPath

actual suspend fun platformTypography(): Typography = Typography(
    defaultFontFamily = FontFamily(
        Font(
            identity = "roboto",
            data = loadBytesFromPath("Roboto-Regular.ttf")
        )
    )
)