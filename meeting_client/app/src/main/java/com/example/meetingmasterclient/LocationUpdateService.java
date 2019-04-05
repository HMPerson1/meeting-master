package com.example.meetingmasterclient;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.Notifications;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;

/**
 * Periodically sends this device's current location to the server
 * <p>Use {@link #start} to start this service.
 * This should be stopped when the user arrives at their destination (either home or the event).
 * <p><b>NOTE:</b> <i>Before starting this service</i>, make sure location is enabled using
 * {@link SettingsClient#checkLocationSettings}(use {@link #LOCATION_REQUEST} as the location request)
 * <b>and</b> make sure that we have the permission {@link Manifest.permission#ACCESS_FINE_LOCATION}.
 * <p>see https://developer.android.com/training/location/change-location-settings.html
 */
public class LocationUpdateService extends Service {
    /**
     * The {@link LocationRequest} we give to {@link FusedLocationProviderClient#requestLocationUpdates}.
     */
    static final LocationRequest LOCATION_REQUEST = new LocationRequest()
            .setMaxWaitTime(60000)
            .setInterval(30000)
            .setFastestInterval(10000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private static final String TAG = "LocationUpdateService";
    private static final int NOTIFICATION_ID = R.string.desc_service_location_update;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // send location to server
            Location location = locationResult.getLastLocation();
            Server.getService().putCurrentLocation(
                    new MeetingService.CurrentLocationData(location.getLatitude(), location.getLongitude())
            ).enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (!response.isSuccessful()) {
                            Log.w(TAG, "Error posting location (response code): " + response);
                        }
                    },
                    (call, throwable) ->
                            Log.w(TAG, "Error posting location", throwable)));
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            Log.d(TAG, "onLocationAvailability() called with: locationAvailability = [" + locationAvailability + "]");
            if (!locationAvailability.isLocationAvailable()) {
                Toast.makeText(LocationUpdateService.this, "Cannot get location updates: location not available", Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * Helper to start this service.
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    static void start(Context context, int eventId) {
        Intent intent = new Intent(context, LocationUpdateService.class);
        intent.putExtra("event_id", eventId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    @Override
    public int onStartCommand(Intent startedIntent, int flags, int startId) {
        locationProviderClient.requestLocationUpdates(
                LOCATION_REQUEST, locationCallback, Looper.getMainLooper()
        ).addOnSuccessListener(_v -> {
            Log.i(TAG, "Successfully registered for location updates");
            // create notification and its intent
            Intent resultIntent = new Intent(this, EventDetails.class);
            resultIntent.putExtra("event_id", startedIntent.getIntExtra("event_id", -1));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Notifications.CHANNEL_LIVE_LOCATION_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.notif_service_tracking_location_title))
                    .setContentText(getString(R.string.notif_service_tracking_location_body))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            startForeground(NOTIFICATION_ID, builder.build());
        }).addOnFailureListener(e -> {
            Log.w(TAG, "requestLocationUpdates failed", e);
            Toast.makeText(this, "Cannot get location updates", Toast.LENGTH_LONG).show();
            stopSelf();
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
