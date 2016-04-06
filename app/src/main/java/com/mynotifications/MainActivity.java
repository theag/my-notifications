package com.mynotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NotificationsAdapter.OnItemClickListener {

    private static final int REQUEST_NEW = 1;
    private static final int REQUEST_EDIT = 2;
    private NotificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyNotification.loadIfNull(this, getFilesDir());

        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 8);
        SimpleDateFormat parser = new SimpleDateFormat("MMM d, yyyy h:mm:ss aa");
        System.out.println(parser.format(cal.getTime()));*/

        ListView lv = (ListView)findViewById(R.id.listView);
        adapter = new NotificationsAdapter(this);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyNotification.save(getFilesDir());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void addNew(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, REQUEST_NEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_NEW:
            case REQUEST_EDIT:
                if(resultCode == RESULT_OK) {
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onNameClick(int position) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.ARG_INDEX, position);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void onEnabledClick(int position, boolean enabled) {
        MyNotification note = MyNotification.get(position);
        note.enabled = enabled;
        note.reset(this);
    }
}
