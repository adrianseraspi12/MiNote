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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogContent;
import com.franmontiel.fullscreendialog.FullScreenDialogController;
import com.suzei.minote.MainActivity;
import com.suzei.minote.R;
import com.suzei.minote.adapter.ToDoAdapter;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.db.NotesLoaderManager;
import com.suzei.minote.utils.AndroidUtils;
import com.suzei.minote.utils.ColorPicker;
import com.suzei.minote.utils.DateTimePickerDialog;
import com.suzei.minote.utils.FixedHoloDatePickerDialog;
import com.suzei.minote.utils.RangeTimePickerDialog;
import com.suzei.minote.utils.TodoJson;
import com.suzei.minote.utils.onBackPressedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FullScreenTodo extends Fragment implements FullScreenDialogContent, View.OnClickListener {

    private static final String TAG = "FullScreenTodo";
    public static final int EXISTING_NOTE_LOADER = 0;

    private View view;
    private ImageButton addView;
    private RecyclerView todoView;
    private TextView numberView;
    private EditText dateView;
    private EditText timeView;
    private EditText messageView;

    private Calendar calendar;
    private DateTimePickerDialog dateTime;
    private ToDoAdapter mAdapter;
    private List<String> todoList = new ArrayList<>();
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
        view = inflater.inflate(R.layout.fullscreen_dialog_todo, container, false);
        getBundles();
        initUiViews();
        initObjects();
        initDialogPicker();
        initLoaderManager();
        setUpAdapter();
        setUpRecyclerView();
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
        addView = view.findViewById(R.id.note_add);
        todoView = view.findViewById(R.id.note_list);
        dateView = view.findViewById(R.id.note_date);
        timeView = view.findViewById(R.id.note_time);
        messageView = view.findViewById(R.id.note_enter_message);
        numberView = view.findViewById(R.id.note_add_number);

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
                String format = "EEE, MMM/dd/yyyy";
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
            mNotesManager = new NotesLoaderManager(getContext(), currentNoteUri, new NotesLoaderManager.NoteCallbacks() {

                @Override
                public void finishLoad(String date, String time, String message,
                                       String location, String color) {

                    dateView.setText(date);
                    timeView.setText(time);
                    mColorPicker.setSelectedColor(color);

                    todoList.addAll(TodoJson.getItemsArray(message));

                    todoView.swapAdapter(mAdapter, true);

                    String number = String.valueOf(todoList.size() + 1);
                    numberView.setText(number + ".)");

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
                    timeView.setText("");
                    dateView.setText("");
                    timeView.setText("");
                    todoList.clear();
                }
            });
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EXISTING_NOTE_LOADER, null, mNotesManager);
        }
    }

    private void setUpAdapter() {
        mAdapter = new ToDoAdapter(todoList, 0, new ToDoAdapter.RecyclerViewListener() {

            @Override
            public void OnItemClick(int position) {
                todoList.remove(position);
                mAdapter.notifyItemRemoved(position);
                todoView.swapAdapter(mAdapter, true);
                String number = String.valueOf(todoList.size() + 1);
                numberView.setText(number + ".)");
            }
        });
    }

    private void setUpRecyclerView() {
        todoView.setAdapter(mAdapter);
        todoView.setLayoutManager(new LinearLayoutManager(getContext()));
        todoView.setHasFixedSize(true);
    }

    private void setListeners() {
        dateView.setOnClickListener(this);
        timeView.setOnClickListener(this);
        addView.setOnClickListener(this);
        dateView.setOnTouchListener(mTouchListener);
        timeView.setOnTouchListener(mTouchListener);
        addView.setOnTouchListener(mTouchListener);
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
        int type = NoteEntry.TYPE_TODO;
        String date = dateView.getText().toString().trim();
        String time = timeView.getText().toString().trim();
        String color = mColorPicker.getSelectedColor();
        String message = getMessage();

        if (message == null) {
            return null;
        }

        ContentValues values = new ContentValues();
        values.put(NoteEntry.TYPE, type);
        values.put(NoteEntry.DATE, date);
        values.put(NoteEntry.TIME, time);
        values.put(NoteEntry.COLOR, color);
        values.put(NoteEntry.MESSAGE, message);

        return values;
    }

    private String getMessage() {
        HashMap<String, Boolean> map = new HashMap<>();
        final ArrayList<HashMap<String, Boolean>> list = new ArrayList<>();

        if (todoList.size() != 0) {

            for (int i = 0; i < todoList.size(); i++) {
                String item = todoList.get(i);
                map.put(item, false);
            }

            list.add(map);

            JSONObject json = new JSONObject();
            try {
                json.put("Todo", new JSONArray(list));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json.toString();
        }
        Toast.makeText(getContext(), "Note requires a list", Toast.LENGTH_SHORT).show();
        return null;
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
                DatePickerDialog datePickerDialog = new FixedHoloDatePickerDialog(themedContext,
                        dateTime, dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;

            case R.id.note_time:
                RangeTimePickerDialog timePickerDialog = new RangeTimePickerDialog(themedContext,
                        dateTime, dateTime.getHour(), dateTime.getMinutes(), true);

                timePickerDialog.setMin(dateTime.getDay(), dateTime.getHour(),
                        dateTime.getMinutes());
                timePickerDialog.show();
                break;

            case R.id.note_add:
                String message = messageView.getText().toString();

                if (!TextUtils.isEmpty(message)) {
                    String number = String.valueOf(todoList.size() + 2);
                    numberView.setText(number + ".)");

                    todoList.add(message);
                    mAdapter.notifyDataSetChanged();
                    messageView.setText("");
                }

                break;
        }
    }
}
