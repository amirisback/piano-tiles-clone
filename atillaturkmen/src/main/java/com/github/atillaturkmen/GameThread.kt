package com.github.atillaturkmen

import android.graphics.Canvas
import android.view.SurfaceHolder


class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    private var running: Boolean = false

    private val targetFPS = 60 // frames per second, the rate at which you would like to refresh the Canvas

    fun setRunning(isRunning: Boolean) {
        this.running = isRunning
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetTime = (1000 / targetFPS).toLong()
        val firstTime = System.nanoTime()

        while (true) {
            if (running) {
                startTime = System.nanoTime()
                canvas = null

                // half a second delay before game starts
                if (System.nanoTime() - firstTime < 500000000) {
                    canvas = this.surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        this.gameView.drawLines(canvas!!)
                        this.gameView.drawScore(canvas!!)
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas)
                    continue
                }

                try {
                    // locking the canvas allows us to draw on to it
                    canvas = this.surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        this.gameView.draw(canvas!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                timeMillis = (System.nanoTime() - startTime) / 1000000
                waitTime = targetTime - timeMillis
                if (waitTime < 0) {
                    waitTime = 0
                }

                try {
                    sleep(waitTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        private var canvas: Canvas? = null
    }
}