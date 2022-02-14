package com.example.pianotiles.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.pianotiles.R
import com.example.pianotiles.databinding.FragmentSettingBinding
import com.example.pianotiles.presenter.IMainPresenter

class SettingFragment : DialogFragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var listener: IMainActivity
    private lateinit var presenter: IMainPresenter

    init {

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentSettingBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        this.binding.btnRed.setOnClickListener {
            this.presenter.setColor(R.color.red)
            Toast.makeText(context, "Background : Red", Toast.LENGTH_SHORT).show()
        }
        this.binding.btnGreen.setOnClickListener {
            this.presenter.setColor(R.color.green)
            Toast.makeText(context, "Background : Green", Toast.LENGTH_SHORT).show()
        }
        this.binding.btnBlue.setOnClickListener {
            this.presenter.setColor(R.color.blue)
            Toast.makeText(context, "Background : Blue", Toast.LENGTH_SHORT).show()
        }
        this.binding.btnNormal.setOnClickListener {
            this.presenter.setLevel(1)
            Toast.makeText(context, "Difficulty : Normal", Toast.LENGTH_SHORT).show()
        }
        this.binding.btnHard.setOnClickListener {
            this.presenter.setLevel(2)
            Toast.makeText(context, "Difficulty : Hard", Toast.LENGTH_SHORT).show()
        }
        this.binding.btnBack.setOnClickListener {
            dismiss()
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
        fun newInstance(presenter: IMainPresenter): SettingFragment {
            val fragment: SettingFragment = SettingFragment()
            fragment.presenter = presenter
            return fragment
        }
    }
}