package com.suzei.minote.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.suzei.minote.utils.ColorWheel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsPresenterTest {

    private SettingsPresenter settingsPresenter;

    @Mock
    private SharedPreferences preferences;

    @Mock
    private SettingsContract.View mView;

    @Mock
    private Intent intent;

    @Captor
    private ArgumentCaptor<ColorWheel> colorWheelArgumentCaptor;

    @Before
    public void setUpSettingsPresenter() {
        MockitoAnnotations.initMocks(this);
        settingsPresenter = new SettingsPresenter(preferences, mView);
    }

    @Test
    public void createSettingsPresenter() {
        verify(mView).setPresenter(settingsPresenter);
    }

    @Test
    public void noteColorWheel() {
        settingsPresenter.noteColorWheel();

        String initialColor = "#ef5350";

        when(preferences.getString(eq("default_note_color"), eq("#ef5350")))
                .thenReturn(initialColor);

        verify(mView).showColorWheel(
                eq("Choose note color"),
                isNull(initialColor.getClass()),
                colorWheelArgumentCaptor.capture());
    }

    @Test
    public void textColorWheel() {
        settingsPresenter.textColorWheel();

        String initialColor = "#000000";

        when(preferences.getString(eq("default_text_color"), eq("#000000")))
                .thenReturn(initialColor);

        verify(mView).showColorWheel(
                eq("Choose text color"),
                isNull(initialColor.getClass()),
                colorWheelArgumentCaptor.capture());
    }

    @Test
    public void redirectToEmail() {
        settingsPresenter.redirectToEmail();

        //  Creating an intent action to redirect to email
        when(intent.getAction()).thenReturn(Intent.ACTION_SEND);
        when(intent.getType()).thenReturn("text/email");
        when(intent.getStringArrayExtra(Intent.EXTRA_EMAIL))
                .thenReturn(new String[]{"adrianseraspi12@gmail.com"});
        when(intent.getStringExtra(Intent.EXTRA_SUBJECT)).thenReturn("User Feedback");

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mView).startIntentActivity(intentArgumentCaptor.capture());
    }

    @Test
    public void redirectToPlaystore_usingMarketLink() {
        settingsPresenter.redirectToPlaystore();
        Uri uri = Uri.parse("market://" + "developer?id=Adrian+Seraspi");

        when(intent.getAction()).thenReturn(Intent.ACTION_VIEW);
        when(intent.getData()).thenReturn(uri);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mView).startIntentActivity(intentArgumentCaptor.capture());
    }

    @Test
    public void redirectToPlaystore_usingWebLink() {
        settingsPresenter.redirectToPlaystore();
        Uri uri = Uri.parse("https://play.google.com/store/apps/" + "developer?id=Adrian+Seraspi");

        when(intent.getAction()).thenReturn(Intent.ACTION_VIEW);
        when(intent.getData()).thenReturn(uri);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mView).startIntentActivity(intentArgumentCaptor.capture());
    }

}