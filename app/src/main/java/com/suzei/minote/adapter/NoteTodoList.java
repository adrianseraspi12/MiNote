package com.suzei.minote.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suzei.minote.R;

import java.util.List;

public class NoteTodoList extends BaseAdapter {

    private List<String> todoList;

    public NoteTodoList(List<String> todoList) {
        this.todoList = todoList;
    }

    @Override
    public int getCount() {
        return todoList.size();
    }

    @Override
    public String getItem(int position) {
        return todoList.get(position);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_todo_list,
                    parent, false);
            textView = convertView.findViewById(R.id.todo_list_text);
        }
        String[] item = getItem(position).split(":");
        String full_text = (position + 1) + ".) " + item[0];
        textView.setText(full_text);

        if (item[1].equals("true")) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return convertView;
    }
}
