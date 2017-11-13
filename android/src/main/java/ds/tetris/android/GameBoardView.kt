/*
 * © 2017 Deviant Studio
 */

package ds.tetris.android

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import ds.tetris.game.AREA_WIDTH
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import kotlin.system.measureNanoTime

class GameBoardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseSurfaceView(context, attrs) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val transparentPaint = Paint().apply { alpha = 127 }

    private var canvas: Canvas? = null
    private var bitmap: Bitmap? = null

    private val animationDispatcher = newSingleThreadContext("animation")

    private val brickSize get() = (width / AREA_WIDTH).toFloat()

    private fun drawBlockWithPaint(x: Int, y: Int, paint: Paint, gap: Int = 2, radius: Float = 8f) {
        val left: Float = x * brickSize + gap
        val top: Float = y * brickSize + gap
        val right: Float = left + brickSize - gap * 2
        val bottom: Float = top + brickSize - gap * 2

        canvas?.drawRoundRect(
            left, top, right, bottom,
            radius, radius, paint
        )
    }

    fun fillBlockAt(x: Int, y: Int, color: Int) {
        fillPaint.color = color
        drawBlockWithPaint(x, y, fillPaint)
    }

    fun strokeBlockAt(x: Int, y: Int, color: Int) {
        strokePaint.color = color
        drawBlockWithPaint(x, y, strokePaint)
    }

    fun clearBlockAt(x: Int, y: Int) {
        drawBlockWithPaint(x, y, clearPaint, 0, 0f)
    }

    fun clearArea() {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    suspend fun wipeLines(lines: List<Int>, first: Int) {

        // fade nicely
        val brick = brickSize
        val iterations = 5
        repeat(iterations) {
            val paint = if (it == iterations - 1) clearPaint else transparentPaint
            for (line in lines) {
                canvas?.drawRect(0f, line * brick, width.toFloat(), (line + 1) * brick, paint)
            }
            invalidate()
            delay(50)
        }

        // animate nicely
        var globalOffset = 0
        val animationJobs = mutableListOf<Job>()
        for (i in lines.size - 1 downTo 0) {
            globalOffset++
            val distance = globalOffset * brick
            val startline = if (i == 0) {
                first - globalOffset
            } else {
                lines[i - 1] + 1
            }
            val size = lines[i] - startline

            if (size > 0) {
                animationJobs += launch(animationDispatcher) {
                    val h: Float = size * brick
                    println("area start=$startline size=$size with distance $distance")
                    var y: Float = startline * brick
                    val slice = Bitmap.createBitmap(bitmap, 0, y.toInt(), width, h.toInt())
                    val times = 9
                    for (j in 0..times) {
                        profile("draw canvas") {
                            val nanos = measureNanoTime {
                                canvas?.drawRect(0f, y, width.toFloat(), y + h, clearPaint)
                                y = startline * brick + j * distance / times
                                canvas?.drawBitmap(slice, 0f, y, fillPaint)
                                postInvalidate()
                            }
                            delay((16 - Math.round(nanos / 1000000.0)).coerceAtLeast(0))
                        }
                    }
                }
            }
        }
        animationJobs.forEach { it.join() }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

    }

    override fun onDraw(canvas: Canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, fillPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originalHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val calculatedWidth = originalHeight / 2
        super.onMeasure(
            View.MeasureSpec.makeMeasureSpec(calculatedWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(originalHeight, View.MeasureSpec.EXACTLY))
    }


}