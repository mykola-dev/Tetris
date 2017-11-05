package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class SFlippedFigure:BaseFigure(){
    override val matrix: BitMatrix = BitMatrix {
        +"110"
        +"011"
        +"000"
    }
}