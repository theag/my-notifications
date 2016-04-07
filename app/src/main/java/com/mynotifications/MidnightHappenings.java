package com.mynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mynotifications.notifications.MyNotification;
import com.mynotifications.notifications.NotificationControl;

/**
 * Created by nbp184 on 2016/04/07.
 */
public class MidnightHappenings extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationControl.midnightCheck();
        MyNotification note = NotificationControl.getNextAlarm();
        note.goToNextAndCheck(context);
    }

}
