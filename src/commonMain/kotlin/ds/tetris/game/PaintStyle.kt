/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.graphics.Color

sealed interface PaintStyle {
    val color: Color

    class Fill(override val color: Color) : PaintStyle
    class Stroke(override val color: Color) : PaintStyle
}