package com.suzei.minote.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsFragment = SettingsFragment.newInstance()

        supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit()

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        SettingsPresenter(sharedPrefs, settingsFragment)

        title = "Settings"
    }
}
