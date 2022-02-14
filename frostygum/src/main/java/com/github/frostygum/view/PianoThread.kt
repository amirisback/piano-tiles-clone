package com.github.frostygum.view


import com.github.frostygum.model.Piano
import com.github.frostygum.util.PianoGenerator
import kotlin.random.Random

class PianoThread(
    private val handler: PianoThreadHandler,
    private val canvasSize: Pair<Int, Int>
): Runnable {
    private var thread: Thread = Thread(this)
    private var piano: Piano = Piano()
    private var isRunning: Boolean = false
    private var isHasInitiated: Boolean = false
    private var isBonusLevel: Boolean = false
    private var level: Int = 1

    override fun run() {
        try {
            while(this.isRunning) {
                Thread.sleep(16)
                var hiddenFound = 0
                var bonusNoteCount = 0

                for(note in this.piano.notes) {
                    if(!note.isHidden) {
                        if(!note.isClicked && note.bottom > this.canvasSize.second && !isBonusLevel) {
                            note.lose()
                            this.lost()
                        }

                        if(note.top > this.canvasSize.second) {
                            note.hide()
                        }

                        if(note.tilePos >= 0) {
                            val newPos = calculateTilesMovement(note.top, 15)
                            note.top = newPos
                            note.bottom = newPos + note.length
                        }
                        else {
                            note.hide()
                        }
                    }
                    else {
                        if(note.isBonus) {
                            bonusNoteCount++
                        }
                        hiddenFound++
                    }
                }

                //If all note has been hidden (out of bottom frame)
                if(hiddenFound == this.piano.notes.size) {
                    val size = this.piano.notes.size
                    this.generatePiano()

                    //If all bonus not has been hidden
                    if(bonusNoteCount == size) {
                        this.isBonusLevel = false
                    }
                }

                this.handler.sendTilesLocation(this.piano)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun calculateTilesMovement(rectStartPos: Int, movementLength: Int): Int {
        //multiplier: multiplier for speed/ note movement
        val multiplier = 2
        return rectStartPos + (movementLength * multiplier) + this.level
    }

    private fun generatePiano() {
        //Bonus level spawn every 5 level
        if(!isBonusLevel && (this.level + 1) % 5 == 0) {
            this.isBonusLevel = true
            this.handler.sendGameLevel("BONUS")
            this.piano = PianoGenerator.createPiano(30, -500, Random.nextBoolean(), true)
        }
        else {
            this.level++
            this.handler.sendGameLevel(this.level.toString())
            this.piano = PianoGenerator.createPiano(25 + (this.level * 2), -500, Random.nextBoolean())
        }
    }

    fun setLastPos(piano: Piano) {
        this.piano = piano
        this.isHasInitiated = true
    }

    fun setLastLevel(level: String) {
        this.level = level.toInt()
    }

    fun start() {
        if(!isHasInitiated) {
            this.isHasInitiated = true
        }

        if(!isRunning) {
            this.isRunning = true
            this.thread.start()
        }
    }

    fun block() {
        this.handler.threadHasBlocked()
        this.isRunning = false
    }

    private fun lost() {
        this.handler.gameLost()
        this.isRunning = false
    }
}