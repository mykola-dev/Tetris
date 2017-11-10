package ds.tetris.android

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import ds.tetris.game.figures.Point
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.newSingleThreadContext

class NextFigure @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs) {

    private val holderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder?) {
            surfaceReady = true
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            surfaceReady = false
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    }

    private var surfaceReady = false

    private val surfaceActor = actor<Unit>(newSingleThreadContext("mini surface")) {
        while (isActive) {
            receive()
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas(null)
                if (canvas != null)
                    doDrawing(canvas)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bricks = mutableListOf<Point>()
    private val brickSize get() = (width / 4).toFloat()
    private val radius = 8f
    private val gap = 2

    init {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(holderCallback)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    fun fillBlockAt(x: Int, y: Int, color: Int) {
        paint.color = color
        bricks += Point(x, y)
    }

    fun clear() {
        bricks.clear()
    }

    private fun doDrawing(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        for (b in bricks) {
            val left: Float = b.x * brickSize + gap
            val top: Float = b.y * brickSize + gap
            val right: Float = left + brickSize - gap * 2
            val bottom: Float = top + brickSize - gap * 2

            canvas.drawRoundRect(
                left, top, right, bottom,
                radius, radius, paint
            )
        }
    }

    override fun invalidate() {
        super.invalidate()
        if (surfaceReady)
            surfaceActor.offer(Unit)
    }
}