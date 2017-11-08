package ds.tetris.fx.view

import ds.tetris.fx.util.toColor
import ds.tetris.game.NextFigureView
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import tornadofx.*

private const val BRICK_SIZE = 20.0

class NextFigure : View(), NextFigureView {

    private var canvas: Canvas by singleAssign()

    override fun drawBlockAt(x: Int, y: Int, color: Int) {
        val gap = 1.0
        val radius = 2.0
        with(canvas.graphicsContext2D) {
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
    }

    override fun clearArea() = canvas.graphicsContext2D.clearRect(0.0, 0.0, canvas.width, canvas.height)

    override val root: Parent = stackpane {
        maxWidth = 4 * BRICK_SIZE
        maxHeight = 4 * BRICK_SIZE
        canvas = canvas(maxWidth, maxHeight)
    }
}