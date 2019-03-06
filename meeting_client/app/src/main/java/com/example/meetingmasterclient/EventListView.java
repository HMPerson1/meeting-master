package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;

public class EventListView extends AppCompatActivity {
    int uid;

    ListView eventData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventData = findViewById(R.id.event_data);

        Call<MeetingService.UserProfile> c = Server.getService().getCurrentUser();
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT);
                        return;
                    }

                    uid = response.body().pk;

                    sendSearchRequest(0);
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListView eventData = findViewById(R.id.event_data);

        switch (item.getItemId()) {
            case R.id.this_week:
                sendSearchRequest(1);
                break;
            case R.id.this_month:
                sendSearchRequest(2);
                break;
            default:
                sendSearchRequest(0);
                break;
        }

        return true;
    }

    // TODO: Code request for event filtering
    public void sendSearchRequest(int period) {
        // period = 0 ==> today
        // period = 1 ==> this week
        // period = 2 ==> this month
    }

    /*
    private void sendSearchRequest(String url) {
        eventQueue = Volley.newRequestQueue(this);

        JsonArrayRequest eventRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String[][] eventInfo = new String[response.length()][4];

                try {
                    for (int i = 0; i < response.length(); i++) {
                        eventInfo[i][0] = response.getJSONObject(i).getString("admin");
                        eventInfo[i][1] = response.getJSONObject(i).getString("name");
                        eventInfo[i][2] = response.getJSONObject(i).getString("date");
                        eventInfo[i][3] = response.getJSONObject(i).getString("location");
                    }

                    EventViewAdapter results = new EventViewAdapter(getApplicationContext(), eventInfo);
                    eventData.setAdapter(results);
                    eventData.setVisibility(View.VISIBLE);
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "An error has occurred while processing the information", Toast.LENGTH_SHORT).show();
                } finally {
                    eventQueue.stop();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "An error has occurred in the network", Toast.LENGTH_SHORT).show();
                eventQueue.stop();
            }
        });

        eventQueue.add(eventRequest);
    }*/
}