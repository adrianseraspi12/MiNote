package com.suzei.minote.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.suzei.minote.Injection
import com.suzei.minote.R
import com.suzei.minote.ui.settings.SettingsActivity

import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class ListActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "ListActivity"

        private const val FRAGMENT_TAG = "LIST_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setTitle(R.string.all_notes)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        val fm = supportFragmentManager
        var listFragment = fm.findFragmentByTag(FRAGMENT_TAG) as ListFragment?

        if (listFragment == null) {
            listFragment = ListFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.list_container, listFragment, FRAGMENT_TAG)
                    .commit()

            ListPresenter(
                    Injection.provideDataSourceImpl(applicationContext),
                    listFragment)

            Log.i(TAG, "onCreate: ListFragment is null")
        } else {
            Log.i(TAG, "onCreate: ListFragment is not null")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_settings) {
            startActivity(Intent(
                    this@ListActivity,
                    SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

}
