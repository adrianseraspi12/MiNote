package com.suzei.minote.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonConvert {

    private static final String TAG = "JsonConvert";

    public static boolean isValidJson(String str) {
        try {
            new JSONObject(str);
        }  catch (JSONException e) {

            try {
                new JSONArray(str);
            } catch (JSONException e1) {
                return false;
            }
        }

        return true;
    }

    public static String toString(String str) {
        str = str.replaceAll("[\\[\\](){}:\"]","");
        str = str.replaceAll(",", "\n");
        str = str.replaceAll("false", "");
        str = str.replaceAll("true", "");
        str = str.replaceAll("Todo", "");

        return str;
    }
}
