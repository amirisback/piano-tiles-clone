package com.example.pianotiles.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.pianotiles.R

class GameStorage(context: Context) {
    protected var sharedPref: SharedPreferences
    companion object {
        protected var NAMA_SHARED_PREF: String = "sp_menu"
        protected var KEY_SCORE: String = "HIGH_SCORE"
        protected var KEY_COLOR: String = "PICKED_COLOR"
        protected var KEY_LEVEL: String = "PICKED_LEVEL"
    }

    init {
        this.sharedPref = context.getSharedPreferences(NAMA_SHARED_PREF, 0)
    }

    fun saveScore(score: Int) {
        val editor: SharedPreferences.Editor = this.sharedPref.edit()
        editor.putInt(KEY_SCORE, score)
        editor.commit()
    }

    fun getScore(): Int {
        return sharedPref.getInt(KEY_SCORE, 0)
    }
    
    fun saveColor(color: Int) {
        val editor: SharedPreferences.Editor = this.sharedPref.edit()
        editor.putInt(KEY_COLOR, color)
        editor.commit()
    }

    fun getColor(): Int {
        return sharedPref.getInt(KEY_COLOR, R.color.red)
    }

    fun saveLevel(level: Int) {
        val editor: SharedPreferences.Editor = this.sharedPref.edit()
        editor.putInt(KEY_LEVEL, level)
        editor.commit()
    }

    fun getLevel(): Int {
        return sharedPref.getInt(KEY_LEVEL, 1)
    }
}