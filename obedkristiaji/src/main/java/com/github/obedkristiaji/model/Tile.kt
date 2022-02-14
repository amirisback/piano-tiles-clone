package com.github.obedkristiaji.model

import android.graphics.Rect

class Tile {
    var tile: MutableList<Rect> = mutableListOf()

    fun addTile(rect: Rect) {
        this.tile.add(rect)
    }

    fun click(position: Int) {
        this.tile.removeAt(position)
    }

    fun getRect(position: Int): Rect {
        return this.tile[position]
    }

    fun getSize(): Int {
        return this.tile.size
    }

    fun setRect(position: Int, rect: Rect) {
        this.tile[position].set(rect)
    }
}