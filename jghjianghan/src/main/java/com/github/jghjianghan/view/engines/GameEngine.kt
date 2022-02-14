package com.github.jghjianghan.view.engines

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView

/**
 * Generic Class that manages a piano tile game in the given ImageView
 * @param context The context of the running game
 * @param iv The ImageView where the game will be displayed and played
 */
abstract class GameEngine (private val context: Context, private val iv: ImageView) {
    interface GameListener {
        fun onEndGame()
        fun onScoreChanged(score: Number)
        fun disablePause()
    }

    private val mBitmap: Bitmap = Bitmap.createBitmap(
        iv.width,
        iv.height,
        Bitmap.Config.ARGB_8888
    )
    protected val mCanvas = Canvas(mBitmap)
    protected val strokePaint = Paint()
    protected val fillPaint = Paint()

    init {
        iv.setImageBitmap(mBitmap)
    }

    /**
     * initiate the colors and styles of the
     */
    open fun initiatePaints(){
        strokePaint.color = Color.BLACK
        strokePaint.style = Paint.Style.STROKE
        fillPaint.color = Color.BLACK
    }

    /**
     * Resets the canvas' background and redraw its lane separator
     */
    protected abstract fun clearCanvas()

    /**
     * Resets the canvas and then redraw all the elements (tiles)
     */
    abstract fun redraw(drawers: ArrayList<TileDrawer>)

    abstract fun startGame()
    abstract fun stopGame()
    abstract fun pauseGame()
    abstract fun resumeGame()
    abstract fun isStopped(): Boolean

    abstract fun getScore(): Number
}