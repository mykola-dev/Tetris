package ds.tetris.android

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import ds.tetris.coroutines.coroutineContext
import ds.tetris.game.AREA_WIDTH
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class GameBoardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    init {
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private val radius = 8f
    private val gap = 2
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val transparentPaint = Paint().apply { alpha = 127 }

    private var canvas: Canvas? = null
    private var bitmap: Bitmap? = null

    private val brickSize get() = (width / AREA_WIDTH).toFloat()

    private fun drawBlockWithPaint(x: Int, y: Int, paint: Paint) {
        val left: Float = x * brickSize + gap
        val top: Float = y * brickSize + gap
        val right: Float = left + brickSize - gap * 2
        val bottom: Float = top + brickSize - gap * 2

        canvas?.drawRoundRect(
            left, top, right, bottom,
            radius, radius, paint
        )
        invalidate()
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
        canvas?.clearRect(x * brickSize, y * brickSize, brickSize, brickSize)
        invalidate()
    }

    fun clearArea() {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    suspend fun wipeLines(lines: List<Int>) {
        // fade nicely
        val brick = brickSize
        val iterations = 5
        repeat(iterations) {
            val paint = if (it == iterations - 1) clearPaint else transparentPaint
            for (line in lines) {
                canvas?.drawRect(0f, (line * brick).toFloat(), width.toFloat(), ((line + 1) * brick).toFloat(), paint)
            }
            invalidate()
            delay(50)
        }


        // animate nicely
        var globalOffset = 0f
        val animationJobs = mutableListOf<Job>()
        for (i in lines.size - 1 downTo 0) {
            val startline = if (i == 0) 0 else lines[i - 1] + 1
            val size = lines[i] - startline

            globalOffset += brick
            val offset = globalOffset
            if (size > 0) {
                animationJobs += launch(coroutineContext()) {
                    val h = size * brick
                    println("area start=$startline size=$size with offset $offset")
                    var y = startline * brick
                    val slice = Bitmap.createBitmap(bitmap, 0, y.toInt(), width, h.toInt())
                    val times = 10
                    for (j in 0..times) {
                        canvas?.clearRect(0f, y, width.toFloat(), h)
                        y = startline * brick + j * offset / times
                        canvas?.drawBitmap(slice, 0f, y, fillPaint)
                        invalidate()
                        delay(10)
                    }
                }
            }
        }

        animationJobs.forEach { it.join() }
    }

    private fun Canvas.clearRect(x: Float, y: Float, width: Float, height: Float) {
        canvas?.drawRect(x, y, x + width, y + height, clearPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

    }

    override fun onDraw(canvas: Canvas) {
        if (bitmap != null)
            canvas.drawBitmap(bitmap, 0f, 0f, fillPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originalHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        val calculatedWidth = originalHeight / 2
        super.onMeasure(
            View.MeasureSpec.makeMeasureSpec(calculatedWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(originalHeight, View.MeasureSpec.EXACTLY))
    }


}