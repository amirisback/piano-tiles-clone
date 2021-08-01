package com.frogobox.pianotilesclone

import com.frogobox.pianotilesclone.BaseActivity
import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.frogobox.pianotilesclone.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun setupViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun setupUI(savedInstanceState: Bundle?) {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.action_bar_layout)

        binding.btnPlay.setOnClickListener {
            val i = Intent(this, GameActivity::class.java)
            startActivity(i)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.maine_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_setting) {
            startActivity(Intent(this, MyPreferences::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

}