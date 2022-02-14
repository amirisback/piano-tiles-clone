package com.github.jghjianghan.view.engines.classic

import android.graphics.Color
import com.github.jghjianghan.model.audio.PianoPlayer
import com.github.jghjianghan.view.UIThreadWrapper
import com.github.jghjianghan.view.engines.NormalTile
import com.github.jghjianghan.view.engines.TileDrawer
import com.github.jghjianghan.view.engines.TileOrchestrator
import java.util.*
import kotlin.collections.ArrayList

class ClassicTileOrchestrator(
    private val engine: ClassicGameEngine,
    private val handler: UIThreadWrapper,
    laneCenters: ArrayList<Float>,
    private val tileWidth: Float,
    private val envHeight: Float,
    private val totalLine: Int,
    pianoPlayer: PianoPlayer

): TileOrchestrator(laneCenters, 0f, pianoPlayer) {
    private val tiles = Vector<NormalTile>()
    private val linePerScreen = (envHeight / (1.8*tileWidth) + 0.5).toInt()
    private val tileHeight = envHeight / linePerScreen
    private val dropper = Dropper(7, 20)
    private val spawner = Spawner()
    private val laneHeightCenters = ArrayList<Float>() // index 0 di atas luar layar
    private var score = 0f
    private var tempScore = 0f
    private var tileClicked = 0
    private var startTime : Long = 0
    private var endTime : Long = 0
    private var startPauseTime : Long = 0
    private var stopPauseTime : Long = 0
    private var pauseFlag = false
    private var stopFlag = false
    private var isStarted = false



    init {
        var currHeight = -tileHeight
        for(i in 0..linePerScreen) {
            laneHeightCenters.add(currHeight)
            currHeight += tileHeight
        }
    }

    fun start() {
        println("orchestrator starting")
        spawner.start()
    }

    override fun getScore(): Number = score

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

        fun spawn(prefCy: Float) {
            if(normalTilesSpawned < totalLine) {
                val cx = laneCenters.random()
                tiles.add(
                    NormalTile(
                        tileWidth,
                        tileHeight,
                        cx,
                        prefCy - tileHeight,
                        envHeight,
                        Color.BLACK
                    )
                )
                normalTilesSpawned++
            }else {
                for (i in 0..3) {
                    tiles.add(
                        NormalTile(
                            tileWidth,
                            tileHeight,
                            laneCenters[i],
                            prefCy - tileHeight,
                            envHeight,
                            Color.GREEN,
                            false
                        )
                    )
                }
            }
        }
    }

    private inner class Dropper(
        private val iterationPerTile: Int,
        private val delay: Long
    ) : Thread() {
        var finishFlag = false

        private var totalIteration = 0
        private var iteration = 0
        private val step = tileHeight/iterationPerTile

        override fun run() {
            startTime = System.currentTimeMillis()
            var drawers = ArrayList<TileDrawer>()
            for (tile in tiles){
                drawers.add(tile.getDrawer())
            }
            while(!stopFlag) {
                if(!pauseFlag) {
                    endTime = System.currentTimeMillis()
                    score = 1f * (endTime - startTime) / 1000
                    if(iteration < totalIteration) {
                        drawers = ArrayList()
                        if(tiles[tiles.size - 1].cy + tileHeight >= 0) {
                            spawner.spawn(tiles[tiles.size-1].cy)
                        }
                        for (tile in tiles){
                            tile.drop(step)
                            drawers.add(tile.getDrawer())
                        }
                        iteration++
                    }else {
                        // reset to 0
                        totalIteration = 0
                        iteration = 0
                    }

                }
                handler.redrawCanvas(drawers)
                if(iteration == totalIteration && finishFlag) {
                    sleep(1000)
                    this@ClassicTileOrchestrator.stop()
                }
                sleep(delay)
            }
        }

        fun drop() {
            totalIteration += iterationPerTile
        }

    }


    fun pause(){
        pauseFlag = true
        startPauseTime = System.currentTimeMillis()
        tempScore = score
        score = Float.MAX_VALUE
    }

    fun resume(){
        stopPauseTime = System.currentTimeMillis()
        score = tempScore
        startTime += stopPauseTime-startPauseTime
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
                        }
                        pianoPlayer?.playNext()
                        tile.onClick()
                        dropper.drop()
                        tileClicked++
                        if(tileClicked == totalLine) {
                            handler.disablePause()
                            dropper.finishFlag = true
                            dropper.drop()
                        }
                    }
                    else if(y >= tile.cy && y <= tile.cy + tileHeight ){ // miss
                        pianoPlayer?.playWrong()
                        touchMissMechanism(x, tile.cy)
                        score = Float.MAX_VALUE
                    }
                    return
                }
            }
        }
    }

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