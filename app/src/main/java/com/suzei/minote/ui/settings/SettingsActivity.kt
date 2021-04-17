package com.suzei.minote.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suzei.minote.R

class SettingsActivity : AppCompatActivity() {

    private val fragment = SettingsFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)
        initPresenter()
        setupFragment()
    }

    private fun initPresenter() {
        SettingsPresenter(
                fragment,
                getPreferences(Context.MODE_PRIVATE)
        )
    }

    private fun setupFragment() {
        val fm = supportFragmentManager
        fm.beginTransaction().replace(R.id.base_container, fragment).commit()
    }

}