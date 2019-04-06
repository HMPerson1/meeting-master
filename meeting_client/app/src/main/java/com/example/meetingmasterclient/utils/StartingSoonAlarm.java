package com.example.meetingmasterclient.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class StartingSoonAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Task location = (LocationServices.getFusedLocationProviderClient(context)).getLastLocation();

            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location current = (Location)task.getResult();
                        LatLng currentLocation = new LatLng(current.getLatitude(), current.getLongitude());

                        Geocoder geocoder = new Geocoder(context);

                        try {
                            ArrayList<Address> dest = (ArrayList<Address>)geocoder
                                    .getFromLocationName(intent.getStringExtra("event_location"), 1);

                            LatLng destination = new LatLng(dest.get(0).getLatitude(), dest.get(0).getLongitude());

                            (new Route(context, currentLocation,
                                    destination, intent.getStringExtra("event_date"),
                                    intent.getStringExtra("event_time"))).execute();
                        } catch(IOException e) {
                            System.err.println("IO ERROR");
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
}
