package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class LFigure:BaseFigure() {
    override val matrix: BitMatrix = BitMatrix {
        +"100"
        +"100"
        +"110"
    }
}