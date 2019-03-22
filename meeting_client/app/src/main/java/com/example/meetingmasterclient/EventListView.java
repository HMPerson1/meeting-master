package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;

public class EventListView extends AppCompatActivity {
    private int user_id;
    private Menu optionsMenu;
    private byte timePeriod;
    private ListView eventData;
    private boolean declined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventData = findViewById(R.id.event_data);
        user_id = getIntent().getIntExtra("user_id", -1);
        declined = false;
        getInvitationByUser();
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
        MenuItem declinedCheckbox = optionsMenu.findItem(R.id.show_declined_events);

        switch (item.getItemId()) {
            case R.id.today:
                timePeriod = 0;
                getInvitationByUser();
                break;
            case R.id.this_week:
                timePeriod = 1;
                getInvitationByUser();
                break;
            case R.id.this_month:
                timePeriod = 2;
                getInvitationByUser();
                break;
            case R.id.show_declined_events:
                declined = !declinedCheckbox.isChecked();
                declinedCheckbox.setChecked(declined);
                getInvitationByUser();
                break;
            default:
                break;
        }

        return true;
    }

    private void getInvitationByUser() {
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
        List<MeetingService.EventData> events = new LinkedList<MeetingService.EventData>();
        Calendar today = Calendar.getInstance();

        for (MeetingService.InvitationData inv : invitations) {
            Call<MeetingService.EventData> c = Server.getService().getEvent("/events/" + inv.event_id + "/");
            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (response.isSuccessful()) {
                            MeetingService.EventData event = response.body();

                            try {
                                Calendar eventCal = Calendar.getInstance();
                                eventCal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(event.event_date));
                                eventCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.event_time.substring(0, 2)));
                                eventCal.set(Calendar.MINUTE, Integer.parseInt(event.event_time.substring(3)));
                                boolean timeIsConsistent;
                                boolean isDeclined;

                                switch(timePeriod) {
                                    case 0:
                                        timeIsConsistent = eventCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                                                && eventCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                                &&
                                                (eventCal.get(Calendar.HOUR_OF_DAY) > today.get(Calendar.HOUR_OF_DAY)
                                                || (eventCal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
                                                        && eventCal.get(Calendar.MINUTE) >= today.get(Calendar.MINUTE)));
                                        break;
                                    case 1:
                                        timeIsConsistent = eventCal.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)
                                                && eventCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                                &&
                                                (eventCal.get(Calendar.DAY_OF_WEEK) > eventCal.get(Calendar.DAY_OF_WEEK)
                                                || (eventCal.get(Calendar.DAY_OF_WEEK) == eventCal.get(Calendar.DAY_OF_WEEK)
                                                        &&
                                                        (eventCal.get(Calendar.HOUR_OF_DAY) > today.get(Calendar.HOUR_OF_DAY)
                                                        || (eventCal.get(Calendar.HOUR_OF_DAY) == today.get(Calendar.HOUR_OF_DAY)
                                                                && eventCal.get(Calendar.MINUTE) >= eventCal.get(Calendar.MINUTE)))));
                                        break;
                                    case 2:
                                        timeIsConsistent = eventCal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                                                && eventCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                                &&
                                                (eventCal.get(Calendar.WEEK_OF_MONTH) == today.get(Calendar.WEEK_OF_MONTH)
                                                        && eventCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                                                        &&
                                                        (eventCal.get(Calendar.DAY_OF_WEEK) > eventCal.get(Calendar.DAY_OF_WEEK)
                                                                || (eventCal.get(Calendar.DAY_OF_WEEK) == eventCal.get(Calendar.DAY_OF_WEEK)
                                                                &&
                                                                (eventCal.get(Calendar.HOUR) > today.get(Calendar.HOUR)
                                                                        || (eventCal.get(Calendar.HOUR) == today.get(Calendar.HOUR)
                                                                        && eventCal.get(Calendar.MINUTE) >= eventCal.get(Calendar.MINUTE))))));
                                        break;
                                    default:
                                        timeIsConsistent = false;
                                        break;
                                }

                                isDeclined = inv.status == 3;

                                if (timeIsConsistent && (declined || !isDeclined)) {
                                    events.add(event);
                                }
                            } catch(ParseException e) {}
                        } else {
                            // TODO: Parse error
                            //Server.parseUnsuccessful(response, MeetingService.EventDetailsError.class(), System.out::println, System.out::println);
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }

        eventData.setAdapter(new EventViewAdapter(getApplicationContext(), events));

        eventData.setOnItemClickListener(
            (parent, view, position, id) -> {
                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                intent.putExtra("event_id", ((MeetingService.EventData)eventData.getItemAtPosition(position)).id);
                startActivity(intent);
            }
        );

        eventData.setVisibility(View.VISIBLE);
    }
}