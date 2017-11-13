/*
 * © 2017 Deviant Studio
 */

package ds.tetris.android

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import ds.tetris.game.Game
import ds.tetris.game.GameView
import ds.tetris.game.PaintStyle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI

class MainActivity : AppCompatActivity(), GameView {

    private var game: Game? = null
    private lateinit var soundtrack: SoundtrackAndroid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        soundtrack = SoundtrackAndroid(this)
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        game?.stop()
        soundtrack.release()
    }

    private fun initListeners() {
        startButton.setOnClickListener {
            game?.stop()
            game = Game(this, soundtrack, UI)
            game?.soundEnabled = soundCheck.isChecked
            game?.start()

            startButton.text = "Restart"
            pauseButton.text = "Pause"
        }
        soundCheck.setOnCheckedChangeListener { _, isChecked ->
            game?.soundEnabled = isChecked
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
        boardView.wipeLines(lines, game?.getTopBrickLine() ?: 0)
    }

    override fun drawPreviewBlockAt(x: Int, y: Int, color: Int) {
        nextFigure.fillBlockAt(x, y, color)
    }

    override fun clearPreviewArea() {
        nextFigure.clear()
    }

    override fun invalidate() {
        boardView.invalidate()
        nextFigure.invalidate()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> game?.onLeftPressed()
            KeyEvent.KEYCODE_DPAD_RIGHT -> game?.onRightPressed()
            KeyEvent.KEYCODE_DPAD_UP -> game?.onUpPressed()
            KeyEvent.KEYCODE_DPAD_DOWN -> game?.onDownPressed()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> game?.onLeftReleased()
            KeyEvent.KEYCODE_DPAD_RIGHT -> game?.onRightReleased()
            KeyEvent.KEYCODE_DPAD_DOWN -> game?.onDownReleased()
        }
        return super.onKeyUp(keyCode, event)
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
