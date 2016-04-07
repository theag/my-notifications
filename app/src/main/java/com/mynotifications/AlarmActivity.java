package com.mynotifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mynotifications.notifications.MyNotification;
import com.mynotifications.notifications.NotificationControl;

public class AlarmActivity extends AppCompatActivity {

    public static final String ARG_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NotificationControl.loadIfNull(this, getFilesDir());

        TextView tv = (TextView)findViewById(R.id.text_alarm);
        tv.setText(getIntent().getStringExtra(ARG_NAME));
    }
}
