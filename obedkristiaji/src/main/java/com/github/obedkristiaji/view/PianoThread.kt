package com.github.obedkristiaji.view

import android.graphics.Rect
import com.github.obedkristiaji.model.Piano

class PianoThread(private val handler: ThreadHandler, private val canvas: Pair<Int, Int>, private val level: Int) : Runnable {
    private var thread: Thread = Thread(this)
    private var start: Boolean = false
    private var initiate: Boolean = false
    private var piano: Piano = Piano()
    private var fill: Boolean = true
    private var exc = 0
    private var score = 0

    override fun run() {
        try {
            while(start) {
                Thread.sleep((14/level.toLong()))

                for((i, tile) in piano.tiles.withIndex()) {
                    var j = 0
                    for(rect in piano.tiles[i].tile) {
                        val newPos = this.setPos(rect)
                        this.piano.tiles[i].setRect(j, newPos)

                        if(rect.top < -20) {
                            this.fill = false
                            break
                        }

                        if(rect.top > -20 && rect.top <= 0) {
                            this.fill = true
                            this.exc = i
                        }

                        if(newPos.top > this.canvas.second) {
                            this.start = false;
                            this.handler.lose(this.score)
                            this.handler.stopThread()
                            break
                        }

                        j++
                    }
                }

                this.handler.setRect(Pair(this.piano, this.score))

                if(this.fill || (piano.tiles[0].tile.isEmpty() && piano.tiles[1].tile.isEmpty() && piano.tiles[2].tile.isEmpty() && piano.tiles[3].tile.isEmpty())) {
                    this.randomTiles(exc)
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun initiate() {
        for(i in 1..this.piano.size) {
            this.piano.tiles[i-1].addTile(Rect(((((i-1)/4f)*this.canvas.first)).toInt(), ((((i-1)/4f)*this.canvas.second)).toInt(), (((i/4f)*this.canvas.first)).toInt(), (((i/4f)*this.canvas.second)).toInt()))
        }
        this.randomTiles(exc)
    }

    fun start() {
        if(!start) {
            this.start = true
            this.thread.start()
        }

        if(!initiate) {
            this.initiate()
        }
    }

    fun stop() {
        this.start = false
        this.handler.setPos(Pair(this.piano, this.score))
        this.handler.stopThread()
    }

    fun setPiano(piano: Piano, score: Int) {
        this.piano = piano
        this.score = score
        this.initiate = true
    }

    private fun setPos(rect: Rect): Rect {
        val plus = (((1/80f)*this.canvas.second)).toInt()
        val rect = Rect(rect.left, rect.top+plus, rect.right, rect.bottom+plus)
        return rect
    }

    fun randomTiles(exc: Int) {
        var pos = (1..this.piano.size).random()
        while((pos-1) == exc) {
            pos = (1..this.piano.size).random()
        }
        val plus = (((1/4f)*this.canvas.second)).toInt()
        this.piano.tiles[pos-1].addTile(Rect(((((pos-1)/4f)*this.canvas.first)).toInt(), -plus, (((pos/4f)*this.canvas.first)).toInt(), 0))
        this.fill = false
    }

    fun deleteTile(pos: Int, index: Int) {
        this.piano.tiles[pos].click(index)
        this.handler.deleteRect(this.piano)
        if(this.level == 1) {
            this.score += 100
        } else {
            this.score += 200
        }
    }

    fun addShake() {
        this.score += 10
    }
}