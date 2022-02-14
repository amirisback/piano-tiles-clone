package com.github.frostygum.presenter

import android.app.Application
import com.github.frostygum.model.HighScore
import com.github.frostygum.model.Piano
import com.github.frostygum.storage.ViewStorage
import com.github.frostygum.util.PianoGenerator
import com.github.frostygum.view.IMainActivity
import kotlin.random.Random

class MainPresenter(private val ui: IMainActivity, private val application: Application): IMainPresenter {
    private var score: Int = 0
    private var piano: Piano = Piano()
    private var level: String = "1"
    private var scoreList: MutableList<HighScore> = mutableListOf()
    //Game States
    private var isThreadHasInitiated: Boolean = false
    private var isThreadHasBlocked: Boolean = false
    private var isThreadHasRunning: Boolean = false
    private var isBonusLevel: Boolean = false
    //Storage
    private val storage: ViewStorage = ViewStorage(this.application)

    init {
        this.scoreList.addAll(this.storage.getScoreList())
    }

    override fun isBonusLevel() = this.isBonusLevel

    override fun getPiano() = this.piano

    override fun getLevel() = this.level

    override fun getScore() = this.score

    override fun isThreadHasRunning() = this.isThreadHasRunning

    override fun isThreadHasBlocked() = this.isThreadHasBlocked

    override fun threadIsRunning(state: Boolean) {
        this.isThreadHasRunning = state
    }

    override fun setBonusLevel(state: Boolean) {
        this.isBonusLevel = state
    }

    override fun threadIsBlocked(state: Boolean) {
        this.isThreadHasBlocked = state
    }

    override fun setPiano(piano: Piano) {
        this.piano = piano
        this.ui.updatePiano(piano)
    }

    override fun resetGame() {
        //Reset game, set default value of level to 1 and score to 0
        this.setLevel("1")
        this.setScore(0)
        this.piano = PianoGenerator.createPiano(20, 500, Random.nextBoolean())
    }

    override fun addScore(customScore: Int) {
        this.score += customScore
        this.setScore(this.score)
    }

    private fun setScore(score: Int) {
        this.score = score
        this.ui.updateUIScore(score)
    }

    override fun setLevel(level: String) {
        this.level = level
        this.ui.setGameLevel(this.level)
    }

    override fun setThreadBlocked() {
        this.isThreadHasRunning = false
        this.isThreadHasBlocked = true
        this.isThreadHasInitiated = false
    }

    override fun setGameLost() {
        this.isThreadHasRunning = false
        this.isThreadHasBlocked = false
        this.isThreadHasInitiated = false
        this.scoreList.add(HighScore(this.level, this.score))
        //Sort the score list by its score
        this.scoreList.sortByDescending { it.score }
        //Save Score List to storage
        this.storage.saveScoreList(this.scoreList)
        //Update Score List UI
        this.ui.setGameLost(this.scoreList)
    }
}