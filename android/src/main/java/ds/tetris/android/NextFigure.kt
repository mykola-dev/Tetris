package ds.tetris.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ds.tetris.game.figures.Point

class NextFigure @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val bricks = mutableListOf<Point>()
    private val brickSize get() = (width / 4).toFloat()
    private val radius = 2f
    private val gap = 1

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

    override fun onDraw(canvas: Canvas) {
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
}