package com.github.jghjianghan.view.engines

abstract class Tile(
    val width: Float,
    val height: Float,
    val cx: Float,
) {
    /**
     * ask the tile to drop itself
     * @return true if the tile successfully dropped, false if tile refuse to drop
     */
    abstract fun isTileTouched(x: Float, y: Float): Boolean
    abstract fun drop(dy: Float)
    abstract fun lift(dy: Float)
    abstract fun getDrawer(): TileDrawer
    abstract fun onClick()
    abstract fun onMissed()
}