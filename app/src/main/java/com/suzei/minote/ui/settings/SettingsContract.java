package com.suzei.minote.ui.settings;

import android.content.Intent;

import com.suzei.minote.utils.ColorWheel;

public interface SettingsContract {

    interface View {

        void setPresenter(Presenter presenter);

        void showColorWheel(String title, String initialColor, ColorWheel colorWheel);

        void startIntentActivity(Intent intent);

    }

    interface Presenter {

        void noteColorWheel();

        void textColorWheel();

        void redirectToEmail();

        void redirectToPlaystore();

    }

}
