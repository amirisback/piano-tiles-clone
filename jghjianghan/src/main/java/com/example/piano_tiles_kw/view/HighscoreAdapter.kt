package com.example.piano_tiles_kw.view

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.LifecycleOwner
import com.example.piano_tiles_kw.R
import com.example.piano_tiles_kw.databinding.FragmentHighscoreBinding
import com.example.piano_tiles_kw.databinding.HighscoreItemBinding
import com.example.piano_tiles_kw.model.GameMode
import com.example.piano_tiles_kw.viewmodel.MainVM

class HighscoreAdapter(
    private val activity: Activity,
    private val vm: MainVM,
    private val owner: LifecycleOwner
): BaseAdapter() {

    override fun getCount(): Int = 4

    override fun getItem(position: Int): Any {
        return when(position){
            0 -> GameMode.CLASSIC
            1 -> GameMode.ARCADE
            2 -> GameMode.RAINING
            else -> GameMode.TILT
        }
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(this.activity).inflate(R.layout.highscore_item, parent, false)
        val binding = HighscoreItemBinding.bind(view)

        when(this.getItem(position) as GameMode){
            GameMode.CLASSIC -> {
                binding.highschoreLabel.text = activity.getString(R.string.btn_classic_string) + ":"
                vm.getClassicHighScore().observe(owner, {
                    binding.tvHighscore.text = if (it==Float.MAX_VALUE) {"-"} else {it.toString()}
                })
            }
            GameMode.ARCADE -> {
                binding.highschoreLabel.text = activity.getString(R.string.btn_arcade_string) + ":"
                vm.getArcadeHighScore().observe(owner, {
                    binding.tvHighscore.text = it.toString()
                })
            }
            GameMode.RAINING -> {
                binding.highschoreLabel.text = activity.getString(R.string.btn_raining_string) + ":"
                vm.getRainingHighScore().observe(owner, {
                    binding.tvHighscore.text = it.toString()
                })
            }
            GameMode.TILT -> {
                binding.highschoreLabel.text = activity.getString(R.string.btn_tilt_string) + ":"
                vm.getTiltHighScore().observe(owner, {
                    binding.tvHighscore.text = it.toString()
                })
            }
        }

        return view
    }
}