package com.suzei.minote.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import com.suzei.minote.utils.ColorWheel

class SettingsPresenter internal constructor(private val sharedPreferences: SharedPreferences, private val mView: SettingsContract.View) : SettingsContract.Presenter {

    init {
        mView.setPresenter(this)
    }

    override fun noteColorWheel() {
        val initialColor = sharedPreferences.getString("default_note_color", "#ef5350")
        mView.showColorWheel(
                "Choose note color",
                initialColor!!,
                object: ColorWheel {

                    override fun onPositiveClick(color: Int) {
                        val hexColor = String.format("#%06X", 0xFFFFFF and color)
                        saveSharedPrefs("default_note_color", hexColor)
                    }

                })

    }

    override fun textColorWheel() {
        val textColor = sharedPreferences.getString("default_text_color", "#000000")
        mView.showColorWheel(
                "Choose text color",
                textColor!!,
                object: ColorWheel {

                    override fun onPositiveClick(color: Int) {
                        val hexColor = String.format("#%06X", 0xFFFFFF and color)
                        saveSharedPrefs("default_text_color", hexColor)
                    }

                })
    }

    private fun saveSharedPrefs(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun redirectToEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/email"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("adrianseraspi12@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback")
        mView.startIntentActivity(Intent.createChooser(emailIntent, "Send Feedback"))
    }

    override fun redirectToPlaystore() {
        val developerId = "developer?id=Adrian+Seraspi"
        var uri: Uri

        try {

            uri = Uri.parse("market://$developerId")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            mView.startIntentActivity(intent)

        } catch (e: ActivityNotFoundException) {

            uri = Uri.parse("https://play.google.com/store/apps/$developerId")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            mView.startIntentActivity(intent)

        }

    }
}