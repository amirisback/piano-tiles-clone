package com.github.atillaturkmen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            val editText = findViewById<EditText>(R.id.editText)
            val musicBox = findViewById<CheckBox>(R.id.checkBox)
            val vibrationBox = findViewById<CheckBox>(R.id.checkBox2)

            val speed = editText.text.toString()
            val music = musicBox.isChecked
            val vibration = vibrationBox.isChecked

            if (speed == "") {
                Toast.makeText(this, "You have to select a speed", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("speed", speed)
                    putExtra("music", music)
                    putExtra("vibration", vibration)
                }
                startActivity(intent)
            }
        }
    }

}
