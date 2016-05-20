package com.mynotifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mynotifications.notifications.MyNotification;
import com.mynotifications.notifications.NotificationControl;

import java.text.SimpleDateFormat;

/**
 * Created by nbp184 on 2016/04/05.
 */
public class NotificationPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationControl.loadIfNull(context, context.getFilesDir());

        MyNotification note = NotificationControl.getNextAlarm();

        Intent noteIntent = new Intent(context, AlarmActivity.class);
        noteIntent.putExtra(AlarmActivity.ARG_NAME, note.name);
        noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AlarmActivity.class);
        stackBuilder.addNextIntent(noteIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        SimpleDateFormat timeParser = new SimpleDateFormat("h:mm aa");
        //SimpleDateFormat timeParser = new SimpleDateFormat("MMM d, yyyy h:mm:ss:SS aa");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(IconAdapter.iconIDs[note.iconIndex])
                .setContentTitle(note.name)
                .setContentText(timeParser.format(note.getStartDate()))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setShowWhen(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(2)
                .setOnlyAlertOnce(true);

        note.goToNextAndCheck(context);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
}
