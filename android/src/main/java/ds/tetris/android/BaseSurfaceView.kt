/*
 * © 2017 Deviant Studio
 */

package ds.tetris.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import ds.tetris.game.log
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.newSingleThreadContext

@Suppress("LeakingThis")
abstract class BaseSurfaceView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs) {
    private val holderCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder?) {
            surfaceReady = true
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            surfaceReady = false
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    }

    protected var surfaceReady = false

    init {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(holderCallback)
    }

    private val surfaceActor = actor<Unit>(newSingleThreadContext(javaClass.simpleName), 2) {
        while (isActive) {
            receive()
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas(null)
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    onDraw(canvas)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        if (surfaceReady) surfaceActor.offer(Unit)
    }

    override fun postInvalidate() {
        super.postInvalidate()
        if (surfaceReady) surfaceActor.offer(Unit)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        log("onDetachedFromWindow")
        surfaceActor.cancel()
        holder.removeCallback(holderCallback)
    }
}