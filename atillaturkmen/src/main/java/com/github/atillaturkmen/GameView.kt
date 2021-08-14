package com.github.atillaturkmen

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.LinkedList
import java.util.concurrent.CopyOnWriteArrayList


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val thread: GameThread

    private var tiles = LinkedList<Tile>()
    private var tempTiles = CopyOnWriteArrayList<Tile>()

    private var vibrator: Vibrator? = null

    private var blackPaint = Paint()
    private var grayPaint = Paint()
    private var redPaint = Paint()
    private var scorePaint = Paint()

    private var row = -1
    private var lastRow = -1

    private var gameOver = false
    private var tappedWrongTile = -1
    private var startY = -1
    private var endY = -1

    private var touchedX = 0f
    private var touchedY = 0f

    private var scoreSize = 100f
    private var backGroundColor = Color.WHITE

    private var started = false

    private var soundPool: SoundPool? = null
    private var failSound: Int? = null
    private var tileSound: Int? = null

    init {

        // add callback
        holder.addCallback(this)

        // instantiate the game thread
        thread = GameThread(holder, this)

        score = 0

        row = (0..3).random()

        //game objects
        tiles.add(Tile(blackPaint, grayPaint, redPaint, row))

        lastRow = row

        // color of the tiles
        blackPaint.color = Color.BLACK
        grayPaint.color = Color.GRAY
        redPaint.color = Color.RED
        scorePaint.color = Color.CYAN

        scorePaint.textSize = scoreSize

        if (music && soundPool == null) {
            soundPool = SoundPool(20, AudioManager.STREAM_MUSIC, 0)
            if (failSound == null) {
                failSound = soundPool?.load(context, R.raw.failsound, 1)
            }

            if (tileSound == null) {
                tileSound = soundPool?.load(context, R.raw.a, 1)
            }
        }

        if (vibration) {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    companion object {
        var score = 0
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        var music = true
        var vibration = true
    }


    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        Log.d("ati, ","created")
        thread.setRunning(true)
        if (!started) {
            // start the game thread
            Log.d("ati, ","started")
            thread.start()
            started = true
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        Log.d("ati, ","changed")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d("ati, ","destroyed")
        thread.setRunning(false)
    }

    fun destroy() {
        soundPool?.release()
        soundPool = null
    }

    /** Everything that has to be drawn on Canvas */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // stop the game
        if (gameOver) {
            soundPool?.play(failSound!!, 1f, 1f,0,0, 1f)
            Tile.speed = 0
            thread.setRunning(false)
        }

        drawLines(canvas)

        // remove the tiles that are out of screen from tiles list
        if (tiles.first.outOfScreen) {
            tiles.poll()
        }
        // draw new tiles when last one is on the screen
        if (tiles.last.startY >= 0) {
            do {
                row = (0..3).random()
            } while (row == lastRow)

            tiles.add(Tile(blackPaint, grayPaint, redPaint, row))

            lastRow = row
        }
        // update and draw all tiles
        for (tile in tiles) {
            tile.update()
            tile.draw(canvas)
            if (tile.gameOver) {
                gameOver = true
            }
        }
        // draw red tile if pressed the wrong tile
        when (tappedWrongTile) {
            0 -> canvas.drawRect(Rect(0, startY, screenWidth/4, endY), redPaint)
            1 -> canvas.drawRect(Rect(screenWidth/4, startY, screenWidth/2, endY), redPaint)
            2 -> canvas.drawRect(Rect(screenWidth/2, startY, screenWidth*3/4, endY), redPaint)
            3 -> canvas.drawRect(Rect(screenWidth*3/4, startY, screenWidth, endY), redPaint)
        }
        drawScore(canvas)
    }

    fun drawLines(canvas: Canvas) {
        // paint the background
        canvas.drawColor(backGroundColor)

        // alignment lines
        canvas.drawLine(screenWidth.toFloat()/4, 0f, screenWidth.toFloat()/4, screenHeight.toFloat(), blackPaint)
        canvas.drawLine(screenWidth.toFloat()/2, 0f, screenWidth.toFloat()/2, screenHeight.toFloat(), blackPaint)
        canvas.drawLine(3*screenWidth.toFloat()/4, 0f, 3*screenWidth.toFloat()/4, screenHeight.toFloat(), blackPaint)
    }

    fun drawScore(canvas: Canvas) {
        // refresh score
        canvas.drawText(score.toString(), screenWidth/2 - scoreSize/2, scoreSize, scorePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        event.actionMasked.let { action ->
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                event.actionIndex.let { index ->
                    if (Tile.speed > 0) {
                        touchedX = event.getX(index)
                        touchedY = event.getY(index)
                        tempTiles = CopyOnWriteArrayList(tiles)
                        for (tile in tempTiles) {
                            if (tile.checkTouch(touchedX, touchedY)) {
                                soundPool?.play(tileSound!!, 1f, 1f,0,0, 1f)
                                if (Build.VERSION.SDK_INT >= 26) {
                                    vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    vibrator?.vibrate(40)
                                }
                                break
                            }
                            else if(!tile.pressed && touchedY < tile.endY && touchedY > tile.startY) {
                                // pressed wrong place
                                tappedWrongTile = when {
                                    (touchedX < screenWidth/4) -> 0
                                    (touchedX < screenWidth/2) -> 1
                                    (touchedX < 3*screenWidth/4) -> 2
                                    else -> 3
                                }
                                startY = tile.startY
                                endY = tile.endY
                                gameOver = true
                            }
                        }
                    }
                }
            }
        }
        return true
    }
}