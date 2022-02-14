package com.example.piano_tiles_kw.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.piano_tiles_kw.R
import com.example.piano_tiles_kw.databinding.FragmentMenuBinding
import com.example.piano_tiles_kw.model.GameMode
import com.example.piano_tiles_kw.model.Page
import com.example.piano_tiles_kw.viewmodel.MainVM

// Contains the main menu page to select game modes

class MenuFragment : Fragment(),
    View.OnClickListener {
    private lateinit var listener: FragmentListener
    private lateinit var binding: FragmentMenuBinding
    private lateinit var vm: MainVM;

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(MainVM::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuBinding.inflate(
            inflater,
            container,
            false
        )

        binding.btnClassic.setOnClickListener(this)
        binding.btnArcade.setOnClickListener(this)
        binding.btnRaining.setOnClickListener(this)
        binding.btnTilt.setOnClickListener(this)
        binding.btnHighscore.setOnClickListener(this)
        binding.btnExit.setOnClickListener(this)
        return binding.root
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
        when (v) {
            binding.btnClassic -> {
                listener.changePage(Page.DESCRIPTION)
                vm.setGameMode(GameMode.CLASSIC)
            }
            binding.btnArcade -> {
                listener.changePage(Page.DESCRIPTION)
                vm.setGameMode(GameMode.ARCADE)
            }
            binding.btnTilt -> {
                listener.changePage(Page.DESCRIPTION)
                vm.setGameMode(GameMode.TILT)
            }
            binding.btnRaining -> {
                listener.changePage(Page.DESCRIPTION)
                vm.setGameMode(GameMode.RAINING)
            }
            binding.btnHighscore -> listener.changePage(Page.HIGHSCORE)
            binding.btnExit -> {
                listener.closeApplication()
            }
        }
    }
}