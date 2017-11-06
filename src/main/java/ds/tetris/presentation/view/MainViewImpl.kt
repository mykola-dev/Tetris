package ds.tetris.presentation.view

import ds.tetris.game.AREA_HEIGHT
import ds.tetris.game.AREA_WIDTH
import ds.tetris.game.Game
import ds.tetris.game.MainView
import ds.tetris.util.toColor
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.transform.Transform
import javafx.stage.Screen
import tornadofx.*
import java.awt.Toolkit


const val BRICK_SIZE = 40.0

class MainViewImpl : View("Tetris"), MainView {

    private val bgColor = Color.BLACK

    val game: Game = Game()
    private val scoreProperty = SimpleIntegerProperty()
    private val levelProperty = SimpleIntegerProperty()
    override var score: Int by scoreProperty
    override var level: Int by levelProperty

    private var canvas: Canvas by singleAssign()

    private val keyDownListener: (KeyEvent) -> Unit = {
        //println("Pressed ${it.code}")
        when (it.code) {
            KeyCode.LEFT -> game.onLeftPressed()
            KeyCode.RIGHT -> game.onRightPressed()
            KeyCode.UP -> game.onUpPressed()
            KeyCode.DOWN -> game.onDownPressed()
            else -> {
            }
        }
    }

    override fun gameOver() {
        dialog {
            label("Game Over").style {
                fontSize = 32.px
            }
            hbox(alignment = Pos.CENTER_RIGHT) {
                paddingTop = 32
                button("OK").action {
                    close()
                }
            }
        }
    }

    override val root: Parent = vbox {
        setOnKeyPressed(keyDownListener)

        hbox(32, Pos.CENTER_LEFT) {
            paddingAll = 16
            button("Start Game").action {
                game.start(this@MainViewImpl)
            }
            label(levelProperty.stringBinding { "Level: $it" }) {
                useMaxWidth = true
            }
            label(scoreProperty.stringBinding { "Score: $it" }) {
                useMaxWidth = true
            }
        }
        stackpane {
            style {
                backgroundColor += bgColor
            }
            canvas = canvas(BRICK_SIZE * AREA_WIDTH, BRICK_SIZE * AREA_HEIGHT)
        }
    }

    suspend override fun wipeLines(lines: List<Int>) {
        val dpi = Math.rint(getDpi())
        println("dpi=$dpi")

        val gc = canvas.graphicsContext2D

        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        params.transform = Transform.scale(dpi, dpi)

        for ((offset, line) in lines.reversed().withIndex()) {
            println("line $line wiped")
            val height = BRICK_SIZE * (line + offset)
            val image = WritableImage(Math.rint(dpi * canvas.width).toInt(), Math.rint(dpi * height).toInt())
            canvas.snapshot(params, image)
            gc.clearRect(0.0, 0.0, canvas.width, height + BRICK_SIZE)
            gc.drawImage(image, 0.0, BRICK_SIZE, canvas.width, height)
        }
    }

    private fun getDpi(): Double {
        val trueHorizontalLines = Toolkit.getDefaultToolkit().screenSize.getHeight()
        val scaledHorizontalLines = Screen.getPrimary().bounds.height
        return trueHorizontalLines / scaledHorizontalLines
    }

    override fun drawBlockAt(x: Int, y: Int, color: Int) {
        val radius = 8.0
        val gap = 1
        with(canvas.graphicsContext2D) {
            fill = color.toColor()
            fillRoundRect(
                x * BRICK_SIZE + gap,
                y * BRICK_SIZE + gap,
                BRICK_SIZE - gap,
                BRICK_SIZE - gap,
                radius, radius
            )
        }
    }

    override fun clearBlockAt(x: Int, y: Int) {
        with(canvas.graphicsContext2D) {
            clearRect(
                x * BRICK_SIZE,
                y * BRICK_SIZE,
                BRICK_SIZE,
                BRICK_SIZE
            )
        }
    }

    override fun clearArea() {
        canvas.graphicsContext2D.clearRect(
            0.0,
            0.0,
            canvas.width,
            canvas.height
        )
    }
}