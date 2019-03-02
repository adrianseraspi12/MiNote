package com.suzei.minote.ui.settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.suzei.minote.BuildConfig
import com.suzei.minote.R
import com.suzei.minote.utils.ColorWheel

class SettingsFragment : BasePreferenceFragmentCompat(), Preference.OnPreferenceClickListener, SettingsContract.View {

    private lateinit var presenter: SettingsContract.Presenter

    private lateinit var defNoteColor: Preference
    private lateinit var defTextColor: Preference
    private lateinit var sendFeedbackPref: Preference
    private lateinit var moreFromDevPref: Preference
    private lateinit var appVersionPref: Preference

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        findAllPreferences()
        showVersionName()
        setListener()
    }

    private fun findAllPreferences() {
        sendFeedbackPref = findPreference("send_feedback")
        moreFromDevPref = findPreference("more_from_dev")
        appVersionPref = findPreference("app_version")
        defNoteColor = findPreference("default_note_color")
        defTextColor = findPreference("default_text_color")
    }

    private fun showVersionName() {
        val appVersion = BuildConfig.VERSION_NAME
        appVersionPref.summary = appVersion
    }

    private fun setListener() {
        sendFeedbackPref.onPreferenceClickListener = this
        moreFromDevPref.onPreferenceClickListener = this
        defNoteColor.onPreferenceClickListener = this
        defTextColor.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val key = preference.key

        when (key) {

            "default_note_color" -> {
                presenter.noteColorWheel()
                return true
            }

            "default_text_color" -> {
                presenter.textColorWheel()
                return true
            }

            "send_feedback" -> {
                presenter.redirectToEmail()
                return true
            }

            "more_from_dev" -> {
                presenter.redirectToPlaystore()
                return true
            }

            else -> throw IllegalArgumentException("Invalid preference key = $key")
        }

    }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this.presenter = presenter
    }

    override fun showColorWheel(title: String, initialColor: String, colorWheel: ColorWheel) {
        ColorPickerDialogBuilder.with(context!!)
                .setTitle(title)
                .initialColor(Color.parseColor(initialColor))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(6)
                .setPositiveButton("Choose") { dialogInterface, _color, integers -> colorWheel.onPositiveClick(_color) }
                .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                .build()
                .show()
    }

    override fun startIntentActivity(intent: Intent) {
        startActivity(intent)
    }

    companion object {

        internal fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}