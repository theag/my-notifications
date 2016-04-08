package com.mynotifications;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Created by nbp184 on 2016/04/08.
 */
public class IconAdapter extends BaseAdapter {

    public static final int[] iconIDs = {R.drawable.ic_notifications_on_24dp, R.drawable.ic_dawn_24dp, R.drawable.ic_wb_sunny_24dp, R.drawable.ic_night_24dp, R.drawable.ic_favorite_24dp, R.drawable.ic_work_24dp};
    public static final String[] iconNames = {"Default", "Morning", "Day", "Night", "Heart", "Work"};

    private Context context;

    public IconAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return iconIDs.length;
    }

    @Override
    public Drawable getItem(int position) {
        return context.getDrawable(iconIDs[position]);
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
            view = inflater.inflate(R.layout.spinner_item_icons, null);
        }
        ImageView iv = (ImageView)view.findViewById(R.id.image_icon);
        iv.setImageDrawable(getItem(position));
        return view;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_dropdown_item_icons, null);
        }
        ImageView iv = (ImageView)view.findViewById(R.id.image_icon);
        iv.setImageDrawable(getItem(position));
        TextView tv = (TextView)view.findViewById(R.id.text_icon);
        tv.setText(iconNames[position]);
        return view;
    }
}
