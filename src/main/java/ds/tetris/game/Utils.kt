package ds.tetris.game

val <T> Array<T>.random get() = this[(Math.random() * size).toInt()]