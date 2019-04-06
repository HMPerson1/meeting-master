package com.example.meetingmasterclient.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

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

                                            (new Route(context, currentLocation, destination, event.getPk())).execute();
                                        } catch(IOException e) {
                                            System.err.println("IO ERROR");
                                        }
                                    },
                                    (call, t) -> t.printStackTrace()
                            ));
                        }

                        /*
                        try {
                            ArrayList<Address> dest = (ArrayList<Address>)geocoder
                                    .getFromLocationName(event.getString("event_location"), 1);

                            LatLng destination = new LatLng(dest.get(0).getLatitude(), dest.get(0).getLongitude());

                            (new Route(context, currentLocation, destination,
                                    event.getInt("event_id"),
                                    event.getString("event_date"),
                                    event.getString("event_time"))).execute();
                        } catch(IOException e) {
                            System.err.println("IO ERROR");
                        }*/
                    } else {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch(SecurityException e) {
            Toast.makeText(context, "An error has occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
