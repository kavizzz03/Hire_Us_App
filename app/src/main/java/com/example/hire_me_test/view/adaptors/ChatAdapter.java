package com.example.hire_me_test.view.adaptors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<String> {

    public ChatAdapter(Context context, List<String> messages) {
        super(context, android.R.layout.simple_list_item_1, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        String message = getItem(position);
        if (message.startsWith("USER:")) {
            tv.setTextColor(0xFF007AFF); // Blue user messages
        } else if (message.startsWith("ADMIN:")) {
            tv.setTextColor(0xFF34C759); // Green admin messages
        }
        return tv;
    }
}
