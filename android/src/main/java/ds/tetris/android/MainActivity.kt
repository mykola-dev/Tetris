package ds.tetris.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        game = Game(this, nextFigure, UI)
        game?.start()
    }

    override var score: Int = 0
        set(value) {
            field = value
        }
    override var level: Int = 0
        set(value) {
            field = value
        }

    override fun drawBlockAt(x: Int, y: Int, color: Int, style: PaintStyle) {
    }

    override fun clearBlockAt(x: Int, y: Int) {
    }

    override fun gameOver() {
    }

    override fun clearArea() {
    }

    suspend override fun wipeLines(lines: List<Int>) {
    }
}