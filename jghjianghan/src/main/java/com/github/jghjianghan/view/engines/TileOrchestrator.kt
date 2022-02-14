package com.github.jghjianghan.view.engines

import com.github.jghjianghan.model.audio.PianoPlayer

abstract class TileOrchestrator(
    protected val laneCenters : ArrayList<Float>,
    protected var dropSpeed: Float,
    protected val pianoPlayer: PianoPlayer? = null
) {


    abstract fun handleTouch(x: Float, y: Float)
    abstract fun getScore(): Number
}