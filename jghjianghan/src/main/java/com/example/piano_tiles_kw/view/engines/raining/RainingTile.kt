package com.example.piano_tiles_kw.view.engines.raining

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.piano_tiles_kw.view.engines.NormalTile
import com.example.piano_tiles_kw.view.engines.Tile
import com.example.piano_tiles_kw.view.engines.TileDrawer

/**
 * Tile for Raining mode
 * Tile drops according to its internal speed,, disappear on click
 * The anchor point is at the center-top of the tile
 */
class RainingTile(
    width: Float,
    height: Float,
    cx: Float,
    private val envHeight: Float,
    val speed: Float,
    color: Int
) : Tile(width, height, cx) {
    var paint = Paint()
    var cy = 0 - height
    @Volatile
    var isOut = false
    @Volatile
    var isClickable = true
    var dimColor = Color.GRAY
    var missedColor = Color.RED

    init {
        paint.color = color
    }

    @Deprecated(
        "This Tile will drop itself according to its own speed " +
        "assigned in constructor and will ignore the passed arguments",
        ReplaceWith("drop()"))
    override fun drop(dy: Float) {
        drop()
    }
    /**
     * Drops the tile according to its assign speed (in the constructor)
     * @return true if tile is dropped, false otherwise
     */
    fun drop(){
        cy += speed
        if (cy > envHeight) {
            isOut = true
        }
    }

    override fun lift(dy: Float) {
        cy -= dy
    }

    override fun isTileTouched(x: Float, y: Float): Boolean {
        return x >= cx - width / 2 && x <= cx + width / 2 && y >= cy && y <= cy + height
    }

    override fun onClick() {
        isClickable = false
        paint.color = dimColor
    }

    override fun onMissed() {
        isClickable = false
        paint.color = missedColor
    }

    override fun getDrawer(): TileDrawer = Drawer()

    /**
     * A class that draws this particular tile on the canvas
     * Will be passed to the engine to draw in the UI Thread
     */
    inner class Drawer : TileDrawer() {
        override fun drawTile(canvas: Canvas) {
            canvas.drawRect(cx - width / 2, cy, cx + width / 2, cy + height, paint)
        }
    }
}