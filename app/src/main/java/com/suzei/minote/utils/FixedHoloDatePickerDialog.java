package com.suzei.minote.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.DatePicker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class FixedHoloDatePickerDialog extends DatePickerDialog {

    private int mYear;
    private int mMonth;
    private int mDay;

    public FixedHoloDatePickerDialog(Context context, OnDateSetListener callBack,
                                     int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        this.mYear = year;
        this.mMonth = monthOfYear;
        this.mDay = dayOfMonth;

        // Force spinners on Android 7.0 only (SDK 24).
        // Note: I'm using a naked SDK value of 24 here, because I'm
        // targeting SDK 23, and Build.VERSION_CODES.N is not available yet.
        // But if you target SDK >= 24, you should have it.
        if (Build.VERSION.SDK_INT == 24) {
            try {
                final Field field = this.findField(
                        DatePickerDialog.class,
                        DatePicker.class,
                        "mDatePicker"
                );

                final DatePicker datePicker = (DatePicker) field.get(this);
                final Class<?> delegateClass = Class.forName(
                        "android.widget.DatePicker$DatePickerDelegate"
                );
                final Field delegateField = this.findField(
                        DatePicker.class,
                        delegateClass,
                        "mDelegate"
                );

                final Object delegate = delegateField.get(datePicker);
                final Class<?> spinnerDelegateClass = Class.forName(
                        "android.widget.DatePickerSpinnerDelegate"
                );

                if (delegate.getClass() != spinnerDelegateClass) {
                    delegateField.set(datePicker, null);
                    datePicker.removeAllViews();

                    final Constructor spinnerDelegateConstructor =
                            spinnerDelegateClass.getDeclaredConstructor(
                                    DatePicker.class,
                                    Context.class,
                                    AttributeSet.class,
                                    int.class,
                                    int.class
                            );
                    spinnerDelegateConstructor.setAccessible(true);

                    final Object spinnerDelegate = spinnerDelegateConstructor.newInstance(
                            datePicker,
                            context,
                            null,
                            android.R.attr.datePickerStyle,
                            0
                    );
                    delegateField.set(datePicker, spinnerDelegate);

                    datePicker.init(year, monthOfYear, dayOfMonth, this);
                    datePicker.setCalendarViewShown(false);
                    datePicker.setSpinnersShown(true);
                }
            } catch (Exception e) { /* Do nothing */ }
        }
    }

    /**
     * Find Field with expectedName in objectClass. If not found, find first occurrence of
     * target fieldClass in objectClass.
     */
    private Field findField(Class objectClass, Class fieldClass, String expectedName) {
        try {
            final Field field = objectClass.getDeclaredField(expectedName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) { /* Ignore */ }

        // Search for it if it wasn't found under the expectedName.
        for (final Field field : objectClass.getDeclaredFields()) {
            if (field.getType() == fieldClass) {
                field.setAccessible(true);
                return field;
            }
        }

        return null;
    }

//    @Override
//    public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
//
//        if (year < mYear) {
//            view.updateDate(mYear, mMonth, mDay);
//        }
//
//        if (month < mMonth && year == mYear) {
//            view.updateDate(mYear, mMonth, mDay);
//        }
//
//        if (dayOfMonth < mDay && year == mYear && month == mMonth) {
//            view.updateDate(mYear, mMonth, mDay);
//        }
//    }
}

