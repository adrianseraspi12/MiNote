package com.suzei.minote.adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.suzei.minote.R;
import com.suzei.minote.db.NoteContract.NoteEntry;
import com.suzei.minote.models.Notes;
import com.suzei.minote.utils.CustomDialog;
import com.suzei.minote.utils.FullScreenFragmentSelector;
import com.suzei.minote.utils.TodoJson;

import java.util.ArrayList;

public class NotesAdapter extends CursorAdapter {

    private static final String TAG = "NotesAdapter";
    private static final int TYPE_TODO = 0;
    private static final int TYPE_DEFAULT = 1;

    private Context mContext;

    private DatabaseCallbacks callbacks;

    private SparseBooleanArray positionVisible;

    public NotesAdapter(Context context, Cursor c, DatabaseCallbacks callbacks) {
        super(context, c, 0);
        this.mContext = context;
        this.callbacks = callbacks;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        int type = cursor.getInt(cursor.getColumnIndex(NoteEntry.TYPE));

        switch (type) {
            case NoteEntry.TYPE_TODO:
                return TYPE_TODO;
            default:
                return TYPE_DEFAULT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        if (getItemViewType(cursor.getPosition()) == TYPE_TODO) {
            return LayoutInflater.from(context).inflate(R.layout.item_row_notes_todo, parent,
                    false);
        } else {
            return LayoutInflater.from(context).inflate(R.layout.item_row_notes_default, parent,
                    false);
        }
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor) {
        int position = cursor.getPosition();
        Notes notes = get(position, cursor);
        ViewHolder holder = new ViewHolder(view, notes.getType());
        holder.titleView.setText(notes.getTitle());
        holder.dateView.setText(notes.getDate());
        holder.timeView.setText(notes.getTime());
        holder.notesViewFront.setCardBackgroundColor(Color.parseColor(notes.getColor()));
        holder.notesViewBack.setCardBackgroundColor(Color.parseColor(notes.getColor()));

        if (notes.getType() == NoteEntry.TYPE_TODO) {
            bindTodoList(holder.noteTodoList, notes.getMessage());
        } else {
            Log.d(TAG, "bindView: message=" + notes.getMessage());
            holder.messageView.setText(notes.getMessage());
        }

        if (positionVisible == null) {
            positionVisible.put(position, false);
        }

        Bundle bundle = new Bundle();

        bundle.putInt("note_id", notes.get_id());
        bundle.putInt("note_type", notes.getType());
        bundle.putString("note_title", notes.getTitle());
        bundle.putString("note_date", notes.getDate());
        bundle.putString("note_time", notes.getTime());
        bundle.putString("note_message", notes.getMessage());
        bundle.putString("note_color", notes.getColor());

        holder.menuFront.setOnClickListener(cardMenuListener(holder.menuFront, notes.get_id(), notes.getType()));
        holder.menuBack.setOnClickListener(cardMenuListener(holder.menuBack, notes.get_id(), notes.getType()));

        if (positionVisible.get(cursor.getPosition())) {

            if (holder.cardFlipLayout != null) {
                holder.cardFlipLayout.setOnLongClickListener(showNoteDialog(bundle, position));
                holder.cardFlipLayout.setOnClickListener(cardFlip(holder, position));
            }

        } else {
            holder.noteRootContainer.setOnLongClickListener(showNoteDialog(bundle, position));
            holder.noteRootContainer.setOnClickListener(cardFlip(holder, position));
        }

    }

    private Notes get(int position, Cursor cursor) {
        Notes notes = new Notes();

        if (cursor.moveToPosition(position)) {
            int _id = cursor.getInt(cursor.getColumnIndex(NoteEntry._ID));
            int type = cursor.getInt(cursor.getColumnIndex(NoteEntry.TYPE));
            String title = cursor.getString(cursor.getColumnIndex(NoteEntry.TITLE));
            String date = cursor.getString(cursor.getColumnIndex(NoteEntry.DATE));
            String time = cursor.getString(cursor.getColumnIndex(NoteEntry.TIME));
            String message = cursor.getString(cursor.getColumnIndex(NoteEntry.MESSAGE));
            String color = cursor.getString(cursor.getColumnIndex(NoteEntry.COLOR));

            notes.set_id(_id);
            notes.setType(type);
            notes.setTitle(title);
            notes.setDate(date);
            notes.setTime(time);
            notes.setMessage(message);
            notes.setColor(color);
        }
        return notes;
    }

    private void bindTodoList(ListView listView, String message) {
        String items = TodoJson.getMapFormatListString(message);
        ArrayList<String> todoList = new ArrayList<>(TodoJson.getItemsArray(items));
        NoteTodoList adapter = new NoteTodoList(todoList);
        listView.setAdapter(adapter);
    }

    private View.OnClickListener cardMenuListener(final View anchor, final int id, final int type) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(mContext, anchor);
                popup.getMenuInflater().inflate(R.menu.note, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_edit:
                                editNote(id, type);
                                break;
                            case R.id.menu_delete:
                                showDeleteConfirmationDialog(id);
                                break;
                        }

                        return true;
                    }
                });
                popup.show();
            }
        };
    }

    private View.OnClickListener cardFlip(final ViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                        R.animator.flip_right_out);

                AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                        R.animator.flip_left_in);

                boolean isBackVisible = positionVisible.get(position);

                if (!isBackVisible) {

                    if (holder.noteTodoList != null) {
                        holder.noteTodoList.setVisibility(View.VISIBLE);
                    }

                    setRightOut.setTarget(holder.notesViewFront);
                    setLeftIn.setTarget(holder.notesViewBack);
                    setRightOut.start();
                    setLeftIn.start();
                    positionVisible.put(position, true);
                } else {

                    if (holder.noteTodoList != null) {
                        holder.noteTodoList.setVisibility(View.GONE);
                    }

                    setRightOut.setTarget(holder.notesViewBack);
                    setLeftIn.setTarget(holder.notesViewFront);
                    setRightOut.start();
                    setLeftIn.start();
                    positionVisible.put(position, false);
                }
            }
        };
    }

    private View.OnLongClickListener showNoteDialog(final Bundle bundle, final int position) {
        return new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                final int type = !positionVisible.get(position) ?
                        CustomDialog.NOTE_FRONT : CustomDialog.NOTE_BACK;

                CustomDialog dialog = new CustomDialog(mContext, type, bundle.getInt("note_id"),
                        new CustomDialog.DialogCallback() {

                    @Override
                    public void AfterCancel(Cursor cursor) {
                        swapCursor(cursor);
                        notifyDataSetChanged();
                    }
                });
                dialog.show();
                dialog.setContent(bundle);
                dialog.setCanceledOnTouchOutside(true);
                return true;
            }
        };
    }

    private void showDeleteConfirmationDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure want to delete this note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri noteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
                int deleteNoteRow = mContext.getContentResolver().delete(noteUri, null,
                        null);
                if (deleteNoteRow != 0) {
                    Toast.makeText(mContext, "Note deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Error deleting note", Toast.LENGTH_SHORT).show();
                }
                callbacks.AfterDeletion();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void editNote(int id, int type) {
        Bundle bundle = new Bundle();
        Uri currentNoteUri = ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
        Class<?extends Fragment> fragment = FullScreenFragmentSelector.getFragmentClass(type);
        bundle.putString("note_uri", String.valueOf(currentNoteUri));

        FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment.Builder(mContext)
                .setTitle("Edit " + NoteEntry.getTypeString(type))
                .setConfirmButton("Save")
                .setContent(fragment, bundle)
                .build();

        FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
        dialogFragment.show(fm, "dialog");
    }

    public SparseBooleanArray getSparseBoolean() {
        return positionVisible;
    }

    public void setSparseBooleanArray(SparseBooleanArray sparseBooleanArray) {
        this.positionVisible = sparseBooleanArray;
    }

    public class ViewHolder {
        View view;
        TextView titleView;
        TextView dateView;
        TextView timeView;
        TextView messageView;
        TextView menuFront;
        TextView menuBack;
        CardView notesViewFront;
        CardView notesViewBack;
        ListView noteTodoList;
        LinearLayout cardFlipLayout;
        RelativeLayout noteRootContainer;


        public ViewHolder(View view, int type) {
            this.view = view;
            initUiViews(type);
        }

        private void initUiViews(int type) {
            if (type == NoteEntry.TYPE_TODO) {
                cardFlipLayout = view.findViewById(R.id.todo_list_layout);
                noteTodoList = view.findViewById(R.id.item_notes_todo_list);
            }

            messageView = view.findViewById(R.id.item_message);

            titleView = view.findViewById(R.id.item_title);
            dateView = view.findViewById(R.id.item_date);
            timeView = view.findViewById(R.id.item_time);
            menuFront = view.findViewById(R.id.item_menu_front);
            menuBack = view.findViewById(R.id.item_menu_back);
            notesViewFront = view.findViewById(R.id.item_notes_front);
            notesViewBack = view.findViewById(R.id.item_notes_back);
            noteRootContainer = view.findViewById(R.id.item_notes_container);
        }
    }

    public interface DatabaseCallbacks {
        void AfterDeletion();
    }
}
