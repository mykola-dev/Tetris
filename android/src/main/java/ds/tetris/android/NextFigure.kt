package ds.tetris.android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import ds.tetris.game.NextFigureView

class NextFigure @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs), NextFigureView {
    override fun drawBlockAt(x: Int, y: Int, color: Int) {
    }

    override fun clearArea() {
    }
}