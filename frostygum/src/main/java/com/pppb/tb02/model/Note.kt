package com.pppb.tb02.model

class Note(var top: Int, var tilePos: Int) {
    val length: Int = 500
    var bottom: Int = top + length
    var isHidden = false
    var isClicked = false
    var isLoser = false
    var isBonus = false

    fun set(top: Int) {
        this.top = top
        this.bottom = top + length
    }

    fun lose() {
        this.isLoser = true
    }

    fun hide() {
        this.isHidden = true
    }

    fun clicked() {
        this.isClicked = true
    }

    fun setBonus() {
        this.isBonus = true
    }

    override fun toString(): String {
        return "[TOP: $top, BOTTOM: $bottom, IS_HIDDEN?: $isHidden, tile: $tilePos]"
    }
}