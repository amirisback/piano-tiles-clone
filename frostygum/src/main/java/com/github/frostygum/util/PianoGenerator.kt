package com.github.frostygum.util

import com.github.frostygum.model.Piano

object PianoGenerator {
    fun createPiano(size: Int, startPos: Int, alwaysHasNote: Boolean = false, isBonusLevel: Boolean = false): Piano {
        val piano = Piano()
        for(i in 0..size) {
            var startLoc = -1
            var prevLoc = -1

            if(alwaysHasNote) {
                startLoc = 0
            }

            if(i == 0) {
                startLoc = 0
            }
            else {
                prevLoc = piano.notes[piano.notes.size - 1].tilePos
            }

            val tilePos = this.randomPos(startLoc, prevLoc)

            piano.add(startPos, tilePos, isBonusLevel)
        }
        return piano
    }

    private fun randomPos(start: Int, prevPos: Int): Int {
        var pos = 0
        var hasFound = false
        while(!hasFound) {
            pos = (start..3).random()

            if(pos != prevPos) {
                hasFound = true
            }
        }
        return pos
    }
}