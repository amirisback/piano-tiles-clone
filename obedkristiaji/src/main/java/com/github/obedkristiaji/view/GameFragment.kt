package com.github.obedkristiaji.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.github.obedkristiaji.R
import com.github.obedkristiaji.databinding.FragmentGameBinding
import com.github.obedkristiaji.model.Piano
import com.github.obedkristiaji.presenter.IMainPresenter

class GameFragment : Fragment(), View.OnTouchListener {
    private lateinit var binding: FragmentGameBinding
    private lateinit var listener: IMainActivity
    private lateinit var canvas: Canvas
    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var handler: ThreadHandler
    private lateinit var presenter: IMainPresenter
    lateinit var pianoThread: PianoThread
    private var imageResource = R.drawable.btn_play

    init {

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentGameBinding.inflate(inflater, container, false)

        this.mDetector = GestureDetectorCompat(this.activity, MyCustomGestureListener())

        this.binding.ivCanvas.setOnTouchListener(this)

        this.binding.btnPlay.setOnClickListener {
            if(this.presenter.isThread() == false) {
                this.pianoThread = PianoThread(this.handler, Pair(this.canvas.width, this.canvas.height), this.presenter.getPL())
                this.presenter.setThread(true)
            }

            if(this.imageResource == R.drawable.btn_play) {
                this.binding.btnPlay.setImageResource(R.drawable.btn_pause)
                this.imageResource = R.drawable.btn_pause
                this.listener.showCountdown()
            } else if(this.imageResource == R.drawable.btn_pause) {
                this.presenter.setPause(true)
                this.presenter.setPlay(false)
                this.binding.btnPlay.setImageResource(R.drawable.btn_resume)
                this.imageResource = R.drawable.btn_resume
                this.pianoThread.stop()
            } else if(this.imageResource == R.drawable.btn_resume) {
                this.presenter.setPause(false)
                this.binding.btnPlay.setImageResource(R.drawable.btn_pause)
                this.imageResource = R.drawable.btn_pause
                this.pianoThread.setPiano(this.presenter.getPiano(), this.presenter.getScore())
                this.listener.showCountdown()
            }
        }

        return this.binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is IMainActivity) {
            this.listener = context as IMainActivity
        } else {
            throw ClassCastException(context.toString()
                    + " must implement FragmentListener")
        }
    }

    companion object {
        fun newInstance(presenter: IMainPresenter, handler: ThreadHandler): GameFragment {
            val fragment: GameFragment = GameFragment()
            fragment.handler = handler
            fragment.presenter = presenter
            return fragment
        }
    }

    fun drawRect(piano: Piano) {
        val paint = Paint()
        paint.color = ResourcesCompat.getColor(resources, R.color.black, null)

        for (i in 0..piano.size-1) {
            for (j in 0..piano.tiles[i].getSize()-1) {
                this.canvas.drawRect(piano.tiles[i].getRect(j), paint)
            }
        }
    }

    fun setScore(score: Int) {
        this.binding.tvScore.setText("Score : " + score.toString())
    }

    fun initiateGame() {
        if(this.presenter.isStartGame() == false) {
            this.initiatePage()
            this.presenter.setStartGame(true)
        } else {
            this.initiateCanvas()
            this.setScore(0)
        }

        if(this.presenter.isLose() == true) {
            this.binding.btnPlay.setImageResource(R.drawable.btn_play)
            this.presenter.setLose(false)
        }
    }

    fun initiatePage() {
        this.initiateCanvas()
        this.binding.tvScore.setText(R.string.score)
        this.pianoThread = PianoThread(this.handler, Pair(this.canvas.width, this.canvas.height), this.presenter.getPL())
        this.pianoThread.start()
        this.pianoThread.stop()
    }

    private fun initiateCanvas() {
        val width = this.binding.ivCanvas.width
        val height = this.binding.ivCanvas.height

        val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        this.binding.ivCanvas.setImageBitmap(mBitmap)

        this.canvas = Canvas(mBitmap)

        this.resetCanvas()
    }

    fun resetCanvas() {
        this.canvas.drawColor(ResourcesCompat.getColor(resources, this.presenter.getPC(), null))

        this.binding.ivCanvas.invalidate()
    }

    fun addShake() {
        this.pianoThread.addShake()
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if(this.presenter.isStartGame() == true && this.presenter.isThread() == true && this.presenter.isPause() == false) {
            return this.mDetector.onTouchEvent(motionEvent)
        } else {
            return false
        }
    }

    private inner class MyCustomGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            if (e != null) {
                for (i in 0..presenter.getPiano().size-1) {
                    for (j in 0..presenter.getPiano().tiles[i].getSize()-1) {
                        if(presenter.getPiano().tiles[i].getRect(j).contains((e.x).toInt(), (e.y).toInt())) {
                            pianoThread.deleteTile(i, j)
                            break
                        }
                    }
                }
            }
            return super.onDown(e)
        }
    }
}