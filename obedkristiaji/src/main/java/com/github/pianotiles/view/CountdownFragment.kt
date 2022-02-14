package com.github.pianotiles.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.pianotiles.databinding.FragmentCountdownBinding
import com.github.pianotiles.presenter.IMainPresenter

class CountdownFragment : DialogFragment() {
    private lateinit var binding: FragmentCountdownBinding
    private lateinit var listener: IMainActivity
    private lateinit var presenter: IMainPresenter
    private var counter = 3

    init {

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentCountdownBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCountdown.text = counter.toString()
                counter--
            }
            override fun onFinish() {
                counter = 3
                dismiss()
                presenter.setPlay(true)
                listener.startThread()
            }
        }.start()

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
        fun newInstance(presenter: IMainPresenter): CountdownFragment {
            val fragment: CountdownFragment = CountdownFragment()
            fragment.presenter = presenter
            return fragment
        }
    }
}