package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class IFigure : BaseFigure() {
    override val matrix: BitMatrix = BitMatrix {
        +"0100"
        +"0100"
        +"0100"
        +"0100"
    }
}