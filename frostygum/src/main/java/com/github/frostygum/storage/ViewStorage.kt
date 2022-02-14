package com.github.frostygum.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.github.frostygum.model.HighScore

class ViewStorage(ctx: Context)  {
    private var sp: SharedPreferences
    private val spName: String = "sp_display"
    private val keyScoreList: String = "SCORE_LIST"

    init {
        this.sp = ctx.getSharedPreferences(spName, 0)
    }

    fun saveScoreList(scoreList: List<HighScore>) {
        val scoreListStr = Gson().toJson(scoreList)
        this.sp.edit().putString(keyScoreList, scoreListStr).commit()
    }

    fun getScoreList(): List<HighScore> {
        val scoreStr = this.sp.getString(keyScoreList, "")
        val sType = object : TypeToken<List<HighScore>>() { }.type

        return Gson().fromJson(scoreStr, sType) ?: listOf()
    }
}