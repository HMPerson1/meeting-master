package com.example.meetingmasterclient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StartingSoon {
    public static void scheduleStartingSoonAlarm(Context context, String eventDate, String eventTime, String eventLocation) {
        try {
            // Set the time of the alarm
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(eventDate));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eventTime.substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(eventTime.substring(3, 5)));
            calendar.add(Calendar.HOUR, -1);

            // Setting the alarm
            Intent activate = new Intent(context, StartingSoonAlarm.class);
            activate.putExtra("event_date", eventDate);
            activate.putExtra("event_time", eventTime);
            activate.putExtra("event_location", eventLocation);
            PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
            AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pActivate);
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }
}
