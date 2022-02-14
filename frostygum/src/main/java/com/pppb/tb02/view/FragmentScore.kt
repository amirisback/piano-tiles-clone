package com.pppb.tb02.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pppb.tb02.R
import com.pppb.tb02.databinding.FragmentScoreBinding
import com.pppb.tb02.presenter.IMainPresenter
import java.lang.ClassCastException

class FragmentScore() : Fragment(R.layout.fragment_score) {
    private lateinit var binding: FragmentScoreBinding
    private lateinit var listener: IMainActivity
    private lateinit var presenter: IMainPresenter
    private  lateinit var adapter: ScoreListAdapter

    companion object {
        fun newInstance(presenter: IMainPresenter, adapter: ScoreListAdapter): FragmentScore {
            val fragment = FragmentScore()
            fragment.presenter = presenter
            fragment.adapter = adapter
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentScoreBinding.inflate(inflater, container, false)

        this.binding.lstMenu.adapter = adapter

        this.binding.btnStart.setOnClickListener {
            this.presenter.resetGame()
            this.listener.changePage("GAME")
        }

        return binding.root
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