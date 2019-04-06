package com.example.meetingmasterclient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;

public class StartingSoon {
    public static void scheduleStartingSoonAlarm(Context context, int eventId) {
        Call<MeetingService.EventsData> c = Server.getService().getEvents("/events/" + eventId);
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    MeetingService.EventsData event = response.body();

                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(event.event_date));

                        String eventTime = event.event_time;
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eventTime.substring(0, 2)));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(eventTime.substring(3, 5)));
                        calendar.add(Calendar.HOUR, -1);

                        Intent activate = new Intent(context, StartingSoonAlarm.class);
                        activate.putExtra("event_id", eventId);

                        PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
                        AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pActivate);
                    } catch(ParseException e) {
                        e.printStackTrace();
                    }
                },
                (call, t) -> t.printStackTrace()
            )
        );
    }
}
