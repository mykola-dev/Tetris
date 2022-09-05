/*
 * © 2022 Deviant Studio
 */

package ds.tetris.ui

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ds.tetris.game.PaintStyle
import ds.tetris.game.figures.Brick
import ds.tetris.ui.Palette.spectrumColors
import ds.tetris.util.log
import kotlin.math.absoluteValue

private const val gap = 2
private const val radius = 4

enum class AnimationPhase {
    IDLE, WIPING, SHIFTING
}

@Composable
fun Board(
    boardBricks: List<Brick>,
    figure: List<Brick>,
    wipedLines: Set<Int>,
    rotationPivot: Offset?,
    boardSize: IntSize,
    gameOver: Boolean,
    onWipingDone: () -> Unit,
    onRotationDone: () -> Unit,
    animationEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier.aspectRatio(boardSize.width / boardSize.height.toFloat()), contentAlignment = Alignment.Center) {
        var phase: AnimationPhase by remember { mutableStateOf(AnimationPhase.IDLE) }

        val wipeColorAnimated: Int by animateIntAsState(
            targetValue = if (phase == AnimationPhase.WIPING) 7 else 0,
            animationSpec = tween(200, easing = LinearEasing)
        ) { value ->
            if (value == 7) phase = AnimationPhase.SHIFTING
        }
        val wipeSizeAnimated: Float by animateFloatAsState(
            targetValue = if (phase == AnimationPhase.SHIFTING) 1f else 0f,
            animationSpec = tween(200, easing = CubicBezierEasing(0.0f, 0.0f, 0.8f, 0.1f))
        ) { value ->
            if (value == 1f) {
                onWipingDone()
                phase = AnimationPhase.IDLE
            }
        }

        val (mainBody, ghostBody) = remember(figure) { figure.partition { it.isFigure } }
        val degrees = remember { Animatable(0f) }

        val rememberMainBody by rememberUpdatedState(mainBody)
        var previousFigure by remember(rotationPivot, animationEnabled) { mutableStateOf(mainBody) }
        val direction = remember(mainBody, previousFigure) { mainBody.getPosition() - previousFigure.getPosition() }
        var transitionRunning by remember { mutableStateOf(false) }

        val animationKey by produceState(0, direction) {
            //log.v("on key $direction curr=${mainBody.getPosition()} prev=${previousFigure.getPosition()}")

            if (!animationEnabled) {
                value = 0
            } else if (direction.y > 1 || direction.x.absoluteValue > 1) {
                log.w("too fast")
                value++
                /* } else if (direction.y < -1) {
                     log.w("new figure")
                     transitionRunning = false
                     value++*/
            } else if (!transitionRunning && direction != IntOffset.Zero) {
                log.w("starting new animation")
                transitionRunning = true
                value++
            } else {
                // do nothing
            }
        }
        val translateAnimation = remember { Animatable(-1f) }

        if (animationEnabled) {
            LaunchedEffect(animationKey) {
                log.v("key=$animationKey")
                try {
                    translateAnimation.snapTo(-1f)
                    translateAnimation.animateTo(0f, tween(100, easing = LinearEasing))
                } catch (e: Exception) {
                    //e.printStackTrace()
                    log.e(e.message!!)
                } finally {
                    previousFigure = rememberMainBody
                    transitionRunning = false
                    translateAnimation.snapTo(-1f)
                }
            }
        }

        if (rotationPivot != null) {
            if (animationEnabled) {
                LaunchedEffect(rotationPivot) {
                    degrees.animateTo(90f, tween(100))
                    onRotationDone()
                    degrees.snapTo(0f)
                }
            } else {
                onRotationDone()
            }
        }

        Canvas(Modifier.fillMaxSize()) {

            drawRect(Palette.board)
            val brickSize = this.size.width / boardSize.width

            if (phase in setOf(AnimationPhase.IDLE, AnimationPhase.WIPING)) {

                if (!gameOver) {
                    drawBricks(boardBricks + ghostBody, brickSize)
                } else {
                    drawBricks(boardBricks.map { it.copy(style = PaintStyle.Fill(Color.Red)) }, brickSize)
                }
            }

            // rotation and translation
            if (animationEnabled) {
                withTransform({
                    val transFactor = translateAnimation.value * brickSize
                    if (rotationPivot == null /*&& transitionRunning*/ && direction.y >= 0) {
                        //log.d("figure=${previousFigure.getPosition()} dir=$direction animation=${transFactor} isRunning=$transitionRunning y=${brickSize * (direction.y * translateAnimation.value + previousFigure.getPosition().y)}")
                        translate(top = transFactor * direction.y, left = transFactor * direction.x)
                    }
                    if (rotationPivot != null) rotate(degrees.value, rotationPivot * brickSize)
                }) {
                    drawBricks(mainBody, brickSize)
                }
            } else {
                drawBricks(mainBody, brickSize)
            }

            when (phase) {
                AnimationPhase.WIPING -> {
                    wipedLines.forEach { y ->
                        (0 until boardSize.width)
                            .map { x -> Brick(IntOffset(x, y), PaintStyle.Fill(spectrumColors[wipeColorAnimated])) }
                            .let { drawBricks(it, brickSize) }
                    }
                }
                AnimationPhase.SHIFTING -> {
                    val groupedBricks = (boardBricks + ghostBody).groupBy { b ->
                        wipedLines.firstOrNull { b.offset.y < it /*&& !b.isFigure*/ } ?: -1
                    }

                    wipedLines.reversed().forEachIndexed { i, row ->
                        val selectedBricks = groupedBricks[row] ?: return@forEachIndexed
                        //Napier.v("grouped bricks for $row: ${selectedBricks.size}")
                        val y = (row) * brickSize
                        val yOffset = wipeSizeAnimated * brickSize * (i + 1)

                        withTransform({
                            inset(0f, 0f, 0f, size.height - y)
                            translate(0f, yOffset)
                        }) {
                            //drawRect(Color.White)
                            this.drawBricks(selectedBricks, brickSize)
                        }
                    }
                    // draw rest
                    groupedBricks[-1]?.let {
                        drawBricks(it, brickSize)
                    }

                }
                AnimationPhase.IDLE -> {
                    if (wipedLines.isNotEmpty()) {
                        phase = AnimationPhase.WIPING
                    }
                }
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
    Canvas(Modifier.aspectRatio(1f).padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        val brickSize = this.size.width / 4
        drawRect(Color.Transparent)
        drawBricks(next, brickSize)
    }
}

fun DrawScope.drawBricks(bricks: List<Brick>, brickSize: Float) {
    val rectSize = Size(brickSize - gap * 2, brickSize - gap * 2)
    val radius = CornerRadius(radius.dp.toPx(), radius.dp.toPx())
    bricks.forEach { brick ->
        val style: DrawStyle = if (brick.style is PaintStyle.Fill) Fill else Stroke(1.dp.toPx())
        val offset = Offset(brick.offset.x * brickSize + gap, brick.offset.y * brickSize + gap)
        drawRoundRect(brick.style.color, offset, rectSize, radius, style)
    }
}

private fun List<Brick>.getPosition(): IntOffset = IntOffset(minOfOrNull { it.offset.x } ?: 0, minOfOrNull { it.offset.y } ?: 0)