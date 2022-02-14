package com.github.frostygum.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.frostygum.databinding.ActivityMainBinding
import com.github.frostygum.model.HighScore
import com.github.frostygum.model.Piano
import com.github.frostygum.presenter.MainPresenter


class MainActivity : AppCompatActivity(), IMainActivity, SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: PianoThreadHandler
    private lateinit var presenter: MainPresenter
    private lateinit var timer: StartTimer

    private val adapter: ScoreListAdapter = ScoreListAdapter(this)
    private lateinit var fragments: List<Fragment>
    private lateinit var pianoFragment: FragmentPianoTilesGame
    private lateinit var pauseFragment: FragmentGamePause
    private lateinit var loseFragment: FragmentGameLose
    private lateinit var scoreFragment: FragmentScore
    private var selected = Fragment()

    private lateinit var mSensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var accelerometerReading: FloatArray = FloatArray(3)
    private var accelerometerReadingPrev: FloatArray = FloatArray(3)
    private var shakeThreshold: Int = 1500
    private var lastUpdate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Timer Initialization
        this.timer = StartTimer(1000, 500)

        //Presenter Initialization
        this.presenter = MainPresenter(this, this.application)

        //Handler Initialization
        this.handler = PianoThreadHandler(this.presenter)

        //Sensor Initialization
        this.mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.accelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        this.pianoFragment = FragmentPianoTilesGame.newInstance(this.presenter, this.handler)
        this.pauseFragment = FragmentGamePause.newInstance(this.presenter)
        this.loseFragment = FragmentGameLose.newInstance(this.presenter)
        this.scoreFragment = FragmentScore.newInstance(this.presenter, this.adapter)
        this.fragments = listOf(
            this.pianoFragment,
            this.pauseFragment,
            this.loseFragment,
            this.scoreFragment
        )

        //Default start page
        this.changePage("GAME")
    }

    override fun updateUIScore(score: Int) {
        this.pianoFragment.updateUIScore(score)
    }

    override fun updatePiano(piano: Piano) {
        this.pianoFragment.drawTiles(piano)
    }

    override fun setGameLost(scoreList: MutableList<HighScore>) {
        this.loseFragment.setFinalLevel(this.presenter.getLevel())
        this.loseFragment.setFinalScore(this.presenter.getScore())
        this.adapter.update(scoreList)
        this.timer.start()
    }

    override fun setGameLevel(level: String) {
        if(level != "BONUS") {
            this.presenter.setBonusLevel(false)
        }
        else {
            Toast.makeText(this, "Shake your phone to get bonus level scores !!", Toast.LENGTH_SHORT).show()
            this.presenter.setBonusLevel(true)
        }

        this.pianoFragment.updateGameLevel(level)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus) {
            this.pianoFragment.initialization()
        }
    }

    override fun changePage(page: String) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val container: Int = this.binding.fragmentContainer.id
        this.selected = Fragment()

        when (page) {
            "LOSE" -> {
                this.selected = this.loseFragment
            }
            "GAME" -> {
                this.selected = this.pianoFragment
            }
            "PAUSE" -> {
                this.selected = this.pauseFragment
            }
            "SCORE" -> {
                this.selected = this.scoreFragment
            }
        }

        for(fragment in this.fragments) {
            if(fragment == this.selected) {
                if (fragment.isAdded) {
                    ft.show(fragment)
                } else {
                    ft.add(container, fragment)
                }
            }
            else {
                if(fragment.isAdded) {
                    ft.hide(fragment)
                }
            }
        }

        ft.commit()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event == null) {
            return
        }

        if(this.presenter.isBonusLevel()) {
            val v: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            //if sensor is accelerometer
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                this.accelerometerReading = event.values.clone()
                val curTime = System.currentTimeMillis()
                // read sensor every 100ms.
                if (curTime - lastUpdate > 100) {
                    val diffTime: Long = curTime - lastUpdate
                    lastUpdate = curTime
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    //speed is count by delta movement / time (delta movement for every axis x, y, z)
                    val speed: Float = kotlin.math.abs(x + y + z - accelerometerReadingPrev[0] - accelerometerReadingPrev[1] - accelerometerReadingPrev[2]) / diffTime * 10000
                    //if shake detect
                    if (speed > this.shakeThreshold) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(200);
                        }
                        this.presenter.addScore(10)
                    }
                    accelerometerReadingPrev[0] = x
                    accelerometerReadingPrev[1] = y
                    accelerometerReadingPrev[2] = z
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(this.accelerometer != null) {
            mSensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onPause() {
        super.onPause()
        this.mSensorManager.unregisterListener(this)
    }

    private inner class StartTimer(startTime: Long, interval: Long) : CountDownTimer(
        startTime,
        interval
    ) {
        override fun onFinish() {
            changePage("LOSE")
        }

        override fun onTick(millisUntilFinished: Long) {
        }
    }
}