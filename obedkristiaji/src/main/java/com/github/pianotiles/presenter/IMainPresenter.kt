package com.github.pianotiles.presenter

import com.github.pianotiles.model.Piano

interface IMainPresenter {
    fun lose(score: Int)
    fun resetThread()
    fun setPiano(piano: Piano, score: Int)
    fun setRect(piano: Piano, score: Int)
    fun deleteRect(piano: Piano)
    fun setScore(score: Int)
    fun getPiano(): Piano
    fun getScore(): Int
    fun isThread(): Boolean
    fun isPlay(): Boolean
    fun isPause(): Boolean
    fun isLose(): Boolean
    fun setThread(thread: Boolean)
    fun setPlay(play: Boolean)
    fun setPause(pause: Boolean)
    fun setLose(lose: Boolean)
    fun getHS(): Int
    fun getPC(): Int
    fun getPL(): Int
    fun setColor(color: Int)
    fun setLevel(level: Int)
    fun isStartHome(): Boolean
    fun isStartLose(): Boolean
    fun isStartGame(): Boolean
    fun setStartHome(start: Boolean)
    fun setStartLose(start: Boolean)
    fun setStartGame(start: Boolean)
    fun setLastScore(score: Int)
    fun getLastScore(): Int
}