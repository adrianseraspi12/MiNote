package com.suzei.minote.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View mView;
    private SharedPreferences sharedPreferences;

    SettingsPresenter(SharedPreferences sharedPreferences, SettingsContract.View mView) {
        this.mView = mView;
        this.sharedPreferences = sharedPreferences;
        mView.setPresenter(this);
    }

    @Override
    public void noteColorWheel() {
        String initialColor = sharedPreferences.getString("default_note_color", "#ef5350");
        mView.showColorWheel(
                "Choose note color",
                initialColor,
                color -> {
                    String hexColor = String.format("#%06X", (0xFFFFFF & color));
                    saveSharedPrefs("default_note_color", hexColor);
                });

    }

    @Override
    public void textColorWheel() {
        String textColor = sharedPreferences.getString("default_text_color", "#000000");
        mView.showColorWheel(
                "Choose text color",
                textColor,
                color -> {
                    String hexColor = String.format("#%06X", (0xFFFFFF & color));
                    saveSharedPrefs("default_text_color", hexColor);
                }
        );
    }

    private void saveSharedPrefs(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void redirectToEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"adrianseraspi12@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
        mView.startIntentActivity(Intent.createChooser(emailIntent, "Send Feedback"));
    }

    @Override
    public void redirectToPlaystore() {
        final String developerId = "developer?id=Adrian+Seraspi";
        Uri uri;

        try {

            uri = Uri.parse("market://" + developerId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mView.startIntentActivity(intent);

        } catch (ActivityNotFoundException e) {

            uri = Uri.parse("https://play.google.com/store/apps/" + developerId);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mView.startIntentActivity(intent);

        }
    }
}