package com.example.piano_tiles_kw.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.piano_tiles_kw.databinding.FragmentResultBinding
import com.example.piano_tiles_kw.model.GameMode
import com.example.piano_tiles_kw.model.Page
import com.example.piano_tiles_kw.viewmodel.MainVM

// Contains the result page after the game is over

class ResultFragment : Fragment(),
    View.OnClickListener{
    private lateinit var listener: FragmentListener
    private lateinit var binding : FragmentResultBinding
    private lateinit var vm : MainVM

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(MainVM::class.java)

        vm.getGameMode().observe(viewLifecycleOwner, {
            when(it){
                GameMode.CLASSIC -> {
                    vm.getClassicHighScore().observe(viewLifecycleOwner, {
                        binding.tvHighscoreValueRes.text = if (it==Float.MAX_VALUE){"-"} else {it.toString()}
                    })

                    vm.getClassicScore().observe(viewLifecycleOwner, {
                        binding.tvScoreValueRes.text = if (it==Float.MAX_VALUE){"-"} else {it.toString()}
                    })
                }
                GameMode.ARCADE -> {
                    vm.getArcadeHighScore().observe(viewLifecycleOwner, {
                        binding.tvHighscoreValueRes.text = it.toString()
                    })

                    vm.getArcadeScore().observe(viewLifecycleOwner, {
                        binding.tvScoreValueRes.text = it.toString()
                    })
                }
                GameMode.RAINING -> {
                    vm.getRainingHighScore().observe(viewLifecycleOwner, {
                        Log.d("raining highscore", it.toString())
                        binding.tvHighscoreValueRes.text = it.toString()
                    })

                    vm.getRainingScore().observe(viewLifecycleOwner, {
                        Log.d("raining score", it.toString())
                        binding.tvScoreValueRes.text = it.toString()
                    })
                }
                GameMode.TILT -> {
                    vm.getTiltHighScore().observe(viewLifecycleOwner, {
                        binding.tvHighscoreValueRes.text = it.toString()
                    })

                    vm.getTiltScore().observe(viewLifecycleOwner, {
                        binding.tvScoreValueRes.text = it.toString()
                    })
                }
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBinding.inflate(inflater, container, false)

        binding.btnAgain.setOnClickListener(this)
        binding.btnMenu.setOnClickListener(this)

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
        if (binding.btnAgain == v) {
            listener.changePage(Page.GAMEPLAY)
        } else if (binding.btnMenu == v) {
            listener.changePage(Page.MENU)
        }
    }
}