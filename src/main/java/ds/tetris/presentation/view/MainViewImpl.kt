package ds.tetris.presentation.view

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.instance
import ds.tetris.di.mainComponent
import ds.tetris.game.AREA_HEIGHT
import ds.tetris.game.AREA_WIDTH
import ds.tetris.game.Game
import ds.tetris.game.MainView
import ds.tetris.util.toColor
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import tornadofx.*

const val BRICK_SIZE = 40

class MainViewImpl : View("Tetris"), MainView, KodeinAware {

    val bgColor = Color.BLACK

    override val kodein: Kodein = mainComponent

    val game: Game = instance()

    private val scoreProperty = SimpleIntegerProperty()
    override var score: Int by scoreProperty

    private var canvas: Canvas by singleAssign()

    private val keyDownListener: (KeyEvent) -> Unit = {
        println("Pressed ${it.code}")
        when (it.code) {
            KeyCode.LEFT -> game.onLeftPressed()
            KeyCode.RIGHT -> game.onRightPressed()
            KeyCode.UP -> game.onUpPressed()
            KeyCode.DOWN -> game.onDownPressed()
            else -> {
            }
        }
    }

    private val keyUpListener: (KeyEvent) -> Unit = {
        println("Released ${it.code}")
        when (it.code) {
            KeyCode.DOWN -> game.onDownReleased()
            else -> {
            }
        }
    }

    override val root: Parent = vbox {
        setOnKeyPressed(keyDownListener)
        setOnKeyReleased(keyUpListener)

        hbox(32, Pos.CENTER_LEFT) {
            paddingAll = 16
            button("Start Game").action {
                game.start(this@MainViewImpl)
            }
            label(scoreProperty.stringBinding { "Score: $it" }) {
                useMaxWidth = true
            }
        }
        stackpane {
            style {
                backgroundColor += bgColor
            }
            canvas = canvas((BRICK_SIZE * AREA_WIDTH).toDouble(), (BRICK_SIZE * AREA_HEIGHT).toDouble())
        }
    }

    override fun onDock() {
    }

    override fun drawBlockAt(x: Int, y: Int, color: Int) {
        with(canvas.graphicsContext2D) {
            fill = color.toColor()
            fillRect(
                (x * BRICK_SIZE).toDouble(),
                (y * BRICK_SIZE).toDouble(),
                BRICK_SIZE.toDouble(),
                BRICK_SIZE.toDouble()
            )
        }
    }

    override fun clearBlockAt(x: Int, y: Int) {
        with(canvas.graphicsContext2D) {
            clearRect(
                (x * BRICK_SIZE).toDouble(),
                (y * BRICK_SIZE).toDouble(),
                BRICK_SIZE.toDouble(),
                BRICK_SIZE.toDouble()
            )
        }
    }
}