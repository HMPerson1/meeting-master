package com.example.meetingmasterclient.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

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
import java.util.Random;

import retrofit2.Call;

public class Route extends AsyncTask<Void, Void, JSONObject> {
    public static LinkedList<Polyline> polylines = new LinkedList<>();
    private Context context;
    private GoogleMap mMap;
    private LatLng origin;
    // private LatLng[] origins;
    private LatLng destination;
    private int eventId;

    public Route(Context context, GoogleMap mMap, LatLng origin, LatLng destination) {
        super();
        this.context = context;
        this.mMap = mMap;
        this.origin = origin;
        this.destination = destination;
    }

    public Route(Context context, LatLng origin, LatLng destination, int event) {
        super();
        this.context = context;
        this.origin = origin;
        /*
        origins = new LatLng[1];
        origins[0] = origin;
        */
        this.destination = destination;
        this.eventId = event;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        // for (int i = 0; i < origins.length; i++)
        // TODO: Do for origins[i] rather than origin
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

            // TODO: Return array of JSON objects rather than only one
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
        // TODO: Check for size of array; if there is only one, schedule alarm; if there are more, draw the routes with a for loop
        try {
            JSONObject leg = route
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0);

            // The map is null if the alarm is being scheduled
            if (mMap == null) {
                int duration = leg.getJSONObject("duration").getInt("value");

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
                                calendar.add(Calendar.SECOND, (-1) * duration);

                                Intent activate = new Intent(context, LeaveNowAlarm.class);
                                activate.putExtra("event_id", event.getPk());
                                PendingIntent pActivate = PendingIntent.getBroadcast(context, 0, activate, 0);
                                AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                                alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pActivate);
                            } catch(ParseException e) {
                                e.printStackTrace();
                            }
                        },
                        (call, t) -> t.printStackTrace()
                ));
            } else {
                if (!polylines.isEmpty()) {
                    for (int i = 0; i < polylines.size(); i++) {
                        Polyline pol = polylines.remove();
                        pol.remove();
                    }
                }

                JSONArray arr = leg.getJSONArray("steps");

                for (int i = 0; i < arr.length(); i++) {
                    PolylineOptions options = new PolylineOptions();
                    options.color(randomColor());
                    options.width(10);
                    options.addAll(PolyUtil.decode(arr.getJSONObject(i).getJSONObject("polyline").getString("points")));
                    polylines.add(mMap.addPolyline(options));
                }
            }
        } catch(JSONException je) {
            System.err.println(route.toString());
        }
    }

    private int randomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
