package com.github.jghjianghan.model.audio

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build

class PianoPlayer(val context: Context, val song: Song) {
    var iter = 0
    private val noteToRes: MutableMap<MusicalNote, Int> = mutableMapOf()
    private val soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        SoundPool.Builder().setMaxStreams(5).build()
    } else {
        SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    }

    init {
        for (note in MusicalNote.values()){
            noteToRes[note] = soundPool.load(context, note.resId, 1)
        }
    }

    fun playNext(){
        soundPool.play(noteToRes[song.noteList[iter++]]!!, 1f, 1f, 0, 0, 1f)
        iter %= song.noteList.size
    }

    fun playWrong(){
        soundPool.play(noteToRes[MusicalNote.C4]!!, 1f, 1f, 0, 0, 1f)
    }
}