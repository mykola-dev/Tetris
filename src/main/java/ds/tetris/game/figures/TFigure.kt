package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class TFigure:BaseFigure() {
    override val matrix: BitMatrix = BitMatrix {
        +"010"
        +"111"
        +"000"
    }
}