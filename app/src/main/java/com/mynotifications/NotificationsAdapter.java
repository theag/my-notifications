package com.mynotifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by nbp184 on 2016/04/05.
 */
public class NotificationsAdapter extends BaseAdapter implements ListAdapter, View.OnClickListener {

    private Context context;
    private OnItemClickListener listener = null;

    public NotificationsAdapter(Context context) {
        this.context = context;
        if(context instanceof OnItemClickListener) {
            listener = (OnItemClickListener)context;
        }
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return MyNotification.getCount();
    }

    @Override
    public MyNotification getItem(int position) {
        return MyNotification.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_notification, null);
        }
        TextView tv = (TextView)view.findViewById(R.id.text_position);
        tv.setText(""+position);
        MyNotification note = getItem(position);
        tv = (TextView)view.findViewById(R.id.text_name);
        tv.setText(note.name);
        tv.setOnClickListener(this);
        Switch sw = (Switch)view.findViewById(R.id.switch_enabled);
        sw.setChecked(note.enabled);
        sw.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            int position = getPosition(v);
            switch(v.getId()) {
                case R.id.text_name:
                    listener.onNameClick(position);
                    break;
                case R.id.switch_enabled:
                    listener.onEnabledClick(position, ((Switch)v).isChecked());
                    break;
            }
        }
    }

    private int getPosition(View view) {
        ViewGroup parent = (ViewGroup)view.getParent();
        TextView pos = (TextView)parent.findViewById(R.id.text_position);
        return Integer.parseInt(pos.getText().toString());
    }

    public interface OnItemClickListener {
        void onNameClick(int position);
        void onEnabledClick(int position, boolean enabled);
    }
}
