package com.suzei.minote.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suzei.minote.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)
        setupFragment()
    }

    private fun setupFragment() {
        val fragment = SettingsFragment.newInstance()
        val fm = supportFragmentManager
        fm.beginTransaction().replace(R.id.base_container, fragment).commit()
    }

}