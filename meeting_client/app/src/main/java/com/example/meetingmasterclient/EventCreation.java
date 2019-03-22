package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.TextInputEditText;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.File;

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

    //TODO validate date

    //TODO validate time

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
        return (!validateEventName() | !validateStreetAddr() | !validateCity() | !validateStreetAddr()
            | !validateState());
    }

    public void createMeetingRequest(View v){
        if(!confirmInput(v)) return;

        String event_name = textInputEventName.getEditText().getText().toString().trim();
        String event_date = textInputDate.getEditText().getText().toString().trim();
        String event_time = textInputTime.getEditText().getText().toString().trim();
        String event_duration = textInputDuration.getEditText().getText().toString().trim();
        int event_location = 0;
        String notes = textInputNotes.getEditText().getText().toString().trim();
        //File file_attachment

        Call<MeetingService.EventCreationData> c = Server.getService().createEvent(new MeetingService
                .EventCreationData(event_name, 0));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        //Server.authenticate(response.body().key); TODO check this
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.EventCreationData.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));

        postLocation();
    }

    public void postLocation(){

        Call<MeetingService.LocationData> c2 = Server.getService().newLocation(new MeetingService.LocationData(textInputStreetAddr.getEditText().getText().toString(),
                textInputCity.getEditText().getText().toString(),textInputState.getEditText().getText().toString()));
        c2.enqueue(new Callback<MeetingService.LocationData>() {
            @Override
            public void onResponse(Call<MeetingService.LocationData> call, Response<MeetingService.LocationData> response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventCreation.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(EventCreation.this,response.toString() , Toast.LENGTH_LONG).show();
                MeetingService.LocationData locationInfo =response.body();
                LocationID= locationInfo.getPk();



            }

            @Override
            public void onFailure(Call<MeetingService.LocationData> call, Throwable t) {//error from server

                Toast.makeText(EventCreation.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }
        });

    }



    }




