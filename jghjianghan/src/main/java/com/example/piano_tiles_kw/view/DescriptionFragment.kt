package com.example.piano_tiles_kw.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.piano_tiles_kw.R
import com.example.piano_tiles_kw.databinding.FragmentDescriptionBinding
import com.example.piano_tiles_kw.model.GameMode
import com.example.piano_tiles_kw.model.Page
import com.example.piano_tiles_kw.viewmodel.MainVM

// Contains the description of each game modes before the game starts

class DescriptionFragment : Fragment(),
    View.OnClickListener{
    private lateinit var listener: FragmentListener
    private lateinit var binding : FragmentDescriptionBinding
    private lateinit var vm : MainVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)

        binding.btnStart.setOnClickListener(this)

        binding.btnCancel.setOnClickListener(this)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(MainVM::class.java)
        vm.getGameMode().observe(viewLifecycleOwner) {
            when (it) {
                GameMode.CLASSIC -> {
                    binding.tvDescription.text = getString(R.string.desc_classic)
                }
                GameMode.ARCADE -> {
                    binding.tvDescription.text = getString(R.string.desc_arcade)
                }
                GameMode.TILT -> {
                    binding.tvDescription.text = getString(R.string.desc_tilt)
                }
                GameMode.RAINING -> {
                    binding.tvDescription.text = getString(R.string.desc_raining)
                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            listener = context
        } else {
            throw ClassCastException(
                context.toString() +
                        "must implement FragmentListener"
            )
        }
    }

    override fun onClick(v: View) {
        if (binding.btnStart == v) {
            listener.changePage(Page.GAMEPLAY)
        } else if (binding.btnCancel == v) {
            listener.changePage(Page.MENU)
        }
    }
}