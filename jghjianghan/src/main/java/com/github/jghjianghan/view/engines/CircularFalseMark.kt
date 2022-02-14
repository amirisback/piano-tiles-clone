package com.github.jghjianghan.view.engines

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class CircularFalseMark(
    val cx: Float,
    val cy: Float,
    val radius: Float,
) {
    var color = Color.RED
    val paint = Paint()
    init {
        paint.color = color
    }

    inner class Drawer: TileDrawer(){
        override fun drawTile(canvas: Canvas) {
            canvas.drawCircle(cx, cy, radius, paint)
        }
    }
}