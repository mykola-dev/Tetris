package ds.tetris.fx.view

import ds.tetris.coroutines.coroutineContext
import ds.tetris.fx.util.clearRect
import ds.tetris.fx.util.drawImage
import ds.tetris.fx.util.toColor
import ds.tetris.game.AREA_HEIGHT
import ds.tetris.game.AREA_WIDTH
import ds.tetris.game.Game
import ds.tetris.game.GameView
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.transform.Transform
import javafx.stage.Screen
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.awt.Toolkit

const val BRICK_SIZE = 40.0

class MainView : View("Tetris"), GameView {

    private val bgColor = Color.BLACK

    val game: Game = Game()
    private val scoreProperty = SimpleIntegerProperty()
    private val levelProperty = SimpleIntegerProperty()
    override var score: Int by scoreProperty
    override var level: Int by levelProperty

    private var canvas: Canvas by singleAssign()

    private val keyDownListener: (KeyEvent) -> Unit = {
        when (it.code) {
            KeyCode.LEFT -> game.onLeftPressed()
            KeyCode.RIGHT -> game.onRightPressed()
            KeyCode.UP -> game.onUpPressed()
            KeyCode.DOWN -> game.onDownPressed()
            else -> {
            }
        }
    }

    override val root: Parent = vbox {
        setOnKeyPressed(keyDownListener)

        hbox(32, Pos.CENTER_LEFT) {
            paddingAll = 16
            button("Start Game").action {
                game.start(this@MainView)
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


        // fade nicely
        val fadeColor = Color.rgb(0, 0, 0, 0.5)
        val iterations = 5
        repeat(iterations) {
            gc.fill = if (it == iterations - 1) bgColor else fadeColor
            for (line in lines) {
                gc.fillRect(0.0, line * BRICK_SIZE, canvas.width, BRICK_SIZE)
            }
            delay(50)
        }

        // animate nicely
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        params.transform = Transform.scale(dpi, dpi)

        var globalOffset = 0.0
        val animationJobs = mutableListOf<Job>()
        for (i in lines.size - 1 downTo 0) {
            val startline = if (i == 0) 0 else lines[i - 1] + 1
            val size = lines[i] - startline

            globalOffset += BRICK_SIZE
            val offset = globalOffset
            if (size > 0) {
                animationJobs += launch(coroutineContext()) {
                    val height = size * BRICK_SIZE
                    println("area start=$startline size=$size with offset $offset")
                    val image = WritableImage(Math.rint(dpi * canvas.width).toInt(), Math.rint(dpi * height).toInt())
                    var viewport = Rectangle2D(0.0, Math.rint(dpi *startline * BRICK_SIZE), canvas.width, height)
                    params.viewport = viewport
                    canvas.snapshot(params, image)
                    for (j in 0..offset.toInt() step (offset / 10).toInt()) {
                        gc.clearRect(viewport)
                        viewport = Rectangle2D(0.0, startline * BRICK_SIZE + j, canvas.width, height)
                        gc.drawImage(image, viewport)
                        delay(10)
                    }
                }
            }
        }

        animationJobs.forEach { it.join() }

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
}