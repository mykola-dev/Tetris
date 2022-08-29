/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.unit.IntSize
import ds.tetris.game.figures.BoardMatrix
import ds.tetris.game.figures.Brick
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.Matrix

data class GameState(
    val bricks: List<Brick> = emptyList(),
    val next: List<Brick> = emptyList(),
    val score: Int = 0,
    val level: Int = 0,
    val wipedLines: List<Int> = emptyList(),
    val soundEnabled: Boolean = true,
    val state: State = State.STOPPED,
    val areaDimensions: IntSize = IntSize(AREA_WIDTH, AREA_HEIGHT)
) {
    enum class State {
        STOPPED, STARTED, PAUSED, GAME_OVER
    }
}


/**
 * This class represents the game area state and helper methods to manipulate with state
 */
internal class Board(boardWidth: Int = AREA_WIDTH, boardHeight: Int = AREA_HEIGHT) {

    val matrix: Matrix<PaintStyle?> = BoardMatrix(boardWidth, boardHeight)


    fun bakeFigure(figure: Figure) {
        figure.points.forEach {
            matrix[it] = PaintStyle.Fill(figure.color)
        }
    }

    //lateinit var currentFigure: IFigure

    /*  fun drawFigure(figure:IFigure) = with(figure) {
          if (ghost == null) {
              // find ghost
              for (i in 0 until AREA_HEIGHT - position.y) {
                  val movement = Point(0, i)
                  if (!canMove(movement, this)) {
                      ghost = clone()
                      ghost!!.position = Point(position.x, position.y + i - 1)
                      break
                  }
              }
          }

          ghost?.points?.forEach {
              view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.STROKE)
          }

          points.forEach {
              view.drawBlockAt(it.x, it.y, currentFigure.color, PaintStyle.FILL)
          }
          view.invalidate()
      }*/

    /*  private fun clearFigure(figure: IFigure) {
          figure
              .points
              .forEach {
                  view.clearBlockAt(it.x, it.y)

              }
          figure
              .ghost
              ?.points
              ?.forEach {
                  view.clearBlockAt(it.x, it.y)
              }
      }*/

    /**
     * @return true if success
     */
    /* fun moveFigure(movement: Point, figure: IFigure = currentFigure): Boolean {
         canMove(movement, figure) || return false
         clearFigure(figure)

         // invalidate ghost
         if (movement.x != 0) figure.ghost = null

         figure.position += movement
         drawFigure()
         return true
     }*/

    /*  private fun canMove(movement: Point, figure: IFigure): Boolean = figure
          .points
          .all {
              val nextPoint = it + movement
              !nextPoint.outOfArea() && !area[nextPoint]
          }*/

    /*  private fun Point.outOfArea(): Boolean = x >= AREA_WIDTH || x < 0 || y >= AREA_HEIGHT || y < 0

      fun rotateFigure() {
          val newFigure = currentFigure.clone().apply {
              rotate()

              // edge cases
              while (!points.all { it.x >= 0 }) {
                  position += RIGHT.movement
              }
              while (!points.all { it.x < AREA_WIDTH }) {
                  position += LEFT.movement
              }
              while (!points.all { it.y < AREA_HEIGHT }) {
                  position += UP.movement
              }

              // try to fix unexpected collisions
              if (!points.all { !area[it] }) {
                  if (canMove(RIGHT.movement, this))
                      position += RIGHT.movement
                  else if (canMove(LEFT.movement, this))
                      position += LEFT.movement
              }

          }

          if (newFigure.points.all { !area[it] }) {
              clearFigure(currentFigure)
              currentFigure = newFigure
              currentFigure.ghost = null
              drawFigure()
          }
      }

      fun fixFigure() {
          currentFigure.points.forEach {
              area[it] = true
          }
      }

      fun getFilledLinesIndices(): List<Int> = area.array
          .mapIndexed { i, row -> i to row }
          .filter { it.second.all { it } }
          .map { it.first }

      fun wipeLines(lines: List<Int>) {
          val cropped = area.array.filter { !it.all { it } }.toMutableList()
          repeat(lines.size) {
              cropped.add(0, Array(AREA_WIDTH) { false })
          }
          area = BitMatrix(cropped.toTypedArray())
      }

      fun startingPosition(figure: IFigure) = Point((AREA_WIDTH - figure.matrix.width) / 2, 0)*/
}