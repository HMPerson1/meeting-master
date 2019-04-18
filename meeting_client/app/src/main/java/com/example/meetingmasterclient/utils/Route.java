package com.example.meetingmasterclient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.meetingmasterclient.MapsActivity;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;

public class Route extends AsyncTask<Void, Void, JSONObject[]> {
    public static LinkedList<Polyline> polylines = new LinkedList<>();
    private Context context;
    private MapsActivity mapActivity;
    private LatLng[] origins;
    private LatLng destination;
    private int eventId;
    private String eventDate;
    private String eventTime;

    public Route(MapsActivity mapActivity, LatLng[] origins, LatLng destination) {
        super();
        this.mapActivity = mapActivity;
        this.context = mapActivity.getApplicationContext();
        this.origins = origins;
        this.destination = destination;
    }

    public Route(Context context, LatLng origin, LatLng destination, int eventId, String eventDate, String eventTime) {
        super();
        this.context = context;
        origins = new LatLng[1];
        origins[0] = origin;
        this.destination = destination;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    @Override
    protected JSONObject[] doInBackground(Void... params) {
        try {
            JSONObject[] routes = new JSONObject[origins.length];

            URL url;
            HttpURLConnection connection;
            BufferedReader reader;
            StringBuilder builder;
            String line = "";

            for (int i = 0; i < origins.length; i++) {
                url = new URL("https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + origins[i].latitude + "," + origins[i].longitude
                        + "&destination=" + destination.latitude + "," + destination.longitude
                        + "&key=AIzaSyDXRxY5Bs0akDeBWYsBRGi8zVExod0HY2w");

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();

                connection.disconnect();

                routes[i] = new JSONObject(builder.toString());
            }

            return routes;
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
    protected void onPostExecute(JSONObject[] routes) {
        if (routes == null) {
            return;
        }

        try {
            if (eventDate != null && eventTime != null) {
                int duration = routes[0]
                        .getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONObject("duration")
                        .getInt("value");

                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(eventDate));

                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(eventTime.substring(0, 2)));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(eventTime.substring(3, 5)));
                    calendar.add(Calendar.SECOND, (-1) * duration);

                    Intent activate = new Intent(context, LeaveNowAlarm.class);
                    activate.putExtra("event_id", eventId);
                    PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
                    AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pActivate);
                } catch(ParseException e) {
                    e.printStackTrace();
                }
            } else {
                int i;

                if (!polylines.isEmpty()) {
                    for (i = 0; i < polylines.size(); i++) {
                        Polyline pol = polylines.remove();
                        pol.remove();
                    }
                }

                int color;
                for (i = 0; i < routes.length; i++) {
                    color = randomColor();

                    JSONArray arr = routes[i]
                            .getJSONArray("routes")
                            .getJSONObject(0)
                            .getJSONArray("legs")
                            .getJSONObject(0)
                            .getJSONArray("steps");

                    for (int j = 0; j < arr.length(); j++) {
                        PolylineOptions options = new PolylineOptions();
                        options.color(color);
                        options.width(10);
                        options.addAll(PolyUtil.decode(
                                arr.getJSONObject(j)
                                        .getJSONObject("polyline")
                                        .getString("points")));
                        polylines.add(mapActivity.mMap.addPolyline(options));
                    }
                }

                mapActivity.onRoutesCompleted(routes);
            }
        } catch(JSONException je) {
            System.err.println("JSON Error 2");
        }
    }

    private int randomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
