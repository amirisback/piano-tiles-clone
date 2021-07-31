package com.frogobox.pianotilesclone

import com.frogobox.pianotilesclone.BaseActivity
import android.widget.ArrayAdapter
import com.example.myapplication.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.myapplication.databinding.ActivityScoreBinding
import java.util.ArrayList

class ScoreActivity : BaseActivity<ActivityScoreBinding>() {
    private lateinit var arr1: Array<String>
    private lateinit var arr2: Array<String>
    private lateinit var arr3: Array<String>
    
    override fun setupViewBinding(): ActivityScoreBinding {
        return ActivityScoreBinding.inflate(layoutInflater)
    }

    override fun setupUI(savedInstanceState: Bundle?) {
        val arrayData = ArrayList<String>()
        arrayData.add("Easy")
        arrayData.add("Medium")
        arrayData.add("Hard")
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_item, R.id.spinnerText, arrayData)
        binding.spinner.adapter = arrayAdapter
        val i = intent
        arr1 = i.getStringArrayExtra("easy") as Array<String>
        arr2 = i.getStringArrayExtra("medium") as Array<String>
        arr3 = i.getStringArrayExtra("hard") as Array<String>
    }

    fun displayScore(view: View) {
        val toShow = ArrayList<String?>()
        when {
            binding.spinner.selectedItem.toString().contains("Easy") -> {
                for (s in arr1) {
                    if (!s.split("\n").toTypedArray()[1].contains("-1")) toShow.add(
                        s.split("\n").toTypedArray()[0] + " : " + s.split("\n").toTypedArray()[1]
                    )
                }
            }
            binding.spinner.selectedItem.toString().contains("Medium") -> {
                for (s in arr2) {
                    if (!s.split("\n").toTypedArray()[1].contains("-1")) toShow.add(
                        s.split("\n").toTypedArray()[0] + " : " + s.split("\n").toTypedArray()[1]
                    )
                }
            }
            else -> {
                for (s in arr3) {
                    if (!s.split("\n").toTypedArray()[1].contains("-1")) toShow.add(
                        s.split("\n").toTypedArray()[0] + " : " + s.split("\n").toTypedArray()[1]
                    )
                }
            }
        }
        val arrayAdapter: ArrayAdapter<String?> =
            ArrayAdapter<String?>(this, R.layout.score_item, R.id.scoreText, toShow)
        binding.scoreList.adapter = arrayAdapter
    }
}