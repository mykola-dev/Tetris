/*
 * © 2017-2022 Deviant Studio
 */

package ds.tetris.game

import androidx.compose.ui.unit.IntOffset
import ds.tetris.game.Direction.*
import ds.tetris.game.figures.Figure
import ds.tetris.game.figures.FigureFactory
import ds.tetris.game.job.KeyCoroutine
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.random.Random

private const val BASE_DELAY = 800L

class Game(
    private val soundtrack: Soundtrack,
    private val log: Napier,
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    //override val coroutineContext: CoroutineContext = SupervisorJob()

    private val state = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = state

    private val board: Board = Board()

    //var area: BitMatrix = BitMatrix.createArea(AREA_WIDTH, AREA_HEIGHT)

    private lateinit var currentFigure: Figure
    private lateinit var nextFigure: Figure

    private val score: Score = Score {
        val currLevel = state.value.level
        if (currLevel < level && currLevel != 0 && state.value.soundEnabled) {
            soundtrack.play(Sound.LEVEL_UP)
        }

        state.update {
            it.copy(
                score = points,
                level = level
            )
        }
    }

    private val soundEnabled: Boolean get() = state.value.soundEnabled


    private val isStarted: Boolean get() = state.value.state == GameState.State.STARTED
    private val isPaused: Boolean get() = state.value.state == GameState.State.PAUSED

    private var downKeyCoroutine: KeyCoroutine = KeyCoroutine(this, 30) {
        score.awardSpeedUp()
        //fallActor.offer(Unit)
    }
    private var leftKeyCoroutine: KeyCoroutine = KeyCoroutine(this) {
        //board.moveFigure(LEFT.movement)
    }
    private var rightKeyCoroutine: KeyCoroutine = KeyCoroutine(this) {
        //board.moveFigure(RIGHT.movement)
    }

/*    private val fallFlow = MutableSharedFlow<Unit>() {

    }*/

    private val keysChannel: ReceiveChannel<Direction> = Channel()


    private val fallActor = launch {
        nextFigure = randomFigure(false)
        while (isActive) {
            var falling = true

            currentFigure = nextFigure
            currentFigure.moveToStart()
            nextFigure = randomFigure(false)

            while (falling) {
                log.v("tick")
                falling = select {
                    keysChannel.onReceive {
                        currentFigure.moveFigure(DOWN.movement)
                    }
                    onTimeout(calculateDelay()) {
                        currentFigure.moveFigure(DOWN.movement)
                    }
                }
            }
        }

        /* actor<Unit>(uiContextProvider()) {
             log("actor started")
             while (isActive) {
                 var falling = true
                 nextFigure = randomFigure(false)
                 drawPreview()
                 board.drawFigure()
                 while (falling) {
                     falling = select {
                         onReceive {
                             board.moveFigure(DOWN.movement)
                         }
                         onTimeout(calculateDelay()) {
                             board.moveFigure(DOWN.movement)
                         }
                     }
                 }
                 downKeyCoroutine.stop()
                 if (gameOver()) {
                     isStarted = false
                     if (soundEnabled) soundtrack.play(Sound.GAME_OVER)
                     coroutineContext.cancel()
                 } else {
                     board.fixFigure()

                     val lines = board.getFilledLinesIndices()
                     if (lines.isNotEmpty()) {
                         if (soundEnabled) soundtrack.play(Sound.WIPE, lines.size)
                         board.wipeLines(lines)
                         view.wipeLines(lines)
                         score.awardLinesWipe(lines.size)
                     }
                     nextFigure.position = board.startingPosition(nextFigure)
                     board.currentFigure = nextFigure
                 }
             }
             view.gameOver()
             log("actor stopped")
         }*/
    }

    fun onResume() {
        when (state.value.state) {
            GameState.State.PAUSED -> {
                state.update { it.copy(state = GameState.State.STARTED) }
            }
            GameState.State.STOPPED -> {

                state.update { it.copy(state = GameState.State.STARTED) }
            }
            GameState.State.STARTED -> {
                state.update { it.copy(state = GameState.State.PAUSED) }
            }
        }
    }

    fun onReset() {

    }

/*    fun start() {
        if (isStarted) error("Can't start twice")

        //view.clearArea()

        score.awardStart()

        board.currentFigure = *//*IFigure()*//*randomFigure()
        //sceneFiller(board, view)
        fallActor.offer(Unit)

        if (soundEnabled) soundtrack.play(Sound.START)
    }*/

    /*   fun stop() {
           stopper.cancel()
       }*/

    /* fun pause() {
         fallActor.isActive || return
         if (soundEnabled) soundtrack.play(Sound.ROTATE)
         isPaused = !isPaused
         if (!isPaused)
             fallActor.offer(Unit)
     }*/

    /*   private fun drawPreview() {
           view.clearPreviewArea()
           nextFigure.points.forEach {
               view.drawPreviewBlockAt(it.x, it.y, nextFigure.color)
           }
           view.invalidate()
       }*/

    private fun calculateDelay(): Long = if (!isPaused) {
        (BASE_DELAY - score.level * 50).coerceAtLeast(1)
    } else {
        INFINITY
    }

    private fun gameOver(): Boolean = currentFigure.offset.y <= 0

    private fun randomFigure(bringToStart: Boolean = true): Figure {
        val figure: Figure = FigureFactory.create()
        if (bringToStart) {
            //figure.offset = startingPosition(figure)
        }
        return figure
    }

    //fun getTopBrickLine() = board.matrix.array.indexOfFirst { !it.all { !it } }

    fun onLeftPressed() {
        log.v("left pressed")
        if (isStarted) leftKeyCoroutine.start()
        playMoveSound()
    }

    fun onRightPressed() {
        if (isStarted) rightKeyCoroutine.start()
        playMoveSound()
    }

    fun onDownPressed() {
        if (isStarted) downKeyCoroutine.start()
        playMoveSound()
    }

    fun onUpPressed() {
        if (isStarted) rotateFigure()
        if (soundEnabled) soundtrack.play(Sound.ROTATE)
    }

    fun onDownReleased() = downKeyCoroutine.stop()
    fun onLeftReleased() {
        log.v("left released")
        leftKeyCoroutine.stop()
    }
    fun onRightReleased() = rightKeyCoroutine.stop()

    private fun playMoveSound() {
        if (soundEnabled) soundtrack.play(Sound.MOVE, (Random.nextInt(4) + 1).toInt())
    }

    private fun Figure.moveFigure(movement: IntOffset): Boolean {
        /* canMove(movement, figure) || return false
         clearFigure(figure)

         // invalidate ghost
         if (movement.x != 0) figure.ghost = null

         figure.position += movement
         drawFigure()*/
        return true
    }

    private fun canMove(movement: IntOffset, figure: Figure): Boolean = figure
        .points
        .all {
            val nextPoint = it + movement
            !nextPoint.outOfArea() && board.matrix[nextPoint] == null
        }

    private fun IntOffset.outOfArea(): Boolean = x >= AREA_WIDTH || x < 0 || y >= AREA_HEIGHT || y < 0

    private fun Figure.moveToStart() {
        this.offset = IntOffset((AREA_WIDTH - this.matrix.width) / 2, 0)
    }

    private fun rotateFigure() = with(currentFigure) {
        rotate()

        // edge cases
        while (!points.all { it.x >= 0 }) {
            offset += RIGHT.movement
        }
        while (!points.all { it.x < AREA_WIDTH }) {
            offset += LEFT.movement
        }
        while (!points.all { it.y < AREA_HEIGHT }) {
            offset += UP.movement
        }

        // try to fix unexpected collisions
        if (!points.all { board.matrix[it] == null }) {
            if (canMove(RIGHT.movement, this))
                offset += RIGHT.movement
            else if (canMove(LEFT.movement, this))
                offset += LEFT.movement
        }


        /* if (points.all { !area[it] }) {
             clearFigure(currentFigure)
             currentFigure = newFigure
             currentFigure.ghost = null
             drawFigure()
         }*/
    }

    private fun draw() {
        //val bricks =
    }
}