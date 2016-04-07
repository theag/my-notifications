package com.mynotifications.notifications;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/04/05.
 */
public class MyNotification {

    public static final int b_MONDAY = 1;
    public static final int b_TUESDAY = 2;
    public static final int b_WEDNESDAY = 4;
    public static final int b_THURSDAY = 8;
    public static final int b_FRIDAY = 16;
    public static final int b_SATURDAY = 32;
    public static final int b_SUNDAY = 64;

    public static final int a_MONDAY = 0;
    public static final int a_TUESDAY = 1;
    public static final int a_WEDNESDAY = 2;
    public static final int a_THURSDAY = 3;
    public static final int a_FRIDAY = 4;
    public static final int a_SATURDAY = 5;
    public static final int a_SUNDAY = 6;

    public static final int DAILY = 0;
    public static final int WEEKLY = 1;
    public static final int MONTHLY = 2;
    public static final int YEARLY = 3;

    public static final int FOREVER = 0;
    public static final int UNTIL = 1;
    public static final int FOR_NUMBER = 2;

    protected static final String unitSep = ""+((char)31);
    public static final int WEEKEND = b_SATURDAY | b_SUNDAY;
    public static final int WEEKDAY = b_MONDAY | b_TUESDAY | b_WEDNESDAY | b_THURSDAY | b_FRIDAY;
    public static final String[][] weekDayDisplay = {{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}};

    public static int getWeekDay_a(Calendar when) {
        switch(when.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return a_MONDAY;
            case Calendar.TUESDAY:
                return a_TUESDAY;
            case Calendar.WEDNESDAY:
                return a_WEDNESDAY;
            case Calendar.THURSDAY:
                return a_THURSDAY;
            case Calendar.FRIDAY:
                return a_FRIDAY;
            case Calendar.SATURDAY:
                return a_SATURDAY;
            case Calendar.SUNDAY:
                return a_SUNDAY;
            default:
                return -1;
        }
    }

    protected static MyNotification load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, MyNotification.unitSep);
        MyNotification note = new MyNotification();
        note.name = tokens.nextToken();
        note.iconIndex = Integer.parseInt(tokens.nextToken());
        note.repeatType = Integer.parseInt(tokens.nextToken());
        note.enabled = Boolean.parseBoolean(tokens.nextToken());
        note.repeatCount = Integer.parseInt(tokens.nextToken());
        note.start.setTimeInMillis(Long.parseLong(tokens.nextToken()));
        note.next.setTimeInMillis(Long.parseLong(tokens.nextToken()));
        note.repeatDays = Integer.parseInt(tokens.nextToken());
        note.untilChoice = Integer.parseInt(tokens.nextToken());
        note.untilCount = Integer.parseInt(tokens.nextToken());
        note.until.setTimeInMillis(Long.parseLong(tokens.nextToken()));
        note.happenCount = Integer.parseInt(tokens.nextToken());
        return note;
    }

    public String name;
    public int repeatType;
    public boolean enabled;
    public int repeatCount;
    public int iconIndex;
    protected Calendar start;
    protected Calendar next;
    private int repeatDays;
    public int untilChoice;
    public int untilCount;
    private Calendar until;
    private int happenCount;

    protected MyNotification() {
        name = "";
        repeatType = 0;
        iconIndex = 0;
        enabled = true;
        repeatCount = 1;
        start = Calendar.getInstance();
        next = Calendar.getInstance();
        repeatDays = 0;
        untilChoice = 0;
        untilCount = 5;
        until = Calendar.getInstance();
        happenCount = 0;
    }

    protected String saveStr() {
        String rv = name +unitSep +iconIndex +unitSep +repeatType +unitSep +enabled +unitSep +repeatCount +unitSep
                + start.getTimeInMillis() +unitSep +next.getTimeInMillis() +unitSep +repeatDays
                +unitSep +untilChoice +unitSep +untilCount +unitSep +until.getTimeInMillis()
                +unitSep +happenCount;
        return rv;
    }

    public void reset(Context context) {
        happenCount = 0;
        if(enabled) {
            next = Calendar.getInstance();
            next.setTimeInMillis(start.getTimeInMillis());
            while(next.before(Calendar.getInstance())) {
                goToNextAlarm();
                happenCount++;
                if(untilChoice == FOR_NUMBER && happenCount == untilCount) {
                    enabled = false;
                    break;
                }
            }
        }
        NotificationControl.alarmReset(this, context);
    }

    public void goToNextAndCheck(Context context) {
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
        NotificationControl.goToNextAlarm(context);
    }

    protected void goToNextAlarm() {
        switch(repeatType) {
            case DAILY:
                next.add(Calendar.DAY_OF_MONTH, repeatCount);
                break;
            case WEEKLY:
                int currentDay = getWeekDay_a(next);
                int nextDay = -1;
                boolean stillInWeek = false;
                int pow2 = 1;
                for(int i = 0; i < 7; i++) {
                    if(i <= currentDay && (repeatDays & pow2) > 0 && nextDay < 0) {
                        nextDay = i;
                    } else if(i > currentDay && (repeatDays & pow2) > 0) {
                        stillInWeek = true;
                        nextDay = i;
                        break;
                    }
                    pow2 *= 2;
                }
                if(stillInWeek) {
                    next.add(Calendar.DAY_OF_WEEK, nextDay - currentDay);
                } else {
                    next.add(Calendar.DAY_OF_WEEK, -currentDay);
                    next.add(Calendar.WEEK_OF_MONTH, repeatCount);
                    next.add(Calendar.DAY_OF_WEEK, nextDay);
                }
                break;
            case MONTHLY:
                next.add(Calendar.MONTH, repeatCount);
                while(next.get(Calendar.DAY_OF_MONTH) != start.get(Calendar.DAY_OF_MONTH)) {
                    next.add(Calendar.MONTH, repeatCount);
                }
                break;
            case YEARLY:
                next.add(Calendar.YEAR, repeatCount);
                break;
        }
        SimpleDateFormat parser = new SimpleDateFormat("MMM d, yyyy h:mm:ss:SS aa");
        System.out.println("get next: " + parser.format(next.getTime()));
    }

    public Date getStartDate() {
        return start.getTime();
    }

    public Date getUntilDate() {
        return until.getTime();
    }

    public String getDetails() {
        SimpleDateFormat timeParser = new SimpleDateFormat("h:mm aa");
        SimpleDateFormat dateParser1 = new SimpleDateFormat("MMM d, yyyy");
        SimpleDateFormat dateParser2 = new SimpleDateFormat("MMM d");
        String rv = "???";
        switch(repeatType) {
            case DAILY:
                rv = "Every " +repeatCount +" day(s) at " +timeParser.format(next.getTime());
                break;
            case WEEKLY:
                rv = "Every " +repeatCount +" week(s) on ";
                if(repeatDays == WEEKDAY) {
                    rv += "weekdays ";
                } else if(repeatDays == WEEKEND) {
                    rv += " weekends ";
                } else {
                    int pow2 = 1;
                    for(int i = 0; i < 7; i++) {
                        if((repeatDays & pow2) > 0) {
                            rv += weekDayDisplay[0][i] +", ";
                        }
                    }
                }
                rv = rv.substring(0, rv.length() - 2) +" at " +timeParser.format(next.getTime());
                break;
            case MONTHLY:
                int dayOfMonth = next.get(Calendar.DAY_OF_MONTH);
                rv = "Every " +repeatCount +" month(s) at " +timeParser.format(next.getTime()) +" on the" +dayOfMonth;
                switch(dayOfMonth) {
                    case 1:
                        rv += "st";
                        break;
                    case 2:
                        rv += "nd";
                        break;
                    default:
                        rv += "th";
                }
                break;
            case YEARLY:
                rv = "Every " +repeatCount +" year(s) at " +timeParser.format(next.getTime()) +" on " +dateParser2.format(next.getTime());
                break;
        }
        switch(untilChoice) {
            case UNTIL:
                rv += " until " +dateParser1.format(until.getTime());
                break;
            case FOR_NUMBER:
                rv += " for " +(untilCount-happenCount) +"/" +untilCount +" more events";
                break;
        }
        return rv;
    }

    public boolean doesRepeatOn_a(int i) {
        int pow2 = 1;
        for(int p = 0; p <= i; p++) {
            pow2 *= 2;
        }
        return (repeatDays & pow2) > 0;
    }

    public void setRepeatOn_a(int i, boolean value) {
        int pow2 = 1;
        for(int p = 0; p <= i; p++) {
            pow2 *= 2;
        }
        if(value) {
            repeatDays |= pow2;
        } else {
            repeatDays &= -pow2;
        }
    }

    public void setStartTime(Calendar cal) {
        start = Calendar.getInstance();
        start.setTimeInMillis(cal.getTimeInMillis());
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
    }

    public void setUntilTime(Calendar cal) {
        until = Calendar.getInstance();
        until.setTimeInMillis(cal.getTimeInMillis());
        until.set(Calendar.SECOND, 0);
        until.set(Calendar.MILLISECOND, 0);
    }
}
