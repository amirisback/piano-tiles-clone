package com.pppb.tb02.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pppb.tb02.R
import com.pppb.tb02.databinding.FragmentGameLoseBinding
import com.pppb.tb02.presenter.IMainPresenter
import java.lang.ClassCastException

class FragmentGameLose: Fragment(R.layout.fragment_game_paused) {
    private lateinit var binding: FragmentGameLoseBinding
    private lateinit var listener: IMainActivity
    private lateinit var presenter: IMainPresenter
    private var score: Int = 0
    private var level: String = "1"

    companion object {
        fun newInstance(presenter: IMainPresenter): FragmentGameLose {
            val fragment = FragmentGameLose()
            fragment.presenter = presenter
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentGameLoseBinding.inflate(inflater, container, false)

        this.binding.btnStart.setOnClickListener {
            this.presenter.resetGame()
            this.listener.changePage("GAME")
        }

        this.binding.btnScore.setOnClickListener {
            this.listener.changePage("SCORE")
        }

        return this.binding.root
    }

    fun setFinalLevel(level: String) {
        this.level = level
    }

    fun setFinalScore(score: Int) {
        this.score = score
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.binding.tvScore.text = this.score.toString()
        this.binding.tvLevel.text = this.level.toString()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            this.binding.tvScore.text = this.score.toString()
            this.binding.tvLevel.text = this.level.toString()
        }
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
}