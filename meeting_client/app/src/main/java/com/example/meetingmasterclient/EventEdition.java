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
    final TextInputLayout nameInput = findViewById(R.id.text_input_event_name);
    final TextInputLayout textInputDate = findViewById(R.id.text_input_date);
    final TextInputLayout textInputDuration = findViewById(R.id.text_input_duration);
    final TextInputLayout textInputTime = findViewById(R.id.text_input_time);
    final TextInputLayout textInputNotes = findViewById(R.id.text_input_notes);
    final TextInputLayout textInputStreetAddr = findViewById(R.id.text_input_street_address);
    final TextInputLayout textInputCity = findViewById(R.id.text_input_city);
    final TextInputLayout textInputState = findViewById(R.id.text_input_state);
    final TextInputLayout textInputRoomNo = findViewById(R.id.text_input_room_no);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        configureSaveButton();




        eventID =1234; //for testing

        if (eventID<0){
            finish();  //did not pass event_id
        }

        //TODO: get event info from backend
        Call<MeetingService.EventData> call = Server.getService().getEventfromId(String.valueOf(eventID));
        call.enqueue(new Callback<MeetingService.EventData>() {
            @Override
            public void onResponse(Call<MeetingService.EventData> call, Response<MeetingService.EventData> response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventEdition.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(EventEdition.this,"response" , Toast.LENGTH_LONG).show();
                Toast.makeText(EventEdition.this,response.toString() , Toast.LENGTH_LONG).show();


                MeetingService.EventData eventInfo =response.body();//store response

                //display the event details to the user
                nameInput.getEditText().setText(eventInfo.getEvent_name());
                textInputDate.getEditText().setText(eventInfo.getEvent_date());
                textInputTime.getEditText().setText(eventInfo.getEvent_time());
                textInputNotes.getEditText().setText(eventInfo.getNotes());

                //get location details from server

                int location_id = eventInfo.getEvent_location();

                Call<MeetingService.LocationData> c = Server.getService().getLocationDetails(String.valueOf(location_id));
                c.enqueue(Server.mkCallback(
                        (call2, response2) -> {
                            if (response2.isSuccessful()) {
                                assert response2.body() != null;
                                MeetingService.LocationData locationInfo =response2.body();//store response
                                //display location details to the user
                                textInputStreetAddr.getEditText().setText(locationInfo.getStreet_address());
                                textInputCity.getEditText().setText(locationInfo.getCity());
                                textInputState.getEditText().setText(locationInfo.getState());
                                //textInputRoomNo.setText(locationInfo.);

                            } else {
                                try {
                                    System.out.println("response.error = " + response2.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        (call2, t) -> t.printStackTrace()
                ));


            }

            @Override
            public void onFailure(Call<MeetingService.EventData> call, Throwable t) {//error from server

                Toast.makeText(EventEdition.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }

        });



    }

    private void configureSaveButton(){
        Button save_button = (Button)findViewById(R.id.save_meeting_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get new event details
                String name =nameInput.getEditText().getText().toString();
                String date =textInputDate.getEditText().getText().toString();
                String time =textInputTime.getEditText().getText().toString();
                String duration = textInputDuration.getEditText().getText().toString();
                String notes =textInputNotes.getEditText().getText().toString();

                //get new location details
                //display location details to the user
                String street =textInputStreetAddr.getEditText().getText().toString();
                String city =textInputCity.getEditText().getText().toString();
                String state =textInputState.getEditText().getText().toString();

                //post changes
                Call<MeetingService.EventCreationData> c = Server.getService().createEvent(new MeetingService
                        .EventCreationData(name,date,time,duration,
                0, notes, null));

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
