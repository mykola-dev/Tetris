package ds.tetris.fx.util

import javafx.geometry.Rectangle2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image

fun GraphicsContext.drawImage(img: Image, rect: Rectangle2D) = drawImage(img, rect.minX, rect.minY, rect.width, rect.height)
fun GraphicsContext.clearRect(rect: Rectangle2D) = clearRect(rect.minX, rect.minY, rect.width, rect.height)