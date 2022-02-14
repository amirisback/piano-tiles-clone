package com.github.frostygum.view

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.frostygum.R
import com.github.frostygum.databinding.FragmentGamePausedBinding
import com.github.frostygum.presenter.IMainPresenter
import java.lang.ClassCastException

class FragmentGamePause: Fragment(R.layout.fragment_game_paused) {
    private lateinit var binding: FragmentGamePausedBinding
    private lateinit var listener: IMainActivity
    private lateinit var presenter: IMainPresenter

    private lateinit var timer: StartTimer

    companion object {
        fun newInstance(presenter: IMainPresenter): FragmentGamePause {
            val fragment = FragmentGamePause()
            fragment.presenter = presenter
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentGamePausedBinding.inflate(inflater, container, false)

        //Initialize Timer
        this.timer = StartTimer(3000, 1000)

        this.binding.btnStart.setOnClickListener {
            this.timer.start()
        }

        return this.binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is IMainActivity) {
            this.listener = context
        }
        else {
            throw ClassCastException("$context must implement FragmentListener")
        }
    }

    private inner class StartTimer(startTime: Long, interval: Long) : CountDownTimer(startTime, interval) {
        override fun onFinish() {
            if(!presenter.isThreadHasRunning()) {
                listener.changePage("GAME")
                binding.tvTimer.text = ""
            }
        }

        override fun onTick(millisUntilFinished: Long) {
            when (millisUntilFinished) {
                in 2001..2999 -> {
                    binding.tvTimer.text = "3"
                }
                in 1001..1999 -> {
                    binding.tvTimer.text = "2"
                }
                else -> {
                    binding.tvTimer.text = "1"
                }
            }
        }
    }
}