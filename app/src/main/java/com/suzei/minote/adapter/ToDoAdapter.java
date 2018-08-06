package com.suzei.minote.adapter;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.suzei.minote.R;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private static final String TAG = "ToDoAdapter";

    private List<String> mTodoList;
    private RecyclerViewListener listener;
    private int type;

    public ToDoAdapter(List<String> mTodoList, int type, RecyclerViewListener listener) {
        this.mTodoList = mTodoList;
        this.listener = listener;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_todo, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = getMessage(position);
        String number = String.valueOf(position + 1) + ".)";


        holder.bind(number, message, isStrikeThrough(position));
        if (type == 1) {
            holder.removeView.setVisibility(View.GONE);
        }
    }

    private String getMessage(int position) {
        String singleItem = mTodoList.get(position);
        String[] itemSplit = singleItem.split(":");
        return itemSplit[0];
    }

    private Boolean isStrikeThrough(int position) {
        String singleItem = mTodoList.get(position);

        if (singleItem.contains(":")) {

            try {
                String[] itemSplit = singleItem.split(":");
                return Boolean.parseBoolean(itemSplit[1]);

            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "isStrikeThrough: ", e);
                e.printStackTrace();
            }

        }

        return false;
    }

    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView numberView;
        TextView messageView;
        ImageButton removeView;

        public ViewHolder(View itemView) {
            super(itemView);
            initUiViews();
        }

        private void initUiViews() {
            numberView = itemView.findViewById(R.id.todo_number);
            messageView = itemView.findViewById(R.id.todo_message);
            removeView = itemView.findViewById(R.id.todo_remove);
        }

        public void bind(String number, String message, boolean isStrikeThrough) {
            numberView.setText(number);
            messageView.setText(message);

            if (isStrikeThrough) {
                messageView.setPaintFlags(messageView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                messageView.setPaintFlags(messageView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            removeView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface RecyclerViewListener {
        void OnItemClick(int position);
    }
}
