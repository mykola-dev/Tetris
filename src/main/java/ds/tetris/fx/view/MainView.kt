package ds.tetris.fx.view

import ds.tetris.coroutines.coroutineContext
import ds.tetris.fx.util.clearRect
import ds.tetris.fx.util.drawImage
import ds.tetris.fx.util.toColor
import ds.tetris.game.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
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
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.awt.Toolkit

private const val BRICK_SIZE = 40.0

class MainView : View("Tetris"), GameView {

    private val bgColor = Color.BLACK

    private val nextFigureView: NextFigure by inject()

    var game: Game? = null

    private val scoreProperty = SimpleIntegerProperty()
    private val levelProperty = SimpleIntegerProperty()
    private val pauseTitleProperty = SimpleStringProperty("Pause")
    private val startTitleProperty = SimpleStringProperty("Start")
    override var score: Int by scoreProperty
    override var level: Int by levelProperty

    private var canvas: Canvas by singleAssign()

    private val keyDownListener: (KeyEvent) -> Unit = {
        when (it.code) {
            KeyCode.LEFT -> game?.onLeftPressed()
            KeyCode.RIGHT -> game?.onRightPressed()
            KeyCode.UP -> game?.onUpPressed()
            KeyCode.DOWN -> game?.onDownPressed()
            else -> {
            }
        }
    }
    private val keyUpListener: (KeyEvent) -> Unit = {
        when (it.code) {
            KeyCode.DOWN -> game?.onDownReleased()
            KeyCode.LEFT -> game?.onLeftReleased()
            KeyCode.RIGHT -> game?.onRightReleased()
            else -> {
            }
        }
    }

    override val root: Parent = hbox {
        setOnKeyPressed(keyDownListener)
        setOnKeyReleased(keyUpListener)

        stackpane {
            style {
                backgroundColor += bgColor
            }
            canvas = canvas(BRICK_SIZE * AREA_WIDTH, BRICK_SIZE * AREA_HEIGHT)
        }

        vbox(32, Pos.TOP_CENTER) {
            paddingAll = 16
            minWidth = 200.0

            add(nextFigureView)

            this += button(startTitleProperty) {
                action {
                    game?.stop()
                    game = Game(this@MainView, nextFigureView, JavaFx)
                    game?.start()
                    startTitleProperty.set("Restart")
                    pauseTitleProperty.set("Pause")
                }
            }
            button(pauseTitleProperty) {
                isFocusTraversable = false
                action {
                    game?.pause()
                    if (game?.isPaused == true)
                        pauseTitleProperty.set("Resume")
                    else
                        pauseTitleProperty.set("Pause")
                }
            }

            label(levelProperty.stringBinding { "Level: $it" }).style { textFill = Color.BURLYWOOD }
            label(scoreProperty.stringBinding { "Score: $it" }).style { textFill = Color.CADETBLUE }
            style {
                fontSize = 20.px
                backgroundColor += Color.valueOf("#101010")
            }
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
                    var viewport = Rectangle2D(0.0, Math.rint(dpi * startline * BRICK_SIZE), canvas.width, height)
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

    override fun drawBlockAt(x: Int, y: Int, color: Int, style: PaintStyle) {
        val radius = 8.0
        val gap = 2
        with(canvas.graphicsContext2D) {
            when (style) {
                PaintStyle.FILL -> {
                    fill = color.toColor()
                    stroke = Color.TRANSPARENT
                    fillRoundRect(
                        x * BRICK_SIZE + gap,
                        y * BRICK_SIZE + gap,
                        BRICK_SIZE - gap * 2,
                        BRICK_SIZE - gap * 2,
                        radius, radius
                    )
                }
                PaintStyle.STROKE -> {
                    stroke = color.toColor().darker()
                    fill = Color.TRANSPARENT
                    strokeRoundRect(
                        x * BRICK_SIZE + gap,
                        y * BRICK_SIZE + gap,
                        BRICK_SIZE - gap * 2,
                        BRICK_SIZE - gap * 2,
                        radius, radius
                    )
                }
            }
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
                button("OK") {
                    minWidth = 100.0
                    action {
                        close()
                    }

                }
            }
        }
    }
}