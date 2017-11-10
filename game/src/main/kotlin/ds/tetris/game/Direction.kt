/*
 * © 2017 Deviant Studio
 */

package ds.tetris.game

import ds.tetris.game.figures.Point

enum class Direction(val movement: Point) {
    UP(Point(0, -1)),
    DOWN(Point(0, 1)),
    LEFT(Point(-1, 0)),
    RIGHT(Point(1, 0))
}
