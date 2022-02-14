package com.github.obedkristiaji.view

import com.github.obedkristiaji.model.Piano

interface IMainActivity {
    fun changePage(page: Int)
    fun showCountdown()
    fun showLoseDialog()
    fun showSetting()
    fun closeApplication()
    fun drawPiano(piano: Piano)
    fun setScore(score: Int)
    fun startThread()
    fun updateHighScore(score: Int)
    fun initialize()
    fun setLoseScore(score: Int)
}