package com.suzei.minote.ui.settings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.suzei.minote.BuildConfig;
import com.suzei.minote.R;
import com.suzei.minote.utils.ColorWheel;

import androidx.preference.Preference;

public class SettingsFragment extends BasePreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, SettingsContract.View {

    private SettingsContract.Presenter presenter;

    private Preference defNoteColor;
    private Preference defTextColor;
    private Preference sendFeedbackPref;
    private Preference moreFromDevPref;
    private Preference appVersionPref;

    static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        findAllPreferences();
        showVersionName();
        setListener();
    }

    private void findAllPreferences() {
        sendFeedbackPref = findPreference("send_feedback");
        moreFromDevPref = findPreference("more_from_dev");
        appVersionPref = findPreference("app_version");
        defNoteColor = findPreference("default_note_color");
        defTextColor = findPreference("default_text_color");
    }

    private void showVersionName() {
        String appVersion = BuildConfig.VERSION_NAME;
        appVersionPref.setSummary(appVersion);
    }

    private void setListener() {
        sendFeedbackPref.setOnPreferenceClickListener(this);
        moreFromDevPref.setOnPreferenceClickListener(this);
        defNoteColor.setOnPreferenceClickListener(this);
        defTextColor.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {

            case "default_note_color":
                presenter.noteColorWheel();
                return true;

            case "default_text_color":
                presenter.textColorWheel();
                return true;

            case "send_feedback":
                presenter.redirectToEmail();
                return true;

            case "more_from_dev":
                presenter.redirectToPlaystore();
                return true;

            default:
                throw new IllegalArgumentException("Invalid preference key = " + key);

        }

    }

    @Override
    public void setPresenter(SettingsContract.Presenter _presenter) {
        this.presenter = _presenter;
    }

    @Override
    public void showColorWheel(String title, String initialColor, ColorWheel colorWheel) {
        ColorPickerDialogBuilder.with(getContext())
                .setTitle(title)
                .initialColor(Color.parseColor(initialColor))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(6)
                .setPositiveButton("Choose", (dialogInterface, _color, integers) ->
                        colorWheel.onPositiveClick(_color))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .build()
                .show();
    }

    @Override
    public void startIntentActivity(Intent intent) {
        startActivity(intent);
    }

}