package com.example.meetingmasterclient.utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

public class Route extends AsyncTask<Void, Void, JSONObject> {
    private static final String TAG = "Route";
    private GoogleMap mMap;
    private LatLng origin;
    private LatLng destination;

    public Route(GoogleMap mMap, LatLng origin, LatLng destination) {
        super();
        this.mMap = mMap;
        this.origin = origin;
        this.destination = destination;
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

            return new JSONObject(builder.toString());
        } catch(MalformedURLException ue) {
            Log.v(TAG, "URL error");
            return null;
        } catch(IOException ioe) {
            Log.v(TAG, "Input stream error");
            return null;
        } catch(JSONException je) {
            Log.v(TAG, "JSON error");
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject route) {
        try {
            JSONArray arr = route
                    .getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps");

            for (int i = 0; i < arr.length(); i++) {
                PolylineOptions options = new PolylineOptions();
                options.color(Color.BLUE);
                options.width(10);
                options.addAll(PolyUtil.decode(arr.getJSONObject(i).getJSONObject("polyline").getString("points")));

                mMap.addPolyline(options);
            }
        } catch(JSONException je) {
            Log.v(TAG, "JSON error");
        }
    }
}
