package com.pppb.tb02.model

class Piano() {
    var notes: List<Note> = listOf()

    fun add(top: Int, tilePos: Int, isBonusLevel: Boolean = false) {
        val newNote: Note

        newNote = if(notes.isEmpty()) {
            Note(top, tilePos)
        } else {
            val pos = this.notes[this.notes.size - 1].top - 500
            Note(pos, tilePos)
        }

        if(isBonusLevel) {
            newNote.setBonus()
        }

        this.notes += newNote
    }
}