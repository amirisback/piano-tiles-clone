package com.github.frostygum.presenter

import com.github.frostygum.model.Piano

interface IMainPresenter {
    fun setPiano(piano: Piano)
    fun setThreadBlocked()
    fun setLevel(level: String)
    fun setGameLost()
    fun getPiano(): Piano
    fun getLevel(): String
    fun getScore(): Int
    fun addScore(score: Int)
    fun resetGame()
    fun isThreadHasRunning(): Boolean
    fun isThreadHasBlocked(): Boolean
    fun threadIsBlocked(state: Boolean)
    fun threadIsRunning(state: Boolean)
    fun setBonusLevel(state: Boolean)
    fun isBonusLevel(): Boolean
}