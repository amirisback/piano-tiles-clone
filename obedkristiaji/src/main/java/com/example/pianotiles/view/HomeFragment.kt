package com.example.pianotiles.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pianotiles.databinding.FragmentHomeBinding
import com.example.pianotiles.presenter.IMainPresenter


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var listener: IMainActivity
    private lateinit var presenter: IMainPresenter

    init {

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentHomeBinding.inflate(inflater, container, false)

        this.init()

        this.binding.btnBack.setOnClickListener {
            this.listener.changePage(2)
        }

        this.binding.btnSetting.setOnClickListener {
            this.listener.showSetting()
        }

        this.binding.btnExit.setOnClickListener {
            this.listener.closeApplication()
        }

        return this.binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is IMainActivity) {
            this.listener = context as IMainActivity
        } else {
            throw ClassCastException(context.toString()
                    + " must implement FragmentListener")
        }
    }

    companion object {
        fun newInstance(presenter: IMainPresenter): HomeFragment {
            val fragment: HomeFragment = HomeFragment()
            fragment.presenter = presenter
            return fragment
        }
    }

    fun setScore(score: Int) {
        this.binding.tvScore.setText("High Score: " + score.toString())
    }

    private fun init() {
        this.setScore(this.presenter.getHS())
        this.presenter.setStartHome(true)
    }
}