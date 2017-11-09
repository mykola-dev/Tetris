package ds.tetris.android

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import ds.tetris.game.Game
import ds.tetris.game.GameView
import ds.tetris.game.PaintStyle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI

class MainActivity : AppCompatActivity(), GameView {

    private var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        game?.stop()
    }

    private fun initListeners() {
        startButton.setOnClickListener {
            game?.stop()
            game = Game(this, UI)
            game?.start()

            startButton.text = "Restart"
            pauseButton.text = "Pause"
        }
        pauseButton.setOnClickListener {
            game?.pause()
            if (game?.isPaused == true)
                pauseButton.text = "Resume"
            else
                pauseButton.text = "Pause"
        }
        leftButton.setOnTouchListener(ButtonTouchListener({ game?.onLeftPressed() }, { game?.onLeftReleased() }))
        rightButton.setOnTouchListener(ButtonTouchListener({ game?.onRightPressed() }, { game?.onRightReleased() }))
        upButton.setOnTouchListener(ButtonTouchListener({ game?.onUpPressed() }, { }))
        downButton.setOnTouchListener(ButtonTouchListener({ game?.onDownPressed() }, { game?.onDownReleased() }))
    }

    override var score: Int = 0
        set(value) {
            field = value
            scoreLabel.text = "Score: $value"
        }

    override var level: Int = 0
        set(value) {
            field = value
            levelLabel.text = "Level: $value"
        }

    override fun drawBlockAt(x: Int, y: Int, color: Int, style: PaintStyle) {
        when (style) {
            PaintStyle.FILL -> boardView.fillBlockAt(x, y, color)
            PaintStyle.STROKE -> boardView.strokeBlockAt(x, y, color)
        }

    }

    override fun clearBlockAt(x: Int, y: Int) {
        boardView.clearBlockAt(x, y)
    }

    override fun gameOver() {
        AlertDialog.Builder(this)
            .setMessage("Game Over")
            .setPositiveButton(android.R.string.ok, { d, _ -> d.dismiss() })
            .setCancelable(false)
            .show()
    }

    override fun clearArea() {
        boardView.clearArea()
    }

    suspend override fun wipeLines(lines: List<Int>) {
        boardView.wipeLines(lines)
    }

    override fun drawPreviewBlockAt(x: Int, y: Int, color: Int) {
        nextFigure.fillBlockAt(x, y, color)
    }

    override fun clearPreviewArea() {
        nextFigure.invalidate()
    }
}

class ButtonTouchListener(private val pressed: () -> Unit, private val released: () -> Unit) : View.OnTouchListener {

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> pressed()
            MotionEvent.ACTION_UP -> released()
        }
        return false
    }

}
