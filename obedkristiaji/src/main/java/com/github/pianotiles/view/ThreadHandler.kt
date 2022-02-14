package com.github.pianotiles.view

import android.os.Handler
import android.os.Message
import com.github.pianotiles.model.Piano
import com.github.pianotiles.presenter.IMainPresenter

class ThreadHandler(private val presenter: IMainPresenter): Handler() {
    private val MSG_SET_RECT: Int = 0
    private val MSG_RESET_THREAD: Int = 1
    private val MSG_SET_POS: Int = 2
    private val MSG_DELETE_RECT: Int = 3
    private val MSG_SET_PLAY: Int = 4

    override fun handleMessage(msg: Message) {
        when(msg.what) {
            this.MSG_SET_RECT -> {
                val attr: Pair<Piano, Int> = msg.obj as Pair<Piano, Int>
                this.presenter.setRect(attr.first, attr.second)
            }
            this.MSG_RESET_THREAD -> {
                this.presenter.resetThread()
            }
            this.MSG_SET_POS -> {
                val attr: Pair<Piano, Int> = msg.obj as Pair<Piano, Int>
                this.presenter.setPiano(attr.first, attr.second)
            }
            this.MSG_DELETE_RECT -> {
                val piano: Piano = msg.obj as Piano
                this.presenter.deleteRect(piano)
            }
            this.MSG_SET_PLAY -> {
                val score: Int = msg.obj as Int
                this.presenter.lose(score)
            }
        }
    }

    fun setRect(attr: Pair<Piano, Int>) {
        val msg = Message()
        msg.what = MSG_SET_RECT
        msg.obj = attr

        this.sendMessage(msg)
    }

    fun stopThread() {
        val msg = Message()
        msg.what = MSG_RESET_THREAD

        this.sendMessage(msg)
    }

    fun setPos(attr: Pair<Piano, Int>) {
        val msg = Message()
        msg.what = MSG_SET_POS
        msg.obj = attr

        this.sendMessage(msg)
    }

    fun deleteRect(piano: Piano) {
        val msg = Message()
        msg.what = MSG_DELETE_RECT
        msg.obj = piano

        this.sendMessage(msg)
    }

    fun lose(score: Int) {
        val msg = Message()
        msg.what = MSG_SET_PLAY
        msg.obj = score

        this.sendMessage(msg)
    }
}