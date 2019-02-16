package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

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

    public void setEventsToday(ListView eventData) {
        String[][] test = {{"a1", "a1", "a1"}, {"a2", "a2", "a2"}, {"a3", "a3", "a3"}};
        EventViewAdapter eva = new EventViewAdapter(getApplicationContext(), test);
        eventData.setAdapter(eva);
    }

    public void setEventsThisWeek(ListView eventData) {

    }

    public void setEventsThisMonth(ListView eventData) {

    }
}