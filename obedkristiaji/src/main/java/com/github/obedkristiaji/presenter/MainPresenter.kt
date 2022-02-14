package com.github.obedkristiaji.presenter

import com.github.obedkristiaji.model.Piano
import com.github.obedkristiaji.storage.GameStorage
import com.github.obedkristiaji.view.MainActivity

class MainPresenter(private val view: MainActivity): IMainPresenter {
    private val db: GameStorage = GameStorage(this.view)
    private var piano: Piano = Piano()
    private var startHome = false
    private var startLose = false
    private var startGame = false
    private var thread = false
    private var play = false
    private var pause = false
    private var lose = false
    private var score = 0
    private var lastScore = 0

    override fun lose(score: Int) {
        if(score > this.db.getScore()) {
            this.db.saveScore(score)
        }
        this.play = false
        this.lose = true
        this.view.showLoseDialog()
        if(this.startLose == true) {
            this.view.setLoseScore(this.lastScore)
        }
    }

    override fun resetThread() {
        this.thread = false
    }

    override fun setPiano(piano: Piano, score: Int) {
        this.piano = piano
        this.score = score
    }

    override fun setRect(piano: Piano, score: Int) {
        this.view.drawPiano(piano)
        this.setScore(score)
        this.setLastScore(score)
    }

    override fun deleteRect(piano: Piano) {
        this.piano = piano
    }

    override fun setScore(score: Int) {
        this.view.setScore(score)
    }

    override fun getPiano(): Piano {
        return this.piano
    }

    override fun getScore(): Int {
        return this.score
    }

    override fun isThread(): Boolean {
        return this.thread
    }

    override fun isPlay(): Boolean {
        return this.play
    }

    override fun isPause(): Boolean {
        return this.pause
    }

    override fun isLose(): Boolean {
        return this.lose
    }

    override fun setThread(thread: Boolean) {
        this.thread = thread
    }

    override fun setPlay(play: Boolean) {
        this.play = play
    }

    override fun setPause(pause: Boolean) {
        this.pause = pause
    }

    override fun setLose(lose: Boolean) {
        this.lose = lose
    }

    override fun getHS(): Int {
        return this.db.getScore()
    }

    override fun getPC(): Int {
        return this.db.getColor()
    }

    override fun getPL(): Int {
        return this.db.getLevel()
    }

    override fun setColor(color: Int) {
        this.db.saveColor(color)
    }

    override fun setLevel(level: Int) {
        this.db.saveLevel(level)
    }

    override fun isStartHome(): Boolean {
        return this.startHome
    }

    override fun isStartLose(): Boolean {
        return this.startLose
    }

    override fun isStartGame(): Boolean {
        return this.startGame
    }

    override fun setStartHome(start: Boolean) {
        this.startHome = start
    }

    override fun setStartLose(start: Boolean) {
        this.startLose = start
    }

    override fun setStartGame(start: Boolean) {
        this.startGame = start
    }

    override fun setLastScore(score: Int) {
        this.lastScore = score
    }

    override fun getLastScore(): Int {
        return this.lastScore
    }
}