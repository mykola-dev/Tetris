package ds.tetris.game.figures

class IFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"0100"
        +"0100"
        +"0100"
        +"0100"
    }
}

class LFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"100"
        +"100"
        +"110"
    }
}

class LFlippedFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"001"
        +"001"
        +"011"
    }
}

class SFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"011"
        +"110"
        +"000"
    }
}

class SFlippedFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"110"
        +"011"
        +"000"
    }
}

class SquareFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"11"
        +"11"
    }
}

class TFigure : BaseFigure() {
    override var matrix: BitMatrix = BitMatrix {
        +"010"
        +"111"
        +"000"
    }
}