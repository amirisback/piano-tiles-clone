package com.example.piano_tiles_kw.view.engines.raining

import android.util.Log
import com.example.piano_tiles_kw.model.audio.PianoPlayer
import com.example.piano_tiles_kw.view.UIThreadWrapper
import com.example.piano_tiles_kw.view.engines.CircularFalseMark
import com.example.piano_tiles_kw.view.engines.NormalTile
import com.example.piano_tiles_kw.view.engines.TileDrawer
import com.example.piano_tiles_kw.view.engines.TileOrchestrator
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class RainingTileOrchestrator(
    private val engine: RainingGameEngine,
    private val handler: UIThreadWrapper,
    laneCenters : ArrayList<Float>,
    tileWidth: Float,
    spawnDelay: Long,
    initSpeed: Float,
    envHeight: Float,
    tileColor: Int,
    pianoPlayer: PianoPlayer
): TileOrchestrator(laneCenters, initSpeed, pianoPlayer) {
    private val tiles = CopyOnWriteArrayList<RainingTile>()
    private val spawner = Spawner(spawnDelay, tileWidth, 4f, envHeight, tileColor)
    private val puller = Puller()
    private val speeder = Speeder()
    var tileHeight = 1.8f * tileWidth
    private val missed = MissedAnimation(tileHeight, 10, 20)
    private var score = 0
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
    ): Thread() {
        private val speedUpValue: Float = delay * 0.3f
        private val minimumDelay = 300
        private val speedUpIteration = 15
        private var speedUpCounter = 0
        private var prevCenter = -1f;
        override fun run() {
            Log.d("missed", envHeight.toString())
            while (!stopFlag){
                if(!pauseFlag) {
                    var tileSpeed = (ThreadLocalRandom.current().nextFloat() - 0.5f) * speedDeviationFactor + dropSpeed
                    Log.d("missed", envHeight.toString())
                    Log.d("missed dropSpeed", dropSpeed.toString())
                    Log.d("missed Tilespeed", tileSpeed.toString())
                    var center: Float = 0f
                    do {
                        center = laneCenters.random()
                    } while (center == prevCenter)
                    prevCenter = center
                    tiles.add(RainingTile(tileWidth, tileHeight, center, envHeight, tileSpeed, tileColor))
                    if (ThreadLocalRandom.current().nextDouble() < 0.1){//double spawn
                        do {
                            center = laneCenters.random()
                        } while (center == prevCenter)
                        prevCenter = center
                        tileSpeed = (ThreadLocalRandom.current().nextFloat() - 0.5f) * speedDeviationFactor + dropSpeed
                        tiles.add(RainingTile(tileWidth, tileHeight, center, envHeight, tileSpeed, tileColor))
                    }
                    speedUpCounter++
                    sleep(delay)
                    Log.d("spawner", delay.toString())
                    if (speedUpCounter == speedUpIteration){
                        speedUpCounter = 0
                        delay = if (delay-speedUpValue >= minimumDelay){
                            (delay-speedUpValue).toLong()
                        } else minimumDelay.toLong()
                    }
                }
            }
        }
    }
    private inner class Puller: Thread() {
        private var delay: Long = 20
        override fun run() {
            while (!stopFlag){
                if(!pauseFlag) {
                    val drawers = ArrayList<TileDrawer>()
                    val outTiles = ArrayList<RainingTile>()
                    val iter = tiles.iterator()
                    var missedTile = false
                    while (iter.hasNext()){
                        val tile = iter.next()
                        tile.drop()
                        drawers.add(tile.Drawer())
                        if (tile.isOut){
                            if (!missedTile && tile.isClickable){
                                Log.d("missed",tile.cx.toString() + " " + tile.cy.toString())
                                missedTile = true
                                tile.onMissed()
                            }
                            outTiles.add(tile)
                        }
                    }

                    if (missedTile){
                        missedMechanism()
                        return
                    }
                    tiles.removeAll(outTiles)
                    handler.redrawCanvas(drawers)
                    sleep(delay)
                }
            }
        }
    }

    private inner class Speeder(
        private val interval: Long = 13000,
        private val speedUpValue: Float = dropSpeed * 0.12f,
    ): Thread() {
        override fun run() {
            Log.d("speedup", dropSpeed.toString())
            while (!stopFlag){
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
        private val step = totalDisplacement/iteration

        fun start() {
            println("step $step")
            for (i in 1..iteration){
                val drawers = ArrayList<TileDrawer>()
                for (tile in tiles){
                    tile.lift(step)
                    drawers.add(tile.getDrawer())
                }
                println("drawers: " + drawers.size)
                handler.redrawCanvas(drawers)
                Thread.sleep(delay)
            }
            Thread.sleep(postExecDelay)
        }
    }
    private fun missedMechanism(){
        Log.d("missed","missedStarting")
        internalStop()
        pianoPlayer?.playWrong()
        missed.start()
        stop()
    }

    fun pause(){
        pauseFlag = true
    }

    fun resume(){
        pauseFlag = false
    }

    override fun handleTouch(x: Float, y: Float) {
        if (!stopFlag && !pauseFlag){
            val iter = tiles.iterator()
            var onTileTouch = false
            while (iter.hasNext()){
                val tile = iter.next()
                if (tile.isTileTouched(x, y)){
                    onTileTouch = true
                    if (tile.isClickable){
                        tile.onClick()
                        score++
                        pianoPlayer?.playNext()
                        return
                    }
                }
            }
            if (!onTileTouch){
                //miss
                pianoPlayer?.playWrong()
                touchMissMechanism(x, y)
            }
        }

    }

    private fun touchMissMechanism(x: Float, y: Float) {
        internalStop()
        val drawers = ArrayList<TileDrawer>()
        val iter2 = tiles.iterator()
        while (iter2.hasNext()){
            val tile = iter2.next()
            drawers.add(tile.Drawer())
        }
        drawers.add(CircularFalseMark(x, y, 40f).Drawer())
        handler.redrawCanvas(drawers)
        Thread {
            Thread.sleep(1000)
            stop()
        }.start()
    }

    private fun internalStop(){
        handler.disablePause()
        stopFlag = true
    }
    fun stop(){
        internalStop()
        println("stopping orchestrator")
        if (!engine.isOver){
            handler.stopGame()
        }
    }
}