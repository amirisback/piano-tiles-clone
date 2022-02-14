package com.pppb.tb02.view

import com.pppb.tb02.model.HighScore
import com.pppb.tb02.model.Piano

interface IMainActivity {
    fun updateUIScore(score: Int)
    fun changePage(page: String)
    fun updatePiano(piano: Piano)
    fun setGameLevel(level: String)
    fun setGameLost(scoreList: MutableList<HighScore>)
}