package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        configureSaveButton();

        final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.text_input_event_name);
        final TextInputEditText textDuration = findViewById(R.id.text_input_duration);
        final TextInputEditText textInputDate = findViewById(R.id.text_input_date);
        final TextInputEditText textInputTime = findViewById(R.id.text_input_time);
        final TextInputEditText textInputNotes = findViewById(R.id.text_input_notes);
        final TextInputEditText textInputStreetAddr = findViewById(R.id.text_input_street_address);
        final TextInputEditText textInputCity = findViewById(R.id.text_input_city);
        final TextInputEditText textInputState = findViewById(R.id.text_input_state);
        final TextInputEditText textInputRoomNo = findViewById(R.id.text_input_room_no);




        eventID =1; //for testing

        if (eventID<0){
            finish();  //did not pass event_id
        }
/*
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

                MeetingService.LocationData locationInfo= eventInfo.getEvent_location();


                textInputStreetAddr.setText(locationInfo.getStreet_address());
                textInputCity.setText(locationInfo.getCity());
                textInputState.setText(locationInfo.getState());
                //textInputRoomNo.setText(locationInfo.);




            }

            @Override
            public void onFailure(Call<MeetingService.EventsData> call, Throwable t) {//error from server

                Toast.makeText(EventEdition.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }

        });

*/

    }

    private void configureSaveButton(){

        final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.text_input_event_name);
        final TextInputEditText textDuration = findViewById(R.id.text_input_duration);
        final TextInputEditText textInputDate = findViewById(R.id.text_input_date);
        final TextInputEditText textInputTime = findViewById(R.id.text_input_time);
        final TextInputEditText textInputNotes = findViewById(R.id.text_input_notes);
        final TextInputEditText textInputStreetAddr = findViewById(R.id.text_input_street_address);
        final TextInputEditText textInputCity = findViewById(R.id.text_input_city);
        final TextInputEditText textInputState = findViewById(R.id.text_input_state);
        final TextInputEditText textInputRoomNo = findViewById(R.id.text_input_room_no);

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
                String street =textInputStreetAddr.getText().toString();
                String city =textInputCity.getText().toString();
                String state =textInputState.getText().toString();

                //post changes
                Call<MeetingService.EventsData> c = Server.getService().createEvent(new MeetingService
                        .EventCreationData(name,date,time,duration,
                        0, notes));

                //new MeetingService
                //                .EventCreationData(event_name, event_date, event_time, event_duration, locationID, notes));

                c.enqueue(Server.mkCallback(
                        (call, response) -> {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                            } else {
                                Server.parseUnsuccessful(response, MeetingService.EventCreationData.class, System.out::println, System.out::println);
                            }
                        },
                        (call, t) -> t.printStackTrace()
                ));

                finish();//return to Event Details
            }
        });





    }

}
