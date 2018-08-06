package com.suzei.minote.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class DateTimePickerDialog implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private Calendar calendar;
    private DateTimePickerCallback callback;

    private int oldYear;
    private int oldMonth;
    private int oldDay;

    public DateTimePickerDialog(Calendar calendar, DateTimePickerCallback callback) {
        this.calendar = calendar;
        this.callback = callback;

        oldYear = calendar.get(Calendar.YEAR);
        oldMonth = calendar.get(Calendar.MONTH);
        oldDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (year != oldYear || month != oldMonth || dayOfMonth != oldDay) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
        } else {
            Calendar oldCalendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, oldCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, oldCalendar.get(Calendar.MINUTE));
            callback.updateTime(calendar.getTime());
        }

        callback.updateDate(calendar.getTime());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        callback.updateTime(calendar.getTime());
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH);

    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);

    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return calendar.get(Calendar.MINUTE);
    }

    public interface DateTimePickerCallback {

        void updateDate(Date date);
        void updateTime(Date date);

    }
}
