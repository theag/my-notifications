package com.mynotifications;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.mynotifications.notifications.MyNotification;
import com.mynotifications.notifications.NotificationControl;

public class MainActivity extends AppCompatActivity implements NotificationsAdapter.OnItemClickListener {

    public static final int[] iconIDs = {R.drawable.ic_notifications_on_24dp, R.drawable.ic_dawn_24dp, R.drawable.ic_wb_sunny_24dp, R.drawable.ic_night_24dp, R.drawable.ic_favorite_24dp, R.drawable.ic_work_24dp};
    public static final String[] iconNames = {"Default", "Morning", "Day", "Night", "Heart", "Work"};

    private static final int REQUEST_NEW = 1;
    private static final int REQUEST_EDIT = 2;
    private NotificationsAdapter adapter;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationControl.loadIfNull(this, getFilesDir());

        ListView lv = (ListView)findViewById(R.id.listView);
        adapter = new NotificationsAdapter(this);
        lv.setAdapter(adapter);

        refresh = (SwipeRefreshLayout)findViewById(R.id.refresh_list);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                refresh.setRefreshing(true);
                refreshList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshList() {
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationControl.save(getFilesDir());
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
        MyNotification note = NotificationControl.get(position);
        note.enabled = enabled;
        note.reset(this);
    }
}
