package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class EventListView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView eventData = findViewById(R.id.event_data);
        setEventsToday(eventData);
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
                setEventsThisWeek(eventData);
                break;
            case R.id.this_month:
                setEventsThisMonth(eventData);
                break;
            default:
                setEventsToday(eventData);
                break;
        }

        return true;
    }

    // TODO: Program methods to send an HTTP request to the server and parse the response

    private void setEventsToday(ListView eventData) {
        /*String[][] test = {{"a1", "a1", "a1", "a1"}, {"a2", "a2", "a2", "a2"}, {"a3", "a3", "a3", "a3"}};
        EventViewAdapter eva = new EventViewAdapter(getApplicationContext(), test);
        eventData.setAdapter(eva);*/

        JSONObject params = new JSONObject();
        // TODO: Add parameters to JSON


        // TODO: Insert URL in the method
        sendSearchRequest("", params);
    }

    private void setEventsThisWeek(ListView eventData) {
        JSONObject params = new JSONObject();
        // TODO: Add parameters to JSON


        // TODO: Insert URL in the method
        sendSearchRequest("", params);
    }

    private void setEventsThisMonth(ListView eventData) {
        JSONObject params = new JSONObject();
        // TODO: Add parameters to JSON


        // TODO: Insert URL in the method
        sendSearchRequest("", params);
    }

    private void sendSearchRequest(String url, JSONObject params) {
        JsonObjectRequest eventRequest = new JsonObjectRequest(Request.Method.GET, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO: Parse JSON response and display information on ListView

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Define request queue and add request to the queue

    }
}