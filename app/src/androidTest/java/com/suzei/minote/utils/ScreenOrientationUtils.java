package com.suzei.minote.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

public class ScreenOrientationUtils {

    private static void requestRotateDevice(Activity activity, int requestedOrientation) {
        activity.setRequestedOrientation(requestedOrientation);
    }

    public static void rotateScreen(Activity activity) {
        int currentOrientation = activity.getResources().getConfiguration().orientation;

        switch (currentOrientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                requestRotateDevice(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                requestRotateDevice(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;

            default:
                requestRotateDevice(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;

        }
    }

}
