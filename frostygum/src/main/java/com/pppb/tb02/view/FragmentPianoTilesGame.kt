package com.pppb.tb02.view

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.pppb.tb02.R
import com.pppb.tb02.databinding.FragmentPianoTilesGameBinding
import com.pppb.tb02.model.Piano
import com.pppb.tb02.presenter.IMainPresenter
import java.lang.ClassCastException

class FragmentPianoTilesGame: Fragment(R.layout.fragment_piano_tiles_game), View.OnTouchListener {
    private lateinit var binding: FragmentPianoTilesGameBinding
    private lateinit var listener: IMainActivity
    private lateinit var tilesThread: PianoThread
    private lateinit var canvas: Canvas
    private lateinit var detector: GestureDetector
    private lateinit var handler: PianoThreadHandler
    private lateinit var presenter: IMainPresenter

    companion object {
        fun newInstance(presenter: IMainPresenter, handler: PianoThreadHandler): FragmentPianoTilesGame {
            val fragment = FragmentPianoTilesGame()
            fragment.presenter = presenter
            fragment.handler = handler
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentPianoTilesGameBinding.inflate(inflater, container, false)

        //create gesture detector + listener
        this.detector = GestureDetector(this.activity, TilesGestureListener())
        this.binding.ivCanvas.setOnTouchListener(this)

        this.binding.btnPause.setOnClickListener {
            if(this.presenter.isThreadHasRunning() && !this.presenter.isThreadHasBlocked()) {
                this.tilesThread.block()
                this.listener.changePage("PAUSE")
            }
        }

        return this.binding.root
    }

    fun updateUIScore(score: Int) {
        this.binding.tvScore.text = score.toString()
    }

    fun updateGameLevel(level: String) {
        this.binding.tvLevel.text = level
    }

    private fun startThread(piano: Piano) {
        this.tilesThread = PianoThread(
            this.handler,
            Pair(this.canvas.width, this.canvas.height)
        )

        if(this.presenter.isThreadHasBlocked()) {
            this.presenter.threadIsBlocked(true)
            this.tilesThread.setLastPos(this.presenter.getPiano())
            this.tilesThread.setLastLevel(this.presenter.getLevel())
        }
        else {
            this.tilesThread.setLastPos(piano)
        }

        this.presenter.threadIsRunning(true)
        //Start Piano Tiles Thread
        this.tilesThread.start()
    }

    fun drawTiles(piano: Piano) {
        this.resetCanvas()
        //Initialize Paint color for tiles
        val fillPaint = Paint()
        fillPaint.style = Paint.Style.FILL

        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 100F;

        //Each tiles width
        val bin = this.binding.ivCanvas.width / 4

        for((i, note) in piano.notes.withIndex()) {
            val top = note.top
            val bottom = note.bottom
            val left = bin * note.tilePos
            val right = bin * (note.tilePos + 1)

            if(!note.isHidden) {
                fillPaint.color = Color.BLACK

                if(i == 0 && !this.presenter.isThreadHasRunning()) {
                    fillPaint.color = Color.CYAN
                }

                if(note.isBonus) {
                    fillPaint.color = Color.YELLOW
                }
                if(note.isClicked) {
                    fillPaint.color = Color.GRAY
                }
                if(note.isLoser) {
                    fillPaint.color = Color.RED
                }

                if(note.tilePos >= 0) {
                    val rect = Rect(left, top, right, bottom)
                    this.canvas.drawRect(rect, fillPaint)
                }

                if(i == 0 && !this.presenter.isThreadHasRunning() && !note.isLoser) {
                    fillPaint.color = Color.CYAN

                    val y = top + (kotlin.math.abs(top - bottom) / 2) + (textPaint.textSize / 2)
                    val x = left + (kotlin.math.abs(left - right) / 2) - textPaint.textSize

                    this.canvas.drawText("Start", x, y, textPaint)
                }
            }
        }
    }

    private fun drawBackground() {
        this.canvas.drawColor(ResourcesCompat.getColor(resources, R.color.canvas_bg, null))
        val fillPaint = Paint()
        fillPaint.color = Color.BLACK
        val bin = this.binding.ivCanvas.width / 4

        for(i in 0..2) {
            val right = bin * (i + 1)

            this.canvas.drawLine(right.toFloat(), 0F, right.toFloat(), this.binding.ivCanvas.height.toFloat(), fillPaint)
        }
    }

    private fun initiateCanvas() {
        //Get imageView width and height
        val width = this.binding.ivCanvas.width
        val height = this.binding.ivCanvas.height

        //Create Bitmap
        val mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        //Associate the bitmap to the ImageView
        this.binding.ivCanvas.setImageBitmap(mBitmap)

        //Create a canvas with the bitmap
        this.canvas = Canvas(mBitmap)

        //Reset canvas
        this.resetCanvas()
    }

    private fun resetCanvas() {
        //Draw canvas background
        this.drawBackground()
        //Force re-draw
        this.binding.ivCanvas.invalidate()
    }

    fun calculateClickPos(x: Float, y: Float) {
        //bin : col length/ tile length
        val bin = this.binding.ivCanvas.width / 4

        for((i, note) in this.presenter.getPiano().notes.withIndex()) {
            val start = bin * note.tilePos
            val end = bin * (note.tilePos + 1)

            //if in tile-i x range
            if(x > start && x < end) {
                //if in note-i y range
                if(y > (note.top - 150) && y < note.bottom) {
                    //if note hasn't hidden
                    if(!this.presenter.isThreadHasRunning()) {
                        if(i == 0) {
                            this.startThread(this.presenter.getPiano())
                            note.clicked()
                            break
                        }
                    }
                    else {
                        if(!note.isHidden && !note.isClicked && !note.isBonus) {
                            //hide node & add score to ui activity
                            note.clicked()
                            this.presenter.addScore(10)
                            break
                        }
                    }
                }
            }
        }
    }

    fun initialization() {
        this.initiateCanvas()
        this.presenter.resetGame()
        this.drawTiles(this.presenter.getPiano())
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            if(this.presenter.isThreadHasBlocked()) {
                this.startThread(this.presenter.getPiano())
            }
            else {
                this.drawTiles(this.presenter.getPiano())
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is IMainActivity) {
            this.listener = context
        }
        else {
            throw ClassCastException("$context must implement FragmentListener")
        }
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        return this.detector.onTouchEvent(event)
    }

    private inner class TilesGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            if (e != null) {
                calculateClickPos(e.x, e.y)
            }
            return super.onDown(e)
        }
    }
}