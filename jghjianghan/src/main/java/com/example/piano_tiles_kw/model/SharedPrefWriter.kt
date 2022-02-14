package com.example.piano_tiles_kw.model

import android.content.Context
import android.content.SharedPreferences

/**
 * Class that manages read and write in Shared Preference memory system
 * @param context The context of the running game
 */
class SharedPrefWriter(context: Context) {
    var sharedPref: SharedPreferences
    companion object {
        const val NAMA_SHARED_PREF = "piano_tiles_kw_pref"
        const val CLASSIC_HIGHSCORE = "CLASSIC_HIGHSCORE"
        const val RAINING_HIGHSCORE = "RAINING_HIGHSCORE"
        const val ARCADE_HIGHSCORE = "ARCADE_HIGHSCORE"
        const val TILT_HIGHSCORE = "TILT_HIGHSCORE"
    }

    init {
        this.sharedPref = context.getSharedPreferences(NAMA_SHARED_PREF, 0)
    }

    fun saveClassicHighscore(newHighscore:Float){
        val editor = sharedPref.edit()
        editor.putFloat(CLASSIC_HIGHSCORE, newHighscore)
        editor.commit()
    }
    fun getClassicHighscore(): Float{
        return sharedPref.getFloat(CLASSIC_HIGHSCORE, Float.MAX_VALUE)
    }

    fun saveArcadeHighscore(newHighscore:Int){
        val editor = sharedPref.edit()
        editor.putInt(ARCADE_HIGHSCORE, newHighscore)
        editor.commit()
    }
    fun getArcadeHighscore(): Int{
        return sharedPref.getInt(ARCADE_HIGHSCORE, 0)
    }

    fun saveRainingHighscore(newHighscore:Int){
        val editor = sharedPref.edit()
        editor.putInt(RAINING_HIGHSCORE, newHighscore)
        editor.commit()
    }
    fun getRainingHighscore(): Int{
        return sharedPref.getInt(RAINING_HIGHSCORE, 0)
    }

    fun saveTiltHighscore(newHighscore:Int){
        val editor = sharedPref.edit()
        editor.putInt(TILT_HIGHSCORE, newHighscore)
        editor.commit()
    }
    fun getTiltHighscore(): Int{
        return sharedPref.getInt(TILT_HIGHSCORE, 0)
    }
}