package com.suzei.minote.fullscreendialog;

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
import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.db.NotesLoaderManager;
import com.suzei.minote.utils.AndroidUtils;
import com.suzei.minote.utils.ColorPicker;
import com.suzei.minote.utils.DateTimePickerDialog;
import com.suzei.minote.utils.onBackPressedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FullScreenLecture extends Fragment implements FullScreenDialogContent, View.OnClickListener {

    private static final String TAG = "FullScreenLecture";
    public static final int EXISTING_NOTE_LOADER = 0;

    private View view;

    private EditText dateView;
    private EditText messageView;

    private Calendar calendar;
    private DateTimePickerDialog date;
    private NotesLoaderManager mNotesManager;
    private ColorPicker mColorPicker;
    private FullScreenDialogController dialogController;

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
        view = inflater.inflate(R.layout.fullscreen_dialog_lecture, container, false);
        getBundles();
        initUiViews();
        initObjects();
        initDialogPicker();
        initLoaderManager();
        setListeners();
        return view;
    }

    private void getBundles() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentNoteUri = Uri.parse(bundle.getString("note_uri"));
        }
    }

    private void initUiViews() {
        dateView = view.findViewById(R.id.note_date);
        messageView = view.findViewById(R.id.note_enter_message);

        AndroidUtils.setSoftInputMode(getActivity(), WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void initObjects() {
        mColorPicker = new ColorPicker(getContext(), view);
        calendar = Calendar.getInstance();
    }

    private void initDialogPicker() {
        date = new DateTimePickerDialog(calendar,
                new DateTimePickerDialog.DateTimePickerCallback() {

            @Override
            public void updateDate(Date date) {
                String format = "EEE, MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                dateView.setText(sdf.format(calendar.getTime()));
            }

            @Override
            public void updateTime(Date date) {

            }
        });
    }

    private void initLoaderManager() {
        if (currentNoteUri != null) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EXISTING_NOTE_LOADER, null, mNotesManager);
        }
    }

    private void setListeners() {
        dateView.setOnClickListener(this);
        dateView.setOnTouchListener(mTouchListener);
        messageView.setOnTouchListener(mTouchListener);
    }

    private void showUnsaveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Discard your changes and quit editing");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogController.discard();
            }
        });
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
            int rowsUpdated = getContext().getContentResolver().update(NoteEntry.CONTENT_URI, values,
                    selection, selectionArgs);
            if (rowsUpdated != 0) {
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
        int type = NoteEntry.TYPE_LECTURE;

        String date = dateView.getText().toString().trim();
        String color = mColorPicker.getSelectedColor();
        String message = messageView.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(NoteEntry.TYPE, type);
        values.put(NoteEntry.COLOR, color);
        values.put(NoteEntry.MESSAGE, message);

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

            case R.id.note_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(themedContext, date,
                        date.getYear(), date.getMonth(), date.getDay());

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;
        }
    }
}
