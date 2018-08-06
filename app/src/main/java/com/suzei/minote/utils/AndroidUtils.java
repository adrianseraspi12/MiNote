package com.suzei.minote.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtils {

    public static void setSoftInputMode(Activity activity, int mode) {
        activity.getWindow().setSoftInputMode(mode);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
