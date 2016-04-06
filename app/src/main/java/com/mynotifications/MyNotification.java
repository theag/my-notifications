package com.mynotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/04/05.
 */
public class MyNotification {

    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    public static final int DAILY = 0;
    public static final int WEEKLY = 1;
    public static final int YEARLY = 2;

    public static final int FOREVER = 0;
    public static final int UNTIL = 1;
    public static final int FOR_NUMBER = 2;

    private static ArrayList<MyNotification> notifications = null;
    private static int nextAlarm = -1;
    private static PendingIntent pendingIntent = null;
    private static final String unitSep = ""+((char)31);

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
                        notifications.add(load(line));
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
    }

    private static MyNotification load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, unitSep);
        MyNotification note = new MyNotification();
        note.name = tokens.nextToken();
        note.repeatType = Integer.parseInt(tokens.nextToken());
        note.enabled = Boolean.parseBoolean(tokens.nextToken());
        note.repeatCount = Integer.parseInt(tokens.nextToken());
        note.start.setTimeInMillis(Long.parseLong(tokens.nextToken()));
        for(int i = 0; i < note.repeatDays.length; i++) {
            note.repeatDays[i] = Boolean.parseBoolean(tokens.nextToken());
        }
        note.untilChoice = Integer.parseInt(tokens.nextToken());
        note.untilCount = Integer.parseInt(tokens.nextToken());
        note.until.setTimeInMillis(Long.parseLong(tokens.nextToken()));
        note.happenCount = Integer.parseInt(tokens.nextToken());
        return note;
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

    private static void alarmReset(MyNotification sender, Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent = null;
        nextAlarm = -1;
        goToNextAlarm(context);
    }

    private static void goToNextAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent = null;
            nextAlarm = -1;
        }
        MyNotification next = null;
        int index = -1;
        for(int i = 0; i < notifications.size(); i++) {
            if(notifications.get(i).enabled) {
                if(next == null) {
                    next = notifications.get(i);
                    index = i;
                } else if(notifications.get(i).start.before(next.start)) {
                    next = notifications.get(i);
                    index = i;
                }
            }
        }

        if(next != null) {
            Intent intent = new Intent(context, NotificationPublisher.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, next.start.getTimeInMillis(), pendingIntent);
            nextAlarm = index;
        }
    }

    public static MyNotification getNextAlarm() {
        if(nextAlarm >= 0) {
            return notifications.get(nextAlarm);
        }
        return null;
    }

    public String name;
    public int repeatType;
    public boolean enabled;
    public int repeatCount;
    private Calendar start;
    public final boolean[] repeatDays; //monday, tuesday, ...
    public int untilChoice;
    public int untilCount;
    private Calendar until;
    private int happenCount;

    private MyNotification() {
        name = "";
        repeatType = 0;
        enabled = true;
        repeatCount = 1;
        start = Calendar.getInstance();
        repeatDays = new boolean[7];
        untilChoice = 0;
        untilCount = 5;
        until = Calendar.getInstance();
        happenCount = 0;
    }

    private String saveStr() {
        String rv = name +unitSep +repeatType +unitSep +enabled +unitSep +repeatCount +unitSep +start.getTimeInMillis();
        for(int i = 0; i < repeatDays.length; i++) {
            rv += unitSep +repeatDays[i];
        }
        rv += unitSep +untilChoice +unitSep +untilCount +unitSep +until.getTimeInMillis() +unitSep +happenCount;
        return rv;
    }

    public void reset(Context context) {
        happenCount = 0;
        if(enabled) {
            start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, until.get(Calendar.HOUR_OF_DAY));
            start.set(Calendar.MINUTE, until.get(Calendar.MINUTE));
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            if (start.before(Calendar.getInstance())) {
                goToNextAlarm();
            }
        }
        alarmReset(this, context);
    }

    public void nextCheck(Context context) {
        goToNextAlarm();
        switch(untilChoice) {
            case UNTIL:
                if(!Calendar.getInstance().before(until)) {
                    enabled = false;
                }
                break;
            case FOR_NUMBER:
                happenCount++;
                if(happenCount == untilCount) {
                    enabled = false;
                }
                break;
        }
        MyNotification.goToNextAlarm(context);
    }

    private void goToNextAlarm() {
        switch(repeatType) {
            case DAILY:
                start.add(Calendar.DAY_OF_MONTH, repeatCount);
                break;
            case WEEKLY:
                int current = getWeekDay(start);
                int next = -1;
                boolean stillInWeek = false;
                for(int i = 0; i < 7; i++) {
                    if(i <= current && repeatDays[i] && next < 0) {
                        next = i;
                    } else if(i > current && repeatDays[i]) {
                        stillInWeek = true;
                        next = i;
                        break;
                    }
                }
                if(stillInWeek) {
                    start.add(Calendar.DAY_OF_WEEK, next - current);
                } else {
                    start.add(Calendar.DAY_OF_WEEK, -current);
                    start.add(Calendar.WEEK_OF_MONTH, repeatCount);
                    start.add(Calendar.DAY_OF_WEEK, next);
                }
                break;
            case YEARLY:
                start.add(Calendar.YEAR, repeatCount);
                break;
        }
        SimpleDateFormat parser = new SimpleDateFormat("MMM d, yyyy h:mm:ss:SS aa");
        System.out.println(parser.format(start.getTime()));
    }

    private int getWeekDay(Calendar when) {
        switch(when.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
                return FRIDAY;
            case Calendar.SATURDAY:
                return SATURDAY;
            case Calendar.SUNDAY:
                return SUNDAY;
            default:
                return -1;
        }
    }

    public Date getStartDate() {
        return start.getTime();
    }

    public Date getUntilDate() {
        return until.getTime();
    }

    public void setTime(Calendar cal) {
        start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        start.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        if(start.before(Calendar.getInstance())) {
            goToNextAlarm();
        }
        until.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        until.set(Calendar.MONTH, cal.get(Calendar.MONTH));
        until.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
        until.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        until.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        until.set(Calendar.SECOND, 0);
        until.set(Calendar.MILLISECOND, 0);
    }
}
