package com.example.piano_tiles_kw.view.engines.tilt

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.example.piano_tiles_kw.model.audio.PianoPlayer
import com.example.piano_tiles_kw.view.SensorData
import com.example.piano_tiles_kw.view.UIThreadWrapper
import com.example.piano_tiles_kw.view.engines.TileDrawer
import com.example.piano_tiles_kw.view.engines.TileOrchestrator
import com.example.piano_tiles_kw.view.engines.raining.RainingTile
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class TIltTileOrchestrator(
    private val engine: TiltGameEngine,
    private val handler: UIThreadWrapper,
    laneCenters: ArrayList<Float>,
    tileWidth: Float,
    spawnDelay: Long,
    initSpeed: Float,
    envHeight: Float,
    val envWidth: Float,
    tileColor: Int,
    pianoPlayer: PianoPlayer,
    val sensorData: SensorData

) : TileOrchestrator(laneCenters, initSpeed, pianoPlayer) {
    private val tiles = CopyOnWriteArrayList<RainingTile>()
    private val spawner = Spawner(spawnDelay, tileWidth, 4f, envHeight, tileColor)
    private val puller = Puller()
    private val speeder = Speeder()
    var tileHeight = 1.8f * tileWidth
    private val missed = MissedAnimation(tileHeight, 10, 20)
    private var score = 0
    private var circleRadius = 80.0f
    private var circleX = envWidth / 2
    private var circleY = envHeight-circleRadius-15f
    private var pauseFlag = false
    private var stopFlag = false

    override fun getScore(): Number = score

    fun start() {
        println("orchestrator starting")
        spawner.start()
        puller.start()
        speeder.start()

    }

    private inner class Spawner(
        private var delay: Long,
        private val tileWidth: Float,
        private val speedDeviationFactor: Float,
        private val envHeight: Float,
        private val tileColor: Int
    ) : Thread() {
        private val speedUpValue: Float = delay * .08f
        private val minimumDelay = 300
        private val speedUpIteration = 15
        private var speedUpCounter = 0
        private var prevCenter = -1f;
        override fun run() {
            Log.d("missed", envHeight.toString())
            while (!stopFlag) {
                if(!pauseFlag) {
                    var tileSpeed = (ThreadLocalRandom.current()
                        .nextFloat() - 0.5f) * speedDeviationFactor + dropSpeed
                    Log.d("missed", envHeight.toString())
                    Log.d("missed dropSpeed", dropSpeed.toString())
                    Log.d("missed Tilespeed", tileSpeed.toString())
                    var center: Float = 0f
                    do {
                        center = laneCenters.random()
                    } while (center == prevCenter)
                    prevCenter = center
                    tiles.add(
                        RainingTile(
                            tileWidth,
                            tileHeight,
                            center,
                            envHeight,
                            tileSpeed,
                            tileColor
                        )
                    )

                    speedUpCounter++
                    sleep(delay)
                    Log.d("spawner", delay.toString())
                    if (speedUpCounter == speedUpIteration) {
                        speedUpCounter = 0
                        delay = if (delay - speedUpValue >= minimumDelay) {
                            (delay - speedUpValue).toLong()
                        } else minimumDelay.toLong()
                    }
                }

            }
        }
    }

    private inner class Puller : Thread() {
        private var delay: Long = 20
        var maxSpeed = 50f

        override fun run() {
            while (!stopFlag) {
                if(!pauseFlag) {
                    var speed = ((sensorData.sensorX / 9.81) * maxSpeed).toFloat()
                    //moving right
                    if (sensorData.sensorX < 0) {
                        if(circleX + circleRadius + 15f < envWidth){
                            circleX -= speed
                        } else {
                            circleX = envWidth-circleRadius
                        }
                    }
                    //moving left
                    else if (sensorData.sensorX > 0 ) {
                        if(circleX - circleRadius - 15f > 0){
                            circleX -= speed
                        } else {
                            circleX = circleRadius
                        }
                    }
                    val drawers = ArrayList<TileDrawer>()
                    val outTiles = ArrayList<RainingTile>()
                    val iter = tiles.iterator()
                    var missedTile = false
                    while (iter.hasNext()) {
                        val tile = iter.next()
                        tile.drop()

                        if (tile.isClickable && isCircleTouchTile(tile.cx, tile.cy + tileHeight/2, tile.width, tileHeight)) {
                            tile.onClick()
                            score++
                            pianoPlayer?.playNext()
                        }

                        drawers.add(tile.Drawer())
                        if (tile.isOut) {
                            if (!missedTile && tile.isClickable) {
                                Log.d("missed", tile.cx.toString() + " " + tile.cy.toString())
                                missedTile = true
                                tile.onMissed()
                            }
                            outTiles.add(tile)
                        }
                    }

                    if (missedTile) {
                        missedMechanism()
                        return
                    }

                    tiles.removeAll(outTiles)
                    drawers.add(CircleDrawer())
                    handler.redrawCanvas(drawers)
                    sleep(delay)
                    Log.d("sensorData", sensorData.sensorX.toString())
                }

            }
        }

        private fun isCircleTouchTile(cx: Float, cy: Float, tileWidth: Float, tileHeight: Float): Boolean {
            if (hypot(cx-tileWidth/2 - circleX, cy+tileHeight/2 - circleY) < circleRadius){
                return true
            }
            if (hypot(cx+tileWidth/2 - circleX, cy+tileHeight/2 - circleY) < circleRadius){
                return true
            }
            if (hypot(cx-tileWidth/2 - circleX, cy-tileHeight/2 - circleY) < circleRadius){
                return true
            }
            if (hypot(cx+tileWidth/2 - circleX, cy-tileHeight/2 - circleY) < circleRadius){
                return true
            }
            if (circleX > cx-tileWidth/2 && circleX < cx+tileWidth/2 && Math.abs(circleY-cy) < circleRadius+tileHeight/2){
                return true
            }

            if (circleY > cy-tileHeight/2 && circleY < cy+tileHeight/2 && Math.abs(circleX-cx) < circleRadius+tileWidth/2){
                return true
            }
            return false
        }

        private fun hypot(dx: Float, dy: Float): Float{
            return sqrt((dx*dx+dy*dy).toDouble()).toFloat()
        }
    }

    private inner class Speeder(
        private val interval: Long = 13000,
        private val speedUpValue: Float = dropSpeed * 0.3f, //default 0.12f
    ) : Thread() {
        override fun run() {
            Log.d("speedup", dropSpeed.toString())
            while (!stopFlag) {
                if(!pauseFlag) {
                    sleep(interval)
                    if(!pauseFlag) dropSpeed += speedUpValue
                }
            }
        }
    }

    private inner class MissedAnimation(
        totalDisplacement: Float,
        private val iteration: Int,
        private val delay: Long,
        private val postExecDelay: Long = 1000
    ) {
        private val step = totalDisplacement / iteration

        fun start() {
            println("step $step")
            for (i in 1..iteration) {
                val drawers = ArrayList<TileDrawer>()
                for (tile in tiles) {
                    tile.lift(step)
                    drawers.add(tile.getDrawer())
                }
                println("drawers: " + drawers.size)
                drawers.add(CircleDrawer())
                handler.redrawCanvas(drawers)
                Thread.sleep(delay)
            }
            Thread.sleep(postExecDelay)
        }
    }

    private inner class CircleDrawer : TileDrawer() {
        var color = Color.GREEN
        val paint = Paint()

        init {
            paint.color = color
        }

        override fun drawTile(canvas: Canvas) {
            canvas.drawCircle(circleX, circleY, circleRadius, paint)
        }
    }

    private fun missedMechanism() {
        Log.d("missed", "missedStarting")
        internalStop()
        pianoPlayer?.playWrong()
        missed.start()
        stop()
    }

    fun pause() {
        pauseFlag = true
    }

    fun resume() {
        pauseFlag = false
    }

    override fun handleTouch(x: Float, y: Float) {

    }

    private fun internalStop() {
        handler.disablePause()
        stopFlag = true
    }

    fun stop() {
        internalStop()
        println("stopping orchestrator")
        if (!engine.isOver) {
            handler.stopGame()
        }
    }

}