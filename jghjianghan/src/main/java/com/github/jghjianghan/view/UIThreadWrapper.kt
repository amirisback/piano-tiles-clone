package com.github.jghjianghan.view

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.github.jghjianghan.view.engines.GameEngine
import com.github.jghjianghan.view.engines.TileDrawer
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class UIThreadWrapper(
    private val engine: GameEngine,
    looper: Looper,
    private val gameplayFragment: GameEngine.GameListener
): Handler(looper) {
    companion object {
        private const val REDRAW_CANVAS = 1
        private const val STOP_GAME = 2
        private const val DISABLE_PAUSE = 3
    }

    override fun handleMessage(msg: Message) {
        when(msg.what){
            REDRAW_CANVAS -> engine.redraw(msg.obj as ArrayList<TileDrawer>)
            STOP_GAME -> engine.stopGame()
            DISABLE_PAUSE -> gameplayFragment.disablePause()
        }
    }
    fun redrawCanvas(drawers: ArrayList<TileDrawer>){
        val msg = Message()
        msg.what = REDRAW_CANVAS
        msg.obj = drawers
        this.sendMessage(msg)
    }
    fun stopGame(){
        val msg = Message()
        msg.what = STOP_GAME
        this.sendMessage(msg)
    }

    fun disablePause() {
        val msg = Message()
        msg.what = DISABLE_PAUSE
        this.sendMessage(msg)
    }
}
