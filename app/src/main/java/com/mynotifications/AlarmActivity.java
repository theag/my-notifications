package com.mynotifications;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyNotification.loadIfNull(this, getFilesDir());

        MyNotification note = MyNotification.getNextAlarm();
        //note.nextCheck(this);
        TextView tv = (TextView)findViewById(R.id.text_alarm);
        if(note != null) {
            tv.setText(note.name);
        } else {
            tv.setText("no alarm");
        }
    }
}
