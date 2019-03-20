package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;

public class EventListView extends AppCompatActivity {
    Menu optionsMenu;
    byte timePeriod;
    ListView eventData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventData = findViewById(R.id.event_data);

        // TODO: Get current user (somehow) to obtain their events only
        //sendSearchRequest((byte)0, false);

        /*Call<MeetingService.UserProfile> c = Server.getService().getCurrentUser();
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT);
                        return;
                    }

                    sendSearchRequest((byte)0, false);
                },
                (call, t) -> t.printStackTrace()
        ));*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_list_view, menu);
        optionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListView eventData = findViewById(R.id.event_data);
        MenuItem declined = optionsMenu.findItem(R.id.show_declined_events);

        switch (item.getItemId()) {
            case R.id.today:
                //sendSearchRequest((byte)0, declined.isChecked());
                break;
            case R.id.this_week:
                //sendSearchRequest((byte)1, declined.isChecked());
                break;
            case R.id.this_month:
                //sendSearchRequest((byte)2, declined.isChecked());
                break;
            case R.id.show_declined_events:
                declined.setChecked(!declined.isChecked());
                //sendSearchRequest(timePeriod, declined.isChecked());
                break;
            default:
                // just to avoid trouble
                break;
        }

        return true;
    }

    private void getInvitationByUser(int user_id) {
        Call<List<MeetingService.InvitationData>> c = Server.getService().getInvitations("/invitations/" + user_id + "/");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        getEventByInvitation(response.body());
                    } else {
                        // TODO: Parse error
                        //Server.parseUnsuccessful(response, MeetingService.InvitationDataError.class(), System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void getEventByInvitation(List<MeetingService.InvitationData> invitations) {
        List<MeetingService.EventData> events = new LinkedList<MeetingService.EventData>();

        for (MeetingService.InvitationData inv : invitations) {
            Call<MeetingService.EventData> c = Server.getService().getEvent("/events/" + inv.event_id + "/");
            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (response.isSuccessful()) {
                            events.add(response.body());
                        } else {
                            // TODO: Parse error
                            //Server.parseUnsuccessful(response, MeetingService.EventDetailsError.class(), System.out::println, System.out::println);
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }

        while (events.size() == 0) {}

        eventData.setAdapter(new EventViewAdapter(getApplicationContext(), events));
        eventData.setVisibility(View.VISIBLE);
    }

    // TODO: Code request for event filtering
    public void sendSearchRequest(byte period, boolean declined) {
        timePeriod = period;
        // period = 0 ==> today
        // period = 1 ==> this week
        // period = 2 ==> this month
        // declined = true ==> show declined events
        // declined = false ==> do not show declined events
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