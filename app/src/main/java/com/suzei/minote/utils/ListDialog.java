package com.suzei.minote.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import static com.suzei.minote.db.NoteContract.NoteEntry;

public class ListDialog extends AlertDialog.Builder {

    private DialogCallback callback;

    public ListDialog(@NonNull Context context, DialogCallback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    public AlertDialog show() {
        this.setTitle("Select Type of Note: ");

        final ArrayAdapter<String> arrayAdapter = getItemArrayDialog();

        this.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = arrayAdapter.getItem(which);

                Class<? extends Fragment> fragment =
                        FullScreenFragmentSelector.getFragmentClass(getType(str));

                callback.selectedDialog(fragment, str);
            }
        });

        return super.show();
    }

    private ArrayAdapter<String> getItemArrayDialog() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.select_dialog_item);

        arrayAdapter.add("Reminder");
        arrayAdapter.add("To-do");
        arrayAdapter.add("Lecture");
        arrayAdapter.add("Events");

        return arrayAdapter;
    }

    private int getType(String type) {
        switch (type) {
            case "Reminder":
                return NoteEntry.TYPE_REMINDER;
            case "To-do":
                return NoteEntry.TYPE_TODO;
            case "Lecture":
                return NoteEntry.TYPE_LECTURE;
            case "Events":
                return NoteEntry.TYPE_EVENTS;
            default:
                throw new IllegalArgumentException("Not valid type");
        }
    }

    public interface DialogCallback {
        void selectedDialog (Class<?extends Fragment> fragment, String str);
    }

}
