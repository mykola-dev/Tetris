package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class LFlippedFigure:BaseFigure() {
    override val matrix: BitMatrix = BitMatrix {
        +"001"
        +"001"
        +"011"
    }
}