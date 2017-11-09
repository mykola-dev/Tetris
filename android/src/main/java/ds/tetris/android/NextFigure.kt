package ds.tetris.android

import android.content.Context
import android.util.AttributeSet
import android.view.View

class NextFigure @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    fun fillBlockAt(x: Int, y: Int, color: Int) {

    }
}