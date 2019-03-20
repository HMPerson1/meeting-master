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
    private Menu optionsMenu;
    private byte timePeriod;
    private ListView eventData;
    private int numberOfEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventData = findViewById(R.id.event_data);

        getInvitationByUser(getIntent().getIntExtra("user_id", -1));
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
                break;
        }

        return true;
    }

    private void getInvitationByUser(int user_id) {
        if (user_id == -1) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT);
            return;
        }

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
        numberOfEvents = invitations.size();

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

        do {} while (events.size() < numberOfEvents);

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
}