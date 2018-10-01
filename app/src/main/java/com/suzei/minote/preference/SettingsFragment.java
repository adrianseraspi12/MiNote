package com.suzei.minote.preference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;

import com.suzei.minote.BuildConfig;
import com.suzei.minote.R;

public class SettingsFragment extends BasePreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener {

    private Preference sendFeedbackPref;
    private Preference moreFromDevPref;
    private Preference appVersionPref;
    private Preference privacyPolicyPref;

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
        privacyPolicyPref = findPreference("privacy_policy");
    }

    private void showVersionName() {
        String appVersion = BuildConfig.VERSION_NAME;
        appVersionPref.setSummary(appVersion);
    }

    private void setListener() {
        sendFeedbackPref.setOnPreferenceClickListener(this);
        moreFromDevPref.setOnPreferenceClickListener(this);
        privacyPolicyPref.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        switch (key) {

            case "send_feedback":
                redirectToEmail();
                return true;

            case "more_from_dev":
                redirectToPlaystore();
                return true;

            case "privacy_policy":
                redirectToPrivatePolicyWeb();
                return true;

            default:
                throw new IllegalArgumentException("Invalid preference key = " + key);

        }

    }

    private void redirectToEmail() {
        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);

        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL,
                new String[] {"adrianseraspi12@gmail.com"});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
    }

    private void redirectToPlaystore() {
        final String developerId = "developer?id=Adrian+Seraspi";
        try {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://" + developerId)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/" + developerId)));
        }
    }

    private void redirectToPrivatePolicyWeb() {
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/minote-privacy-policy/home")
        ));
    }

}
