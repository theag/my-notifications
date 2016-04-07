package com.mynotifications.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mynotifications.MidnightHappenings;
import com.mynotifications.NotificationPublisher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nbp184 on 2016/04/07.
 */
public class NotificationControl {

    private static ArrayList<MyNotification> notifications = null;
    private static MyNotification midnight = null;
    private static int nextAlarm = -1;
    private static PendingIntent pendingIntent = null;

    public static void loadIfNull(Context context, File dir) {
        if(notifications == null) {
            notifications = new ArrayList<>();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            pendingIntent = alarmManager.getNextAlarmClock().getShowIntent();
            File file = new File(dir, "notifications.txt");
            if(file.exists()) {
                try {
                    BufferedReader inFile = new BufferedReader(new FileReader(file));
                    nextAlarm = Integer.parseInt(inFile.readLine());
                    String line = inFile.readLine();
                    while(line != null) {
                        notifications.add(MyNotification.load(line));
                        line = inFile.readLine();
                    }
                    inFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    file.delete();
                }
            }
        }
        if(midnight == null) {
            midnight = new MyNotification();
            midnight.repeatType = MyNotification.DAILY;
            midnight.enabled = true;
            midnight.repeatCount = 1;
            midnight.start = Calendar.getInstance();
            midnight.start.set(Calendar.HOUR_OF_DAY, 23);
            midnight.start.set(Calendar.MINUTE, 59);
            midnight.start.set(Calendar.SECOND, 59);
            midnight.start.set(Calendar.MILLISECOND, 59);
            midnight.next.setTimeInMillis(midnight.start.getTimeInMillis());
            midnight.untilChoice = MyNotification.FOREVER;
        }
    }

    public static void save(File dir) {
        try {
            PrintWriter outFile = new PrintWriter(new File(dir, "notifications.txt"));
            outFile.println(nextAlarm);
            for(MyNotification note : notifications) {
                outFile.println(note.saveStr());
            }
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MyNotification makeNew() {
        if(notifications == null) {
            notifications = new ArrayList<>();
        }
        notifications.add(new MyNotification());
        return notifications.get(notifications.size() - 1);
    }

    public static MyNotification get(int index) {
        return notifications.get(index);
    }

    public static int getCount() {
        return notifications.size();
    }

    public static void remove(Context context,int index) {
        MyNotification note = notifications.remove(index);
        if(nextAlarm == notifications.indexOf(note)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent = null;
            nextAlarm = -1;
            goToNextAlarm(context);
        }
    }

    protected static void alarmReset(MyNotification sender, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent = null;
        nextAlarm = -1;
        goToNextAlarm(context);
    }

    protected static void goToNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent = null;
            nextAlarm = -1;
        }
        MyNotification next = midnight;
        int index = -1;
        for(int i = 0; i < notifications.size(); i++) {
            if(notifications.get(i).enabled) {
                if(notifications.get(i).next.before(next.next)) {
                    next = notifications.get(i);
                    index = i;
                }
            }
        }

        if(index < 0) {
            Intent intent = new Intent(context, MidnightHappenings.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, next.next.getTimeInMillis(), pendingIntent);
            nextAlarm = index;
        } else {
            SimpleDateFormat parser = new SimpleDateFormat("MMM d, yyyy h:mm:ss:SS aa");
            System.out.println("settting \"" +next.name +"\" at " +parser.format(next.next.getTime()));
            Intent intent = new Intent(context, NotificationPublisher.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, next.next.getTimeInMillis(), pendingIntent);
            nextAlarm = index;
        }
    }

    public static MyNotification getNextAlarm() {
        if(nextAlarm >= 0) {
            return notifications.get(nextAlarm);
        }
        return midnight;
    }

    public static void midnightCheck() {
        for(MyNotification note : notifications) {
            if(note.next.before(midnight.next)) {
                note.goToNextAlarm();
            }
        }
    }

}
