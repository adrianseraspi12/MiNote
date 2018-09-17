package com.suzei.minote.fullscreendialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.suzei.minote.MainActivity;
import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.db.NotesLoaderManager;
import com.suzei.minote.utils.AndroidUtils;
import com.suzei.minote.utils.ColorPicker;
import com.suzei.minote.utils.DateTimePickerDialog;
import com.suzei.minote.utils.FixedHoloDatePickerDialog;
import com.suzei.minote.utils.RangeTimePickerDialog;
import com.suzei.minote.utils.onBackPressedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FullScreenEvents extends Fragment implements FullScreenDialogContent, View.OnClickListener {

    private static final String TAG = "FullScreenEvents";
    public static final int EXISTING_NOTE_LOADER = 0;

    private View view;

    private EditText locationView;
    private EditText messageView;
    private EditText dateView;
    private EditText timeView;

    private Calendar calendar;
    private DateTimePickerDialog dateTime;
    private NotesLoaderManager mNotesManager;
    private FullScreenDialogController dialogController;
    private ColorPicker mColorPicker;

    private Uri currentNoteUri;
    private boolean mNoteHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fullscreen_dialog_events, container, false);
        getBundles();
        initUiViews();
        initObjects();
        initDialogPicker();
        initLoaderManager();
        setListeners();
        return view;
    }

    private void getBundles() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            currentNoteUri = Uri.parse(bundle.getString("note_uri"));
        }
    }

    private void initUiViews() {
        locationView = view.findViewById(R.id.note_location);
        messageView = view.findViewById(R.id.note_enter_message);
        dateView = view.findViewById(R.id.note_start_date);
        timeView = view.findViewById(R.id.note_start_time);

        AndroidUtils.setSoftInputMode(getActivity(), WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void initObjects() {
        mColorPicker = new ColorPicker(getContext(), view);
        calendar = Calendar.getInstance();
    }

    private void initDialogPicker() {
        calendar.setTimeInMillis(System.currentTimeMillis());

        dateTime = new DateTimePickerDialog(calendar,
                new DateTimePickerDialog.DateTimePickerCallback() {

                    @Override
                    public void updateDate(Date date) {
                        String format = "MM/dd/yy";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                        dateView.setText(sdf.format(date));
                    }

                    @Override
                    public void updateTime(Date date) {
                        String format = "hh:mm aa";
                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                        timeView.setText(sdf.format(date));
                    }
                });
    }

    private void initLoaderManager() {
        if (currentNoteUri != null) {
            mNotesManager = new NotesLoaderManager(getContext(), currentNoteUri,
                    new NotesLoaderManager.NoteCallbacks() {

                @Override
                public void finishLoad(String date, String time, String message,
                                       String location, String color) {

                    dateView.setText(date);
                    timeView.setText(time);
                    messageView.setText(message);
                    mColorPicker.setSelectedColor(color);

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM/dd/yyyy "
                                + "hh:mm aa", Locale.ENGLISH);
                        calendar.setTime(sdf.parse(date + " " + time));
                    } catch (ParseException e) {
                        Log.e(TAG, "finishLoad: Error parsing date ", e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void resetLoad() {
                    dateView.setText("");
                    timeView.setText("");
                    messageView.setText("");

                }
            });
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EXISTING_NOTE_LOADER, null, mNotesManager);
        }
    }

    private void setListeners() {
        dateView.setOnClickListener(this);
        timeView.setOnClickListener(this);
        messageView.setOnTouchListener(mTouchListener);
        locationView.setOnTouchListener(mTouchListener);
        dateView.setOnTouchListener(mTouchListener);
        timeView.setOnTouchListener(mTouchListener);
    }

    private void showUnsaveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Discard your changes and quit editing");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onDialogCreated(FullScreenDialogController dialogController) {
        this.dialogController = dialogController;
    }

    @Override
    public boolean onConfirmClick(FullScreenDialogController dialogController) {
        ContentValues values = userInputValues();
        if (values == null) {
            return true;
        }

        if (currentNoteUri != null) {
            String selection = NoteEntry._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(currentNoteUri))};
            int rowUpdated = getContext().getContentResolver().update(NoteEntry.CONTENT_URI, values,
                    selection, selectionArgs);

            if (rowUpdated != 0) {
                Toast.makeText(getContext(), "Note Updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri insertUri = getContext().getContentResolver().insert(NoteEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (insertUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getContext(), "Note not saved",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(getContext(), "Note save", Toast.LENGTH_SHORT).show();
            }
        }

        AndroidUtils.hideKeyboardFrom(getContext(), view);
        return false;
    }

    private ContentValues userInputValues() {
        int type = NoteEntry.TYPE_EVENTS;

        String date = dateView.getText().toString().trim();
        String time = timeView.getText().toString().trim();
        String location = locationView.getText().toString().trim();
        String message = messageView.getText().toString().trim();
        String color = mColorPicker.getSelectedColor();

        ContentValues values = new ContentValues();
        values.put(NoteEntry.TYPE, type);
        values.put(NoteEntry.DATE, date);
        values.put(NoteEntry.TIME, time);
        values.put(NoteEntry.LOCATION, location);
        values.put(NoteEntry.MESSAGE, message);
        values.put(NoteEntry.COLOR, color);

        return values;
    }

    @Override
    public boolean onDiscardClick(FullScreenDialogController dialogController) {

        AndroidUtils.hideKeyboardFrom(getContext(), view);

        if (mNoteHasChanged) {
            showUnsaveChangesDialog();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        final Context themedContext = new ContextThemeWrapper(getContext(),
                android.R.style.Theme_Holo_Light_Dialog);

        switch (v.getId()) {

            case R.id.note_start_date:
                DatePickerDialog datePickerDialog = new FixedHoloDatePickerDialog(themedContext,
                        dateTime, dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;

            case R.id.note_start_time:
                RangeTimePickerDialog timePickerDialog = new RangeTimePickerDialog(themedContext,
                        dateTime, dateTime.getHour(), dateTime.getMinutes(), true);

                timePickerDialog.setMin(dateTime.getDay(),
                        dateTime.getHour(), dateTime.getMinutes());
                timePickerDialog.show();
                break;
        }
    }


}
