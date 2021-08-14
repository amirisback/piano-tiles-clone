package com.github.atillaturkmen

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.github.atillaturkmen.GameView.Companion.screenWidth
import com.github.atillaturkmen.GameView.Companion.screenHeight

/**
 * Tile Class.
 * It goes from top to bottom
 * Purpose of the game is to press the tile
 */

class Tile(blackPaint : Paint, private var pressedTileColor: Paint, private var redPaint: Paint, row : Int) {

    companion object {
        var speed = 30
    }

    private var startX: Int = 0
    var startY: Int = 0
    private var endX: Int = 0
    var endY: Int = 0

    var pressed: Boolean = false

    var outOfScreen = false
    private var outOfBounds = false
    var gameOver = false

    private var tileColor = blackPaint


    init {
        startX = row * (screenWidth/4)
        startY = -screenHeight/4
        endX = screenWidth/4 + startX
        endY = screenHeight/4 + startY
    }

    /**
     * Draws the object on to the canvas.
     */
    fun draw(canvas: Canvas) {
        canvas.drawRect(Rect(startX, startY, endX, endY), tileColor)
    }

    /**
     * update properties for the game object
     */
    fun update() {

        //stop the tile if it reaches the end
        if (startY >= screenHeight && !pressed) {
            tileColor = redPaint
            outOfBounds = true
            speed = -40
        }
        if (outOfBounds && endY <= screenHeight) {
            gameOver = true
        }
        if (startY >= screenHeight && pressed) {
            outOfScreen = true
        }
        startY += (speed)
        endY += (speed)

    }

    fun checkTouch (x: Float, y: Float) : Boolean {
        if (x > startX - screenWidth/30 && x < endX + screenWidth/30 && y < endY && y > startY && !pressed) {
            tileColor = pressedTileColor
            GameView.score++
            pressed = true
            return pressed
        }
        return false
    }

}