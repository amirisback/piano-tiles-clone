package com.github.jghjianghan.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.github.jghjianghan.R
import com.github.jghjianghan.databinding.ActivityMainBinding
import com.github.jghjianghan.model.GameMode
import com.github.jghjianghan.model.Page
import com.github.jghjianghan.model.SharedPrefWriter
import com.github.jghjianghan.viewmodel.MainVM

class MainActivity : AppCompatActivity(), FragmentListener {
    private lateinit var vm: MainVM
    private lateinit var menuFragment: MenuFragment
    private lateinit var descriptionFragment: DescriptionFragment
    private lateinit var gameplayFragment: GameplayFragment
    private lateinit var resultFragment: ResultFragment
    private lateinit var highscoreFragment: HighscoreFragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefWriter: SharedPrefWriter
    private var page = Page.MENU

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = this.supportFragmentManager
        menuFragment = MenuFragment()
        descriptionFragment = DescriptionFragment()
        gameplayFragment = GameplayFragment()
        resultFragment = ResultFragment()
        highscoreFragment = HighscoreFragment()
        sharedPrefWriter = SharedPrefWriter(this)

        vm = ViewModelProvider(this).get(MainVM::class.java)
        initHighscore()

        changePage(Page.MENU)
    }

    override fun changePage(p: Page) {
        val ft = fragmentManager.beginTransaction()
        when (p) {
            Page.MENU -> {
                ft.replace(R.id.fragment_container, menuFragment)
            }

            Page.DESCRIPTION -> {
                ft.replace(R.id.fragment_container, descriptionFragment).addToBackStack(null)
            }

            Page.GAMEPLAY -> {
                ft.replace(R.id.fragment_container, gameplayFragment)
            }

            Page.RESULT -> {
                ft.replace(R.id.fragment_container, resultFragment)
            }
            Page.HIGHSCORE -> {
                ft.replace(R.id.fragment_container, highscoreFragment).addToBackStack(null)
            }
        }
        ft.commit()
        page = p
    }

    override fun onBackPressed() {
        when (page) {
            Page.GAMEPLAY -> {
                gameplayFragment.showPauseDialog()
            }
            Page.RESULT -> changePage(Page.MENU)
            else -> super.onBackPressed()
        }
    }

    override fun closeApplication() {
        moveTaskToBack(true)
        finish()
    }

    private fun initHighscore() {
        vm.setClassicHighScore(sharedPrefWriter.getClassicHighscore())
        vm.setArcadeHighScore(sharedPrefWriter.getArcadeHighscore())
        vm.setRainingHighScore(sharedPrefWriter.getRainingHighscore())
        vm.setTiltHighScore(sharedPrefWriter.getTiltHighscore())
    }

    override fun updateHighscore(newHighscore: Number, mode: GameMode) {
        when (mode) {
            GameMode.RAINING -> {
                val hs = newHighscore.toInt()
                sharedPrefWriter.saveRainingHighscore(hs)
                vm.setRainingHighScore(hs)
            }
            GameMode.CLASSIC -> {
                val hs = newHighscore.toFloat()
                sharedPrefWriter.saveClassicHighscore(hs)
                vm.setClassicHighScore(hs)
            }
            GameMode.ARCADE -> {
                val hs = newHighscore.toInt()
                sharedPrefWriter.saveArcadeHighscore(hs)
                vm.setArcadeHighScore(hs)
            }
            GameMode.TILT -> {
                val hs = newHighscore.toInt()
                sharedPrefWriter.saveTiltHighscore(hs)
                vm.setTiltHighScore(hs)
            }
        }
    }
}