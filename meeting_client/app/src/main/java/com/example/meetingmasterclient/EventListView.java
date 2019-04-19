package com.example.meetingmasterclient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Locale;

import retrofit2.Call;

public class EventListView extends AppCompatActivity {
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(this, EventCreation.class)));

        eventData = findViewById(R.id.event_data);
        declined = false;
        getInvitationByUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_event_list_view, menu);
        optionsMenu = menu;
        return true;
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
            case R.id.item_sync_calendar:
                doSyncCalendar();
                break;
            case R.id.view_invitations:
                startActivity(new Intent(this, InvitationListActivity.class));
                break;
            case R.id.edit_profile:
                startActivity(new Intent(this, ProfileEdition.class));
                break;
            case R.id.user_search:
                startActivity(new Intent(this, UserSearch.class));
                break;
            case R.id.debug_menu:
                startActivity(new Intent(this, DebugLauncherActivity.class));
                break;
            default:
                break;
        }

        return true;
    }

    private void doSyncCalendar() {
        Server.getService().getIcalUrl().enqueue(Server.mkCallback((call, response) -> {
            if (response.isSuccessful()) {
                assert response.body() != null;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(response.body().ical_url));
                startActivity(intent);
            } else {
                Toast.makeText(this, "sync cal error", Toast.LENGTH_SHORT).show();
            }
        }, (call, t) -> t.printStackTrace()));
    }

    private void getInvitationByUser() {
        Call<List<MeetingService.InvitationData>> c = Server.getService().getUsersInvitations();
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
        List<MeetingService.EventsData> events = new LinkedList<MeetingService.EventsData>();
        EventViewAdapter adapter = new EventViewAdapter(getApplicationContext(), events);
        eventData.setAdapter(adapter);
        eventData.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                    intent.putExtra("event_id", ((MeetingService.EventsData)eventData.getItemAtPosition(position)).getPk());
                    startActivity(intent);
                }
        );

        Calendar todayCal = Calendar.getInstance();
        Date todayDate = todayCal.getTime();

        for (MeetingService.InvitationData inv : invitations) {
            Call<MeetingService.EventsData> c = Server.getService().getEvents("/events/" + inv.event_id);
            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (response.isSuccessful()) {
                            MeetingService.EventsData event = response.body();

                            try {
                                Calendar eventCal = Calendar.getInstance();
                                eventCal.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(event.event_date));
                                eventCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(event.event_time.substring(0, 2)));
                                eventCal.set(Calendar.MINUTE, Integer.parseInt(event.event_time.substring(3, 5)));
                                Date eventDate = eventCal.getTime();
                                boolean timeIsConsistent = false;

                                switch (timePeriod) {
                                    case 0:
                                        timeIsConsistent = todayDate.before(eventDate)
                                                && todayCal.get(Calendar.DAY_OF_YEAR) == eventCal.get(Calendar.DAY_OF_YEAR)
                                                && todayCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR);
                                        break;
                                    case 1:
                                        timeIsConsistent = todayDate.before(eventDate)
                                                && todayCal.get(Calendar.WEEK_OF_YEAR) == eventCal.get(Calendar.WEEK_OF_YEAR)
                                                && todayCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR);
                                        break;
                                    case 2:
                                        timeIsConsistent = todayDate.before(eventDate)
                                                && todayCal.get(Calendar.MONTH) == eventCal.get(Calendar.MONTH)
                                                && todayCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR);
                                        break;
                                    default:
                                        break;
                                }

                                if (timeIsConsistent && inv.status != 1 && (declined || inv.status != 3)) {
                                    events.add(event);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch(ParseException e) {
                                Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // TODO: Parse error
                            Server.parseUnsuccessful(response, MeetingService.EventDataError.class, System.out::println, System.out::println);
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }
    }
}