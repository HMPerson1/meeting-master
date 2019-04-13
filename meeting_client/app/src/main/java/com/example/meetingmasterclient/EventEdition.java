package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class EventEdition extends AppCompatActivity {

    int eventID;
    int locationID;

    EditText nameInput;
    EditText textDuration;
    EditText textInputDate;
    EditText textInputTime;
    EditText textInputNotes;
    EditText textInputStreetAddr;
    EditText textInputCity;
    EditText textInputState;
    EditText textInputRoomNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        configureAddUserButton();
        configureSaveButton();
        configureSuggestedLocationsButton();

        nameInput = (TextInputEditText) findViewById(R.id.text_input_event_name);
        textDuration = findViewById(R.id.text_input_duration);
        textInputDate = findViewById(R.id.text_input_date);
        textInputTime = findViewById(R.id.text_input_time);
        textInputNotes = findViewById(R.id.text_input_notes);
        textInputStreetAddr = findViewById(R.id.text_input_street_address);
        textInputCity = findViewById(R.id.text_input_city);
        textInputState = findViewById(R.id.text_input_state);
        textInputRoomNo = findViewById(R.id.text_input_room_no);


        Intent intent = getIntent();
        eventID = intent.getIntExtra("event_id", -1);
        locationID = intent.getIntExtra("location_id", -1);

        if (locationID!=-1){
            //location id given by suggestions list, fill in location text input fields with the chosen location


        }

        if (eventID<0){
            finish();  //did not pass event_id
        }

        //TODO: get event info from backend
        Call<MeetingService.EventsData> call = Server.getService().getEventfromId(String.valueOf(eventID));
        call.enqueue(new Callback<MeetingService.EventsData>() {
            @Override
            public void onResponse(Call<MeetingService.EventsData> call, Response<MeetingService.EventsData>response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventEdition.this, "Oops, Something is wrong: " +
                            response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(EventEdition.this,"response" , Toast.LENGTH_LONG).show();
                Toast.makeText(EventEdition.this,response.toString() , Toast.LENGTH_LONG).show();


                MeetingService.EventsData eventInfo = response.body();//store response

                //display the event details to the user
                nameInput.setText(eventInfo.getEvent_name());
                textInputDate.setText(eventInfo.getEvent_date());
                textInputTime.setText(eventInfo.getEvent_time());
                textDuration.setText(eventInfo.getEvent_duration());
                textInputNotes.setText(eventInfo.getNotes());

                //get location details from server

                if (locationID<0) {
                    MeetingService.LocationData locationInfo = eventInfo.getEvent_location();


                    textInputStreetAddr.setText(locationInfo.getStreet_address());
                    textInputCity.setText(locationInfo.getCity());
                    textInputState.setText(locationInfo.getState());
                    //textInputRoomNo.setText(locationInfo.);
                }else{
                    getLocation();
                }




            }

            @Override
            public void onFailure(Call<MeetingService.EventsData> call, Throwable t) {//error from server

                Toast.makeText(EventEdition.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }

        });

    }//on create

    private void configureSuggestedLocationsButton(){
        Button suggestedButton = (Button) findViewById(R.id.suggested_locations_button);
        suggestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent suggested = new Intent(getApplicationContext(), SuggestionsListActivity.class);
                suggested.putExtra("event_id", eventID);
                startActivity(suggested);
            }
        });
    }

    private void configureSaveButton(){


        Button save_button = (Button)findViewById(R.id.save_meeting_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get new event details
                String name = nameInput.getText().toString();
                String date = textInputDate.getText().toString();
                String time = textInputTime.getText().toString();
                String duration = textDuration.getText().toString();
                String notes =textInputNotes.getText().toString();

                //get new location details

                //display location details to the user
                //TODO post location here


                putLocationandEvent();

                Intent intent = new Intent(getApplicationContext(), EventDetails.class);
                intent.putExtra("event_id", eventID);
                startActivity(intent);

                finish();
        }
        });





    }

    public void submitInvitation(View view) {
        // TODO
    }

    private void configureAddUserButton(){
        Button add_button = (Button)findViewById(R.id.add_users_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventEdition.this, AddUserstoMeeting.class));
            }
        });
    }

    public void putEvent(MeetingService.LocationData locationInfo){
        int locationID= locationInfo.getPk();

        nameInput = (TextInputEditText) findViewById(R.id.text_input_event_name);
        textDuration = findViewById(R.id.text_input_duration);
        textInputDate = findViewById(R.id.text_input_date);
        textInputTime = findViewById(R.id.text_input_time);
        textInputNotes = findViewById(R.id.text_input_notes);

        String event_name = nameInput.getText().toString().trim();
        String event_date = textInputDate.getText().toString().trim();
        String event_time = textInputTime.getText().toString().trim();
        String event_duration = textDuration.getText().toString().trim();
        String notes = textInputNotes.getText().toString().trim();
/*
        MeetingService.EventCreationData eventCreationData = new MeetingService
                .EventCreationData(event_name,event_date,event_time,event_duration,
                locationID, notes, null);
        //post changes
        Call<MeetingService.EventsData> c = Server.getService().updateEvent(eventCreationData,String.valueOf(eventID));*/
        Call<MeetingService.EventsData> c = Server.getService().editEventForm(String.valueOf(eventID),event_name,event_date,event_time,event_duration,
                locationID, notes, null);

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.EventDataError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));




    }

    public void putLocationandEvent(){

        textInputStreetAddr = findViewById(R.id.text_input_street_address);
        textInputCity = findViewById(R.id.text_input_city);
        textInputState = findViewById(R.id.text_input_state);
        textInputRoomNo = findViewById(R.id.text_input_room_no);

        Call<MeetingService.LocationData> c2 = Server.getService().newLocation(new MeetingService.LocationData(textInputStreetAddr.getText().toString(),
                textInputCity.getText().toString(),textInputState.getText().toString()));

        c2.enqueue(new Callback<MeetingService.LocationData>() {
            @Override
            public void onResponse(Call<MeetingService.LocationData> call, Response<MeetingService.LocationData> response) {
                if(!response.isSuccessful()){ //404 error?
                    Log.e("idkwh",textInputState.getText().toString());
                    Toast.makeText(EventEdition.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(EventEdition.this,response.toString() , Toast.LENGTH_LONG).show();
                MeetingService.LocationData locationInfo =response.body();
                putEvent(response.body());


            }

            @Override
            public void onFailure(Call<MeetingService.LocationData> call, Throwable t) {//error from server

                Toast.makeText(EventEdition.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }
        });

    }

    public void getLocation(){ //for location suggestions view displays suggested location
        Call<MeetingService.LocationData> call2 = Server.getService().getLocationDetails(String.valueOf(locationID));
        call2.enqueue(new Callback<MeetingService.LocationData>() {
            @Override
            public void onResponse(Call<MeetingService.LocationData> call, retrofit2.Response<MeetingService.LocationData> response) {
                if (!response.isSuccessful()) { //404 error?
                    return;
                }

                MeetingService.LocationData locationInfo = response.body();//store response
                textInputStreetAddr.setText(locationInfo.getStreet_address());
                textInputCity.setText(locationInfo.getCity());
                textInputState.setText(locationInfo.getState());

            }

            @Override
            public void onFailure(Call<MeetingService.LocationData> call, Throwable t) {//error from server

                Toast.makeText(EventEdition.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

}
