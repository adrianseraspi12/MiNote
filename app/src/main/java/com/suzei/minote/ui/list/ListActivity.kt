package com.suzei.minote.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.suzei.minote.Injection
import com.suzei.minote.R
import com.suzei.minote.ui.editor.note.EditorNoteActivity
import com.suzei.minote.ui.editor.todo.EditorTodoActivity
import com.suzei.minote.ui.list.notes.ListNoteFragment
import com.suzei.minote.ui.list.notes.ListNotePresenter
import com.suzei.minote.ui.list.todo.ListTodoFragment
import com.suzei.minote.ui.list.todo.ListTodoPresenter
import com.suzei.minote.ui.settings.SettingsActivity
import com.suzei.minote.utils.LogMe
import com.suzei.minote.utils.dialogs.SelectNoteDialog
import com.suzei.minote.utils.dialogs.SelectNoteDialogListener
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        LogMe.info("LOG ListActivity = onCreate()")

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        setSupportActionBar(list_toolbar)

        fm = supportFragmentManager

        list_view_pager.adapter = ListTabPagerAdapter()
        list_tab_layout.setupWithViewPager(list_view_pager)

        list_fab.setOnClickListener(this)
        adView.adListener = adListener

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.list_fab) {
            val dialog = SelectNoteDialog()
            dialog.setOnCreateClickListener(object : SelectNoteDialogListener {

                override fun onCreateClick(type: Int) {

                    when(type) {

                        SelectNoteDialog.NOTE_PAD ->
                            startActivity(Intent(
                                this@ListActivity,
                                EditorNoteActivity::class.java))

                        SelectNoteDialog.TODO_LIST ->
                            startActivity(Intent(
                                this@ListActivity,
                                EditorTodoActivity::class.java))

                    }

                }

            })

            dialog.show(fm, null)
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

    override fun onDestroy() {
        LogMe.info("LOG ListActivity = onDestroy()")
        super.onDestroy()
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

    inner class ListTabPagerAdapter: FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment {
            when (position) {

                0 -> {
                    LogMe.info("LOG ListTabPagerAdapter = set listNoteFragment")

                    val listNoteFragment = ListNoteFragment.newInstance()

                    ListNotePresenter(
                            Injection.provideDataSourceImpl(applicationContext),
                            listNoteFragment)

                    return listNoteFragment
                }

                1 -> {
                    LogMe.info("LOG ListTabPagerAdapter = set listTodoFragment")

                    val listTodoFragment = ListTodoFragment.newInstance()

                    ListTodoPresenter(
                            Injection.provideTodoRepository(applicationContext),
                            listTodoFragment)

                    return listTodoFragment
                }

            }

            throw IllegalArgumentException("Invalid Position $position")
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {

                0 -> return "Notes"

                1 -> return "Todo"

            }

            return super.getPageTitle(position)
        }

        override fun getCount(): Int {
            return 2
        }

    }

}
