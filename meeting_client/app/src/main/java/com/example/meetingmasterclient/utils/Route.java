package com.example.meetingmasterclient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.meetingmasterclient.MapsActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Route extends AsyncTask<Void, Void, JSONObject> {
    private Context context;
    private GoogleMap mMap;
    private LatLng origin;
    private LatLng destination;
    private String eventDate;
    private String eventTime;

    public Route(Context context, GoogleMap mMap, LatLng origin, LatLng destination) {
        super();
        this.context = context;
        this.mMap = mMap;
        this.origin = origin;
        this.destination = destination;
    }

    public Route(Context context, LatLng origin, LatLng destination, String eventDate, String eventTime) {
        super();
        this.context = context;
        this.origin = origin;
        this.destination = destination;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?"
                    + "origin=" + origin.latitude + "," + origin.longitude
                    + "&destination=" + destination.latitude + "," + destination.longitude
                    + "&key=AIzaSyDXRxY5Bs0akDeBWYsBRGi8zVExod0HY2w");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            connection.disconnect();

            return new JSONObject(builder.toString());
        } catch(MalformedURLException ue) {
            Toast.makeText(context, "URL error", Toast.LENGTH_SHORT).show();
            return null;
        } catch(IOException ioe) {
            Toast.makeText(context, "Input stream error", Toast.LENGTH_SHORT).show();
            return null;
        } catch(JSONException je) {
            Toast.makeText(context, "JSON error", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject route) {
        try {
            JSONObject leg = route
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0);

            // The map is null if the alarm is being scheduled
            if (mMap == null && eventDate != null && eventTime != null) {
                int duration = leg.getJSONObject("duration").getInt("value");

                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(eventDate));
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eventTime.substring(0, 2)));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(eventTime.substring(3, 5)));
                    calendar.add(Calendar.SECOND, (-1) * duration);

                    Intent activate = new Intent(context, LeaveNowAlarm.class);
                    activate.putExtra("origin_lat", origin.latitude);
                    activate.putExtra("origin_long", origin.longitude);
                    activate.putExtra("dest_lat", destination.latitude);
                    activate.putExtra("dest_long", destination.longitude);
                    PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
                    AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pActivate);
                } catch(ParseException e) {
                    e.printStackTrace();
                }

            } else if (mMap != null && eventDate == null && eventTime == null) {
                JSONArray arr = leg.getJSONArray("steps");

                for (int i = 0; i < arr.length(); i++) {
                    PolylineOptions options = new PolylineOptions();
                    options.color(Color.BLUE);
                    options.width(10);
                    options.addAll(PolyUtil.decode(arr.getJSONObject(i).getJSONObject("polyline").getString("points")));
                    mMap.addPolyline(options);
                }
            } else {
                System.out.println("Error: Inconsistent values");
            }
        } catch(JSONException je) {
            System.err.println(route.toString());
        }
    }
}
