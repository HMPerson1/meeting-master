package com.example.meetingmasterclient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;

public class StartingSoonAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Task locate = (LocationServices.getFusedLocationProviderClient(context)).getLastLocation();

            locate.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location current = (Location)task.getResult();
                        LatLng currentLocation = new LatLng(current.getLatitude(), current.getLongitude());

                        Geocoder geocoder = new Geocoder(context);

                        int eventId = intent.getIntExtra("event_id", 0);
                        if (eventId != 0) {
                            Call<MeetingService.EventsData> c = Server.getService().getEvents("/events/" + eventId);
                            c.enqueue(Server.mkCallback(
                                    (call, response) -> {
                                        MeetingService.EventsData event = response.body();
                                        MeetingService.LocationData location = event.event_location;

                                        try {
                                            ArrayList<Address> dest = (ArrayList<Address>)geocoder
                                                    .getFromLocationName(
                                                            location.getStreet_address()
                                                                    + ", "
                                                                    + location.getCity() + ", "
                                                                    + location.getState(),1);

                                            LatLng destination = new LatLng(dest.get(0).getLatitude(), dest.get(0).getLongitude());

                                            (new Route(context, currentLocation, destination, event.getPk(), event.getEvent_date(), event.getEvent_time())).execute();
                                        } catch(IOException e) {
                                            System.err.println("IO ERROR");
                                        }
                                    },
                                    (call, t) -> t.printStackTrace()
                            ));
                        }
                    } else {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch(SecurityException e) {
            Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show();
        }
    }

    public static void scheduleStartingSoonAlarm(Context context, int eventId) {
        Server.getService().getEventfromId(eventId).enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        MeetingService.EventsData event = response.body();
                        scheduleStartingSoonAlarm(context, eventId, event.getEvent_date(), event.getEvent_time());
                    } else {
                        Toast.makeText(context, "Error retrieving event", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
            ));
    }

    public static void scheduleStartingSoonAlarm(Context context, int eventId, String eventDate, String eventTime) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(eventDate));

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eventTime.substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(eventTime.substring(3, 5)));
            calendar.add(Calendar.HOUR, -1);

            Intent activate = new Intent(context, StartingSoonAlarm.class);
            activate.putExtra("event_id", eventId);

            PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
            AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pActivate);
            System.out.println("Alarm successful");
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    public static void cancelStartingSoonAlarm(Context context, int eventId) {
        Intent activate = new Intent(context, StartingSoonAlarm.class);
        activate.putExtra("event_id", eventId);

        PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
        AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(pActivate);
    }
}
