package com.github.frostygum.view

import com.github.frostygum.model.HighScore
import com.github.frostygum.model.Piano

interface IMainActivity {
    fun updateUIScore(score: Int)
    fun changePage(page: String)
    fun updatePiano(piano: Piano)
    fun setGameLevel(level: String)
    fun setGameLost(scoreList: MutableList<HighScore>)
}