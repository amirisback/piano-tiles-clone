package com.example.piano_tiles_kw.view

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.piano_tiles_kw.R
import com.example.piano_tiles_kw.databinding.FragmentHighscoreBinding
import com.example.piano_tiles_kw.model.GameMode
import com.example.piano_tiles_kw.model.Page
import com.example.piano_tiles_kw.viewmodel.MainVM


// Contains the description of each game modes before the game starts

class HighscoreFragment : Fragment() {
    private lateinit var listener: FragmentListener
    private lateinit var binding: FragmentHighscoreBinding
    private lateinit var vm: MainVM
    private lateinit var adapter: HighscoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHighscoreBinding.inflate(inflater, container, false)
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to reset all the high scores??")
                .setPositiveButton(
                    R.string.btn_highscore_reset
                ) { _, _ ->
                    for (mode in GameMode.values()) {
                        if (mode == GameMode.CLASSIC) {
                            listener.updateHighscore(Float.MAX_VALUE, mode)
                        } else {
                            listener.updateHighscore(0, mode)
                        }
                    }
                }
                .setNegativeButton(R.string.btn_cancel_string, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

        }
        binding.btnBack.setOnClickListener {
            listener.changePage(Page.MENU)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm = ViewModelProvider(requireActivity()).get(MainVM::class.java)
        adapter = HighscoreAdapter(requireActivity(), vm, this)

        binding.lvHighscore.adapter = adapter
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
}