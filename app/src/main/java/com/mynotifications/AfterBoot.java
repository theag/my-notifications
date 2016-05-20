package com.mynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mynotifications.notifications.MyNotification;
import com.mynotifications.notifications.NotificationControl;

/**
 * Created by nbp184 on 2016/05/20.
 */
public class AfterBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationControl.loadIfNull(context, context.getFilesDir());
            NotificationControl.startFromBoot(context);
        }
    }
}
