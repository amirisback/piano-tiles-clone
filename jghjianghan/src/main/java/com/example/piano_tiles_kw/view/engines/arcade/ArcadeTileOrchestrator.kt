package com.example.piano_tiles_kw.view.engines.arcade

import android.graphics.Color
import android.util.Log
import com.example.piano_tiles_kw.model.audio.PianoPlayer
import com.example.piano_tiles_kw.view.UIThreadWrapper
import com.example.piano_tiles_kw.view.engines.NormalTile
import com.example.piano_tiles_kw.view.engines.TileDrawer
import com.example.piano_tiles_kw.view.engines.TileOrchestrator
import com.example.piano_tiles_kw.view.engines.raining.RainingTile
import java.util.*
import kotlin.collections.ArrayList

class ArcadeTileOrchestrator(
    private val engine: ArcadeGameEngine,
    private val handler: UIThreadWrapper,
    laneCenters: ArrayList<Float>,
    private val tileWidth: Float,
    private val envHeight: Float,
    pianoPlayer: PianoPlayer

): TileOrchestrator(laneCenters, 0f, pianoPlayer) {
    private val tiles = Vector<NormalTile>()
    private val linePerScreen = (envHeight / (1.8*tileWidth) + 0.5).toInt()
    private val tileHeight = envHeight / linePerScreen
    private val dropper = Dropper(20)
    private val spawner = Spawner()
    private val laneHeightCenters = ArrayList<Float>() // index 0 di atas luar layar
    private var score : Int = 0
    private val missed = MissedAnimation(tileHeight, 10, 20)
    private var isStarted = false
    private var step = tileHeight/15
    private val speeder = Speeder()
    private var pauseFlag = false
    private var stopFlag = false

    init {
        var currHeight = -tileHeight
        for(i in 0..linePerScreen) {
            laneHeightCenters.add(currHeight)
            currHeight += tileHeight
        }

        println("tileHeight : "  + tileHeight)
    }

    fun start() {
        println("orchestrator starting")
        spawner.start()
    }

    private inner class Spawner {
        private var normalTilesSpawned = 0 // Black Tiles

        fun start() {

            // spawn yelow starting tiles
            for (i in 0..3) {
                tiles.add(
                    NormalTile(
                        tileWidth,
                        tileHeight,
                        laneCenters[i],
                        laneHeightCenters[linePerScreen],
                        envHeight,
                        Color.YELLOW,
                        false
                    )
                )
            }

            // spawn initial black tiles
            for (i in linePerScreen-1 downTo 1) {
                val cx = laneCenters.random()
                tiles.add(
                    NormalTile(
                        tileWidth,
                        tileHeight,
                        cx,
                        laneHeightCenters[i],
                        envHeight,
                        Color.BLACK
                    )
                )
                normalTilesSpawned++
            }

            val drawers = ArrayList<TileDrawer>()
            for (tile in tiles){
                drawers.add(tile.getDrawer())
            }
            handler.redrawCanvas(drawers)
        }

        fun spawn(prefCy : Float) {
            val cx = laneCenters.random()
            tiles.add(
                NormalTile(
                    tileWidth,
                    tileHeight,
                    cx,
                    prefCy-tileHeight,
                    envHeight,
                    Color.BLACK
                )
            )
        }
    }

    private inner class Dropper(
        private val delay: Long
    ) : Thread() {
        override fun run() {
            while(!stopFlag) {
                if(!pauseFlag) {
                    val drawers = ArrayList<TileDrawer>()
                    val outTiles = ArrayList<NormalTile>()
                    var missedTile = false
                    if(tiles[tiles.size - 1].cy+tileHeight >= 0) {
                        spawner.spawn(tiles[tiles.size-1].cy)
                    }
                    for (tile in tiles){
                        tile.drop(step)
                        drawers.add(tile.getDrawer())
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
                }

                sleep(delay)
            }
        }
    }

    private inner class Speeder(
        private val interval: Long = 10000,
        private val speedIncrement: Float = 1.2f
    ): Thread() {
        override fun run() {

            while (!stopFlag){
                if(!pauseFlag) {
                    sleep(interval)
                    if(!pauseFlag) step += speedIncrement
                }
            }
        }
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
            while (iter.hasNext()){
                val tile = iter.next()
                if(tile.isClickable) {
                    if (tile.isTileTouched(x, y)){
                        if(!isStarted) {
                            isStarted = true
                            dropper.start()
                            speeder.start()
                        }
                        score++
                        pianoPlayer?.playNext()
                        tile.onClick()
                    }
                    else if(y >= tile.cy && y <= tile.cy + tileHeight ){ // miss
                        pianoPlayer?.playWrong()
                        touchMissMechanism(x, tile.cy)
                    }
                    return
                }
            }

        }
    }

    override fun getScore(): Number = score

    private fun touchMissMechanism(x: Float, yc: Float) {
        internalStop()
        val drawers = ArrayList<TileDrawer>()

        var xc = 0f
        for(xcenter in laneCenters) {
            if(x >= xcenter - tileWidth/2 && x <= xcenter + tileWidth/2) {
                xc = xcenter
            }
        }

        tiles.add(NormalTile(tileWidth, tileHeight, xc, yc, envHeight, Color.RED, false))

        val iter2 = tiles.iterator()
        while (iter2.hasNext()){
            val tile = iter2.next()
            drawers.add(tile.Drawer())
        }

        handler.redrawCanvas(drawers)
        Thread {
            Thread.sleep(1000)
            stop()
        }.start()
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