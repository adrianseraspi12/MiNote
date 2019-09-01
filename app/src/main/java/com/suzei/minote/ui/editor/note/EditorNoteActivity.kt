package com.suzei.minote.ui.editor.note

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.suzei.minote.Injection
import com.suzei.minote.R
import kotlinx.android.synthetic.main.activity_editor.*

class EditorNoteActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"

        const val FRAGMENT_EDITOR_NOTE_TAG = "EDITOR_NOTE_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val itemId = intent.getStringExtra(EXTRA_NOTE_ID)

        val fm = supportFragmentManager

        var editorFragment = fm.findFragmentByTag(FRAGMENT_EDITOR_NOTE_TAG) as EditorNoteFragment?

        if (editorFragment == null) {

            editorFragment = EditorNoteFragment.newInstance()

            fm.beginTransaction()
                    .replace(R.id.editor_container, editorFragment, FRAGMENT_EDITOR_NOTE_TAG)
                    .commit()

            if (itemId != null) {
                EditorNotePresenter(itemId,
                        Injection.provideDataSourceImpl(applicationContext),
                        editorFragment)
            } else {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                EditorNotePresenter(prefs,
                        Injection.provideDataSourceImpl(applicationContext),
                        editorFragment)
            }

        }

        adView.adListener = adListener

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private val adListener = object: AdListener() {

        override fun onAdLoaded() {
            super.onAdLoaded()
            adView.visibility = View.VISIBLE
        }

        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            adView.visibility = View.GONE
        }

    }

}