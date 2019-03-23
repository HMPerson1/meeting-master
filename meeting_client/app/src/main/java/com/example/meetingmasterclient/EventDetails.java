package com.example.meetingmasterclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.EventLog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetails extends AppCompatActivity {
    public static final String PREFS_NAME = "App_Settings";
    private static final String TAG = "DebugLauncherActivity";
    int eventID;    //TODO this will change to string
    String userID;
    private Button attendeeListButton;
    private Button acceptInviteButton;
    private Button declineInviteButton;
    MeetingService.EventsData eventInfo;
    //TODO disable "View Attachment" button if no attachment exists in document

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //testing changing text fields
        final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.meeting_name);
        final TextInputEditText textInputDate = findViewById(R.id.date);
        final TextInputEditText textInputTime = findViewById(R.id.time);
        final TextInputEditText textInputNotes = findViewById(R.id.notes);
        final TextInputEditText textInputStreetAddr = findViewById(R.id.street);
        final TextInputEditText textInputCity = findViewById(R.id.city);
        final TextInputEditText textInputState = findViewById(R.id.state);
        final TextInputEditText textInputRoomNo = findViewById(R.id.room_num);

        eventID = getEventByID(getIntent().getIntExtra("id", -1));

        attendeeListButton = (Button) findViewById(R.id.view_attendees_button);
        attendeeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventDetails.this, AttendeeList.class));
            }
        });

        //TODO: get info from backend
        //MeetingService.EventData eventData = new MeetingService.EventData();
        //get the current intent
        Intent intent = getIntent();
        eventID = intent.getIntExtra("event_id", -1);
        userID = intent.getStringExtra("user_id");

        eventID =1; //for testing

        if (eventID<0){
            finish();  //did not pass event_id
        }

        //TODO: get event info from backend
        Call<MeetingService.EventsData> call = Server.getService().getEventfromId(String.valueOf(eventID));
        call.enqueue(new Callback<MeetingService.EventsData>() {
            @Override
            public void onResponse(Call<MeetingService.EventsData> call, Response<MeetingService.EventsData>response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventDetails.this, "Oops, Something is wrong: " +
                            response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(EventDetails.this,"response" , Toast.LENGTH_LONG).show();
                Toast.makeText(EventDetails.this,response.toString() , Toast.LENGTH_LONG).show();


                eventInfo = response.body();//store response

                //display the event details to the user
                nameInput.setText(eventInfo.getEvent_name());
                textInputDate.setText(eventInfo.getEvent_date());
                textInputTime.setText(eventInfo.getEvent_time());
                textInputNotes.setText(eventInfo.getNotes());

                //get location details from server

                MeetingService.LocationData locationInfo= eventInfo.getEvent_location();


                textInputStreetAddr.setText(locationInfo.getStreet_address());
                textInputCity.setText(locationInfo.getCity());
                textInputState.setText(locationInfo.getState());
                //textInputRoomNo.setText(locationInfo.);




            }

            @Override
            public void onFailure(Call<MeetingService.EventsData> call, Throwable t) {//error from server

                Toast.makeText(EventDetails.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }

        });

        acceptInviteButton = (Button) findViewById(R.id.Accept);
        acceptInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInvitationStatus(eventID, userID, 2);
            }
        });

        declineInviteButton = (Button) findViewById(R.id.decline);
        declineInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInvitationStatus(eventID, userID, 3);
            }
        });
    }

    public void changeInvitationStatus(int eventID, String userID, int newStatus){
        Call<Void> c = Server.getService().setInvitationStatus(eventID, userID, newStatus);
        c.enqueue
                (Server.mkCallback(
                        (call, response) -> {
                            if (response.isSuccessful()) {
                                // TODO: change button to reflect this
                            } else {
                                //Server.parseUnsuccessful(response, MeetingService.EventDataError.class,
                                //        System.out::println, System.out::println);
                                //TODO: make InvitationData error
                            }
                        },
                        (call, t) -> t.printStackTrace()
                ));

    }

    public boolean openAlertDialog(){
        AlertDialogDelete alertDialogDelete = new AlertDialogDelete();
        alertDialogDelete.show(getSupportFragmentManager(), "warning");
        boolean userConfirm = alertDialogDelete.getStatus();
        System.out.println("Sure? " + userConfirm);
        return userConfirm;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.notificationSwitch).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.isChecked()){
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String NotificationStatusKey= String.valueOf(eventID) +"checkStatus";
                editor.putBoolean(NotificationStatusKey, menuItem.isChecked());
                editor.commit();
                /* true = notifications on, false= notifications off
                    key= eventID+checkStatus;
                 */
                /*
                //get pref
                Boolean value = sharedPref.getBoolean(NotificationStatusKey, false);
                Log.d(TAG, value.toString());
                */
                return false;
            }
        });

        menu.findItem(R.id.edit_event).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(getApplicationContext(), EventEdition.class);
                intent.putExtra("event_id", eventID);
                startActivity(intent);



                startActivity(new Intent(EventDetails.this, EventEdition.class));

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.delete_event_menu:
                boolean userConfirm = openAlertDialog();
                userConfirm = true; //TODO delete this line once openAlertDialog() is fixed
                if (userConfirm) {
                    deleteEvent();
                }
                return true;
            case R.id.export_event_menu:
                exportEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getEventByID(int eventID){
        if (eventID== -1) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
            return -1;
        }

        Call<MeetingService.EventData> c = Server.getService().getEvent("/events/" + eventID+ "/");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        // TODO: set TextField values to retrieved event
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.EventDataError.class,
                                System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));

        return eventID;
    }

    public void deleteEvent(){
        Call<Void> d = Server.getService().deleteEvent("/events/" + eventID + "/");
        d.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        //Server.authenticate(response.body().key);
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.RegistrationError.class,
                                System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    public void exportEvent(){
        //TODO implement
        CSVExporter csvExporter = new CSVExporter();

        //display the event details to the user
        String name = eventInfo.getEvent_name();
        String date = eventInfo.getEvent_date();
        String time = eventInfo.getEvent_time();
        String notes = eventInfo.getNotes();

        csvExporter.setEvent(name, date, time, notes);
        csvExporter.writeToFile("test.csv");

        //TODO finish implementation. Needs an intent and sent to calendar apps
    }
}
