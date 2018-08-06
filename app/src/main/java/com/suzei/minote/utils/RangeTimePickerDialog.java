package com.suzei.minote.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;

public class RangeTimePickerDialog extends TimePickerDialog {

    private static final String TAG = "RangeTimePickerDialog";

    private int minDay = -1;
    private int minHour = -1;
    private int minMinute = -1;

    private int currentHour;
    private int currentMinute;

    private Calendar calendar = Calendar.getInstance();
    private DateFormat dateFormat;


    public RangeTimePickerDialog(Context context, OnTimeSetListener callBack,
                                 int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
        this.currentHour = hourOfDay;
        this.currentMinute = minute;
        this.dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            Class<?> superclass = getClass().getSuperclass();
            Field mTimePickerField = superclass.getDeclaredField("mTimePicker");
            mTimePickerField.setAccessible(true);
            TimePicker mTimePicker = (TimePicker) mTimePickerField.get(this);
            mTimePicker.setOnTimeChangedListener(this);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    public void setMin(int day, int hour, int minute) {
        minDay = day;
        minHour = hour;
        minMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int todayHour = calendar.get(Calendar.HOUR_OF_DAY);
        int todayMinute = calendar.get(Calendar.MINUTE);

        boolean validTime = true;
        // if same day, validTime = true
        // else validTime = false

        if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)) {
                validTime = false;
        }

        if (hourOfDay > todayHour || (hourOfDay == todayHour && minute > todayMinute) &&
                today == minDay) {
            validTime = true;
        } else if (today != minDay) {
            validTime = true;
        }

        if (validTime) {
            //change time
            currentHour = hourOfDay;
            currentMinute = minute;
        }

        Log.e(TAG, "onTimeChanged= " + currentHour +":" + currentMinute);

        updateTime(currentHour, currentMinute);
        updateDialogTitle(view, currentHour, currentMinute);
    }

    private void updateDialogTitle(TimePicker timePicker, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String title = dateFormat.format(calendar.getTime());
        setTitle(title);
    }
}
