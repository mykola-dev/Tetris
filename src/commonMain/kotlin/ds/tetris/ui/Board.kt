/*
 * © 2022 Deviant Studio
 */

package ds.tetris.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ds.tetris.game.PaintStyle
import ds.tetris.game.figures.Brick

private const val gap = 2
private const val radius = 4

enum class AnimationPhase {
    IDLE, WIPING, SHIFTING
}

@Composable
fun Board(
    bricks: List<Brick>,
    wipedLines: List<Int>,
    size: IntSize,
    gameOver: Boolean,
    onWipingDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.aspectRatio(size.width / size.height.toFloat()), contentAlignment = Alignment.Center) {
        var phase: AnimationPhase by remember { mutableStateOf(AnimationPhase.IDLE) }
        if (wipedLines.isNotEmpty()) {
            if (phase == AnimationPhase.IDLE) phase = AnimationPhase.WIPING

            onWipingDone()
        }

        LaunchedEffect(phase) {


        }

        Canvas(Modifier.fillMaxSize()) {
            drawRect(Palette.board)

            if (!gameOver) {
                drawBricks(bricks, size)
            } else {
                drawBricks(bricks.map { it.copy(style = PaintStyle.Fill(Color.Red)) }, size)
            }


        }

        if (gameOver) {
            Surface(color = Color.White, shape = MaterialTheme.shapes.medium, elevation = 8.dp) {
                Text("GAME OVER", fontSize = 30.sp, color = Color.Black, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun NextFigure(next: List<Brick>) {
    Canvas(Modifier.aspectRatio(1f)) {
        drawRect(Color.Transparent)
        drawBricks(next, IntSize(4, 4))
    }
}

fun DrawScope.drawBricks(bricks: List<Brick>, size: IntSize) {

    val brickSize = this.size.width / size.width
    val rectSize = Size(brickSize - gap * 2, brickSize - gap * 2)
    val radius = CornerRadius(radius.dp.toPx(), radius.dp.toPx())
    bricks.forEach { brick ->
        val style: DrawStyle = if (brick.style is PaintStyle.Fill) Fill else Stroke(1.dp.toPx())
        val offset = Offset(brick.offset.x * brickSize + gap, brick.offset.y * brickSize + gap)
        drawRoundRect(brick.style.color, offset, rectSize, radius, style)
    }
}
