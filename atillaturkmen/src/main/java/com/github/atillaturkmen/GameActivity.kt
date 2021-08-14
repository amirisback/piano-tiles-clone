package com.github.atillaturkmen

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup


class GameActivity : BaseActivity() {

    lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ati", "activity created")

        setContentView(R.layout.activity_game)

        val speed = intent.getStringExtra("speed")
        val music = intent.getBooleanExtra("music", true)
        val vibration = intent.getBooleanExtra("vibration", true)

        GameView.music = music
        GameView.vibration = vibration

        if (speed != "") {
            Tile.speed = speed!!.toInt()
        }

        val screen = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        gameView = GameView(this)
        gameView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        screen.addView(gameView)

    }

    override fun onPause() {
        Log.d("ati", "activity paused")
        super.onPause()
    }

    override fun onResume() {
        Log.d("ati", "activity resumed")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d("ati", "activity destroyed")
        gameView.destroy()
        super.onDestroy()
    }
}
