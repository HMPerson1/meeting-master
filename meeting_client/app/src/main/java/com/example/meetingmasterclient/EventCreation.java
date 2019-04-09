package com.example.meetingmasterclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.design.widget.TextInputEditText;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.File;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventCreation extends AppCompatActivity {
    int LocationID;
    private TextInputLayout textInputEventName;
    private TextInputLayout textInputDate;
    private TextInputLayout textInputTime;
    private TextInputLayout textInputDuration;
    private TextInputLayout textInputNotes;
    private TextInputLayout textInputStreetAddr;
    private TextInputLayout textInputCity;
    private TextInputLayout textInputState;
    private TextInputLayout textInputRoomNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        configureAddUserButton();

        configureAttendeeListButton();

        textInputEventName = findViewById(R.id.text_input_event_name);
        textInputDate = findViewById(R.id.text_input_date);
        textInputTime = findViewById(R.id.text_input_time);
        textInputDuration = findViewById(R.id.text_input_duration);
        textInputNotes = findViewById(R.id.text_input_notes);
        textInputStreetAddr = findViewById(R.id.text_input_street_address);
        textInputCity = findViewById(R.id.text_input_city);
        textInputState = findViewById(R.id.text_input_state);
        textInputRoomNo = findViewById(R.id.text_input_room_no);
    }

    /* input validation */

    private boolean validateEventName(){
        String eventName = textInputEventName.getEditText().getText().toString();

        if (eventName.isEmpty()){
            textInputEventName.setError("Event name cannot be empty");
            return false;
        } else {
            textInputEventName.setError(null);
            return true;
        }
    }

    private boolean validateStreetAddr(){
        String streetAddr = textInputStreetAddr.getEditText().getText().toString();
        if (streetAddr.isEmpty()){
            textInputStreetAddr.setError("Street address cannot be empty");
            return false;
        } else {
            textInputStreetAddr.setError(null);
            return true;
        }
    }

    private boolean validateCity(){
        String city = textInputCity.getEditText().getText().toString();
        if (city.isEmpty()){
            textInputCity.setError("City cannot be empty");
            return false;
        } else {
            textInputCity.setError(null);
            return true;
        }
    }

    private boolean validateState(){
        String state = textInputState.getEditText().getText().toString();
        if (state.isEmpty()){
            textInputState.setError("State cannot be empty");
            return false;
        } else {
            textInputState.setError(null);
            return true;
        }
    }

    private void configureAddUserButton(){
        Button add_button = (Button)findViewById(R.id.add_users_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventCreation.this, AddUserstoMeeting.class));
            }
        });
    }
    private void configureAttendeeListButton() {
        Button attendeeListButton = findViewById(R.id.attendees_list_button);

        attendeeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //switch to attendeeList where you can edit permissions
                startActivity(new Intent(EventCreation.this,AttendeeList.class));
            }
        });
    }//configureAttendee
    
    public boolean confirmInput(View v) {
        return (validateEventName() | validateStreetAddr() | validateCity() | validateState());
    }

    public void createMeetingRequest(View v){
        if(!confirmInput(v)) return;

        //File file_attachment

        postLocationandEvent();
        //testing postInvites
        MeetingService.EventsData eventsData = new MeetingService.EventsData();
        postInvites(eventsData);

    }

    public Map<String, ?> getInvitedUsers() {
        SharedPreferences sharedPref = getSharedPreferences("invited_users_IDs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue());
        }
        //remove shared preferences for so list is empty for next event
        sharedPref.edit().clear();
        return allEntries;
    }
    public void postInvites(MeetingService.EventsData event){
        int eventID = event.getPk();
        eventID = 1;//for testing
        Map<String, ?> invitedUsers = getInvitedUsers();
        //send invites to invited users
        for (Map.Entry<String, ?> entry : invitedUsers.entrySet()) {
            Call<MeetingService.InvitationData> c = Server.getService().postInvitations(new MeetingService.InvitationData((String)entry.getValue(),eventID,1,true));
            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(EventCreation.this, "Invitation Sent", Toast.LENGTH_LONG).show();
                        } else {
                            String error=null;
                            Server.parseUnsuccessful(response, MeetingService.RegistrationError.class, System.out::println, System.out::println);
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }
    }

    public void postEvent(MeetingService.LocationData locationInfo){
        LocationID = locationInfo.getPk();
        int locationID = LocationID;
        String event_name = textInputEventName.getEditText().getText().toString().trim();
        String event_date = textInputDate.getEditText().getText().toString().trim();
        String event_time = textInputTime.getEditText().getText().toString().trim();
        String event_duration = textInputDuration.getEditText().getText().toString().trim();
        String notes = textInputNotes.getEditText().getText().toString().trim();

        Call<MeetingService.EventsData> c = Server.getService().createEvent(new MeetingService
                .EventCreationData(event_name, event_date, event_time, event_duration, locationID, notes));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Toast.makeText(EventCreation.this, "EventCreation success: " +
                                response.toString(), Toast.LENGTH_LONG).show();
                        Log.d("EventCreation success", response.toString());
                        //Server.authenticate(response.body().key); TODO check this
                    } else {
                        Toast.makeText(EventCreation.this, "EventCreation unsuccessful: " + response.toString(),
                                Toast.LENGTH_LONG).show();
                        Log.d("EventCreation error", response.toString());
                        Server.parseUnsuccessful(response, MeetingService.EventsData.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    public void postLocationandEvent(){
        Call<MeetingService.LocationData> c2 = Server.getService().newLocation(
                new MeetingService.LocationData(
                    textInputStreetAddr.getEditText().getText().toString(),
                    textInputCity.getEditText().getText().toString(),
                    textInputState.getEditText().getText().toString()));

        c2.enqueue(new Callback<MeetingService.LocationData>() {
            @Override
            public void onResponse(Call<MeetingService.LocationData> call, Response<MeetingService.LocationData> response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventCreation.this, "LocationCreation error: "
                            + response.toString(), Toast.LENGTH_LONG).show();
                    Log.d("LocationCreation error", response.toString());
                    return;
                } else {
                    Toast.makeText(EventCreation.this, "LocationCreation success: " + response.toString(),
                            Toast.LENGTH_LONG).show();
                    Log.i("LocationCreation success", response.toString());
                }
                MeetingService.LocationData locationInfo = response.body();
                postEvent(locationInfo);
            }

            @Override
            public void onFailure(Call<MeetingService.LocationData> call, Throwable t) {//error from server
                Toast.makeText(EventCreation.this,t.getMessage() , Toast.LENGTH_LONG).show();
            }
        });

    }
}
