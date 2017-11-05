package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class SquareFigure:BaseFigure() {
    override val matrix: BitMatrix = BitMatrix {
        +"11"
        +"11"
    }
}