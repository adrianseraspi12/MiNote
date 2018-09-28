package com.suzei.minote.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TodoJson {

    private static final String TAG = "TodoJson";

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

    public static String getMapFormatListString(String str) {

        Log.i(TAG, "getMapFormatListString: structure= " + str);

        ArrayList<String> list = new ArrayList<>();
        JSONObject json = null;

        try {
            json = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray array = json.optJSONArray("Todo");
        for (int i = 0; i < array.length(); i++) {

            try {
                list.add(array.getString(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list.toString();
    }

    public static List<String> getItemsArray(String str) {
        String todo = str
                .replaceAll("[\\[\\{\"]", "")
                .replaceAll("[\\]\\}]","");
        String[] items = todo.split(",");
        return Arrays.asList(items);
    }

}
