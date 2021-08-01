package com.frogobox.pianotilesclone

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.view.*
import android.view.WindowManager.BadTokenException
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.frogobox.pianotilesclone.databinding.ActivityGameBinding
import java.util.*

class GameActivity : BaseActivity<ActivityGameBinding>() {
    
    private var timer = Timer()
    private val handler = Handler()

    private val myPreferences: SharedPreferences by lazy {
        getSharedPreferences("my_prefs", 0)
    }

    private var difficultyName: String? = null
    
    private var paused = false
    private var updateScore = false
    private val gameRunning = false
    
    private var tileWidth = 0f
    private var tileHeight = 0f
    private var speed = 17f
    private var backupSpeed = speed
    
    private var difficulty = 0
    private var score = 0
    private var lastSc = 0
    private var childCount = 0
    private var period = 225
    
    private var imagePianoTilesList = mutableListOf<ImageView>()
    private var availablePositions= mutableListOf<Float>()

    private val b1: MediaPlayer? = null
    private val b2: MediaPlayer? = null
    private var b3: MediaPlayer? = null
    private val b4: MediaPlayer? = null

    private val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels

    override fun setupViewBinding(): ActivityGameBinding {
        return ActivityGameBinding.inflate(layoutInflater)
    }

    override fun setupUI(savedInstanceState: Bundle?) {
        tileHeight = (screenWidth / 2).toFloat()
        tileWidth = (screenWidth / 4).toFloat()

        when (myPreferences.getString("PDifficulty", "0")!!.toInt()) {
            0 -> {
                difficultyName = "Easy"
                difficulty = 0
            }

            1 -> {
                difficulty = 1
                difficultyName = "Medium"
            }

            2 -> {
                difficultyName = "Hard"
                difficulty = 2
            }
        }
        if (gameRunning) {
            initGame()
        }
        binding.btnStart.setOnClickListener { v: View? -> startGame() }
    }

    private fun initDifficulty() {
        when (difficulty) {
            0 -> {
                speed = 10f
                period = 350
            }
            1 -> {
                speed = 12f
                period = 300
            }
            2 -> {
                speed = 14f
                period = 250
            }
        }
        backupSpeed = speed
    }

    private fun increaseDifficulty() {
        if (score - lastSc > 25) {
            if (speed <= 17) speed += 1f else {
                period -= 5
            }
            lastSc = score
        }
    }

    private fun initGame() {
        paused = false
        score = 0
        initDifficulty()
        b3 = MediaPlayer.create(applicationContext, R.raw.b3)
        binding.tvScore.text = score.toString()
        for (i in 0..3) {
            availablePositions.add(tileHeight)
        }
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    moveTiles()
                    if (updateScore) {
                        increaseDifficulty()
                        score += 1
                        binding.tvScore.text = score.toString()
                        updateScore = false
                    }
                }
            }
        }, 0, 10)
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post { if (!paused) pickNextTileToDraw() }
            }
        }, 0, period.toLong())
    }

    private fun updateScoreList() {
        val v = myPreferences.getString("PName", "Player") + "\n" + score;
        val mySet = HashSet(myPreferences.getStringSet(difficultyName, HashSet()))
        val editor = myPreferences.edit()
        mySet.add(v)
        editor.putStringSet(difficultyName, HashSet(mySet))
        editor.apply()
    }

    private fun showStats() {
        updateScoreList()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Results")
        val message = """
            Player:${myPreferences.getString("PName", "Player")}
            Difficulty:$difficultyName
            Score:$score
            """.trimIndent()
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog: DialogInterface?, which: Int -> }
        val alertDialog = builder.create()
        try {
            alertDialog.show()
        } catch (e: BadTokenException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.game_menu, menu)
        return true
    }

    private fun moveTiles() {
        if (!paused) {
            if (speed < backupSpeed) {
                speed += 0.05f
            }
            var quit = false
            for (imageView in imagePianoTilesList) {
                imageView.y = imageView.y + speed
                if (quit) {
                    imagePianoTilesList = ArrayList()
                    break
                }
                if (imageView.y > screenHeight) {
                    if (imageView.minimumHeight == 0) {
                        resetGame()
                        showStats()
                        quit = true
                    }
                    binding.parentRelative.removeView(imageView)
                }
            }
            for (i in 0..3) {
                availablePositions[i] = availablePositions[i] + speed
            }
        }
    }

    private fun pickNextTileToDraw() {
        val rd = Random()
        var run = true
        while (run) {
            val rnd = Math.abs(rd.nextInt()) % 4
            val f = rd.nextFloat()
            if (availablePositions[rnd] >= tileHeight + speed) {
                drawTile(f > 0.25, rnd * tileWidth, 0f)
                availablePositions[rnd] = 0f
                run = false
            }
        }
    }

    private fun drawTile(black: Boolean, x: Float, y: Float) {
        val imageView = ImageView(applicationContext)
        addView(imageView, tileWidth.toInt(), tileHeight.toInt())
        imageView.x = x
        imageView.y = -2 * tileHeight
        if (black) {
            imageView.setBackgroundColor(Color.BLACK)
            imageView.minimumHeight = 0
        } else {
            imageView.setBackgroundColor(Color.WHITE)
            imageView.minimumHeight = 1
        }
        enableListeners(imageView, black)
        imagePianoTilesList.add(imageView)
        childCount += 1
    }

    private fun enableListeners(imageView: ImageView, black: Boolean) {
        imageView.setOnClickListener { v: View -> onTileClick(v, black) }
        imageView.setOnLongClickListener { v: View ->
            onTileClick(v, black)
            false
        }
        imageView.setOnGenericMotionListener { v: View, event: MotionEvent? ->
            onTileClick(v, black)
            false
        }
        imageView.setOnHoverListener { v: View, event: MotionEvent? ->
            onTileClick(v, black)
            false
        }
        imageView.setOnDragListener { v: View, event: DragEvent? ->
            onTileClick(v, black)
            false
        }
    }

    private fun checkIfSounds(): Boolean {
        return myPreferences.getString("PSound", "0")!!.toInt() == 1
    }

    private fun onTileClick(view: View, black: Boolean) {
        if (!paused) {
            if (black) {
                binding.parentRelative.removeView(view)
                imagePianoTilesList.remove(view)
                updateScore = true
                if (checkIfSounds()) {
                    b3!!.start()
                }
            } else {
                resetGame()
                showStats()
            }
        }
    }

    private fun addView(imageView: ImageView, width: Int, height: Int) {
        val params = LinearLayout.LayoutParams(width, height)
        params.setMargins(0, 0, 0, 0)
        imageView.layoutParams = params
        binding.parentRelative.addView(imageView)
    }

    fun pauseGame(item: MenuItem?) {
        if (!paused) {
            paused = true
            speed = 10f
        } else {
            paused = false
        }
    }

    private fun resetGame() {
        timer.cancel()
        timer.purge()
        timer = Timer()
        for (imageView in imagePianoTilesList) {
            binding.parentRelative.removeView(imageView)
        }
        binding.btnStart.visibility = View.VISIBLE
    }

    private fun startGame() {
        initGame()
        binding.btnStart.visibility = View.GONE
    }

}