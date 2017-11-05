package ds.tetris.game.figures

import ds.tetris.game.BitMatrix

class SFigure:BaseFigure(){
    override val matrix: BitMatrix = BitMatrix {
        +"011"
        +"110"
        +"000"
    }
}