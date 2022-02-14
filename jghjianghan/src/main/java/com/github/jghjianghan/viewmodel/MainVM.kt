package com.github.jghjianghan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.jghjianghan.model.GameMode

/**
 * Class that manages view model, this class extends class ViewModel. This class contains all live data needed in the game.
 */
class MainVM : ViewModel() {

    // gameMode
    private val gameMode = MutableLiveData<GameMode>()
    fun getGameMode(): LiveData<GameMode> = gameMode
    fun setGameMode(newMode: GameMode) {
        gameMode.value = newMode
    }
    // end of gameMode

    // raining score
    private val rainingScore = MutableLiveData<Int>()
    fun getRainingScore(): LiveData<Int> = rainingScore
    fun setRainingScore(newScore: Int) {
        rainingScore.value = newScore
    }
    // end of raining score

    // raining highScore
    private val rainingHighScore = MutableLiveData<Int>()
    fun getRainingHighScore(): LiveData<Int> = rainingHighScore
    fun setRainingHighScore(newHighScore: Int) {
        rainingHighScore.value = newHighScore
    }
    // end of raining highScore

    // classic score
    private val classicScore = MutableLiveData<Float>()
    fun getClassicScore(): LiveData<Float> = classicScore
    fun setClassicScore(newScore: Float) {
        classicScore.value = newScore
    }
    // end of classic score

    // classic highScore
    private val classicHighScore = MutableLiveData<Float>()
    fun getClassicHighScore(): LiveData<Float> = classicHighScore
    fun setClassicHighScore(newHighScore: Float) {
        classicHighScore.value = newHighScore
    }
    // end of classic highScore

    // arcade score
    private val arcadeScore = MutableLiveData<Int>()
    fun getArcadeScore(): LiveData<Int> = arcadeScore
    fun setArcadeScore(newScore: Int) {
        arcadeScore.value = newScore
    }
    // end of arcade score

    // arcade highScore
    private val arcadeHighScore = MutableLiveData<Int>()
    fun getArcadeHighScore(): LiveData<Int> = arcadeHighScore
    fun setArcadeHighScore(newHighScore: Int) {
        arcadeHighScore.value = newHighScore
    }
    // end of arcade highScore

    // tilt score
    private val tiltScore = MutableLiveData<Int>()
    fun getTiltScore(): LiveData<Int> = tiltScore
    fun setTiltScore(newScore: Int) {
        tiltScore.value = newScore
    }
    // end of tilt score

    // tilt highScore
    private val tiltHighScore = MutableLiveData<Int>()
    fun getTiltHighScore(): LiveData<Int> = tiltHighScore
    fun setTiltHighScore(newHighScore: Int) {
        tiltHighScore.value = newHighScore
    }
    // end of tilt highScore

    // toolbarTitle
    private val toolbarTitle = MutableLiveData<String>()
    fun getToolbarTitle(): LiveData<String> = toolbarTitle
    fun setToolbarTitle(newTitle: String) {
        toolbarTitle.value = newTitle
    }
    // end of toolbarTitle

}