package com.suzei.minote.utils;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.db.NoteDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomDialog extends Dialog implements Dialog.OnCancelListener {

    private static final String TAG = "CustomDialog";

    public static final int NOTE_FRONT = 0;
    public static final int NOTE_BACK = 1;

    private DialogCallback callback;

    private int type;
    private int id;

    private ViewHolder holder;
    private Context mContext;

    private List<TextView> textViews = new ArrayList<>();

    public CustomDialog(@NonNull Context context, int type, int id, DialogCallback callback) {
        super(context);
        this.mContext = context;
        this.type = type;
        this.id = id;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notes);
        initUiViews();
        setOnCancelListener(this);
    }

    private void initUiViews() {
        switch (type) {
            case NOTE_FRONT:
                holder = new ViewHolder(this.getWindow().getDecorView(), false);
                break;

            case NOTE_BACK:
                holder = new ViewHolder(this.getWindow().getDecorView(), true);
                break;

            default:
                throw new IllegalArgumentException("Invalid type= " + type);
        }
    }

    private String getMessage() {
        HashMap<String, Boolean> map = new HashMap<>();
        final ArrayList<HashMap<String, Boolean>> list = new ArrayList<>();


        if (textViews.size() != 0) {

            int index = textViews.size() - 1;

            for (int i = 0; i < textViews.size(); i++) {
                TextView textView = textViews.get(index);

                String text = textView.getText().toString();

                map.put(text, isStrikeThrough(textView));
                index--;
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

        return null;
    }

    private boolean isStrikeThrough(TextView textView) {
        if (textView.getPaintFlags() == 1281) {
            return false;
        } else {
            return true;
        }
    }

    public void setContent(Bundle bundle) {
        int noteType = bundle.getInt("note_type");
        String message = bundle.getString("note_message");
        String color = bundle.getString("note_color");
        int bgColor = Color.parseColor(color);

        holder.titleView.setText(bundle.getString("note_title"));
        holder.dateView.setText(bundle.getString("note_date"));
        holder.timeView.setText(bundle.getString("note_time"));
        holder.rootView.setBackgroundColor(bgColor);

        Log.d(TAG, "setContent: type=" + noteType);

        if (noteType == NoteEntry.TYPE_TODO) {
            setTodoMessage(message);
        } else {
            setContentMessage(message);
        }
    }

    private void setContentMessage(String message) {
        holder.messageView.setText(message);
    }

    private void setTodoMessage(String message) {
        String items = TodoJson.getMapFormatListString(message);
        ArrayList<String> todoList = new ArrayList<>();
        todoList.addAll(TodoJson.getItemsArray(items));

        for (int i = 0; i < todoList.size(); i++) {
            final TextView textView = new TextView(mContext);
            textViews.add(textView);
            String text = todoList.get(i);
            final String[] item = text.split(":");

            String full_text = (i + 1) + ".)" + item[0];

            Log.d(TAG, "setTodoMessage: item= " + item[0]);

            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            textView.setText(full_text);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            if (item[1].equals("true")) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                //map isStrikeThrough true
            } else {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                Log.d(TAG, "setTodoMessage: textview paintFlags= " + textView.getPaintFlags());
                //map isStrikeThrough false
            }

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (textView.getPaintFlags() == 1281) {
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        textView.setPaintFlags(1281);
                    }
                }
            });

            holder.noteTodoContainer.addView(textView);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {

        NoteDBHelper dbHelper = new NoteDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (getMessage() != null) {

            String message = getMessage()
                    .replaceAll("[0-9]", "")
                    .replaceAll("[.)]", "");

            ContentValues values = new ContentValues();
            values.put(NoteEntry.MESSAGE, message);

            String where = NoteEntry._ID + "= " + id;

            int update = db.update(NoteEntry.TABLE_NAME, values, where, null);

            String items = NoteEntry._ID + ", " + NoteEntry.TYPE + ", " + NoteEntry.TITLE
                    + ", " + NoteEntry.DATE + ", " + NoteEntry.TIME + ", " +
                    NoteEntry.MESSAGE + ", " + NoteEntry.COLOR;

            Cursor newCursor = db.rawQuery("SELECT " + items + " FROM "
                    + NoteEntry.TABLE_NAME, null);

            callback.AfterCancel(newCursor);
        }


    }

    class ViewHolder {
        RelativeLayout rootView;
        RelativeLayout noteFrontView;
        ScrollView noteBackView;
        LinearLayout noteTodoContainer;
        View view;
        TextView dateView;
        TextView timeView;
        TextView titleView;
        TextView messageView;

        public ViewHolder(View view, boolean isBackVisible) {
            this.view = view;
            initUiViews();
            showDisplay(isBackVisible);
        }

        private void initUiViews() {
            rootView = view.findViewById(R.id.dialog_note_root);
            noteFrontView = view.findViewById(R.id.dialog_note_front);
            noteBackView = view.findViewById(R.id.dialog_note_back);
            dateView = view.findViewById(R.id.dialog_note_date);
            timeView = view.findViewById(R.id.dialog_note_time);
            titleView = view.findViewById(R.id.dialog_note_title);
            messageView = view.findViewById(R.id.dialog_note_message);
            noteTodoContainer = view.findViewById(R.id.dialog_note_todo_container);
        }

        private void showDisplay(boolean isBackVisible) {
            if (isBackVisible) {
                noteFrontView.setVisibility(View.GONE);
                noteBackView.setVisibility(View.VISIBLE);
            } else {
                noteFrontView.setVisibility(View.VISIBLE);
                noteBackView.setVisibility(View.GONE);
            }
        }
    }

    public interface DialogCallback {
        void AfterCancel(Cursor cursor);
    }
}
