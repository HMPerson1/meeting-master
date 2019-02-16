package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Create_a_Meeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a__meeting);



        //check if required fields filled and validate address if user presses create a meeting button
        configureCreateaMeetingButton();

        //Switch activity to add user activity if admin clicks add user button
        configureAddUserButton();


    }

    private void configureAddUserButton(){
        Button add_button = (Button)findViewById(R.id.add_users_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Create_a_Meeting.this, AddUserstoMeeting.class));

            }
        });
    }

    private void configureCreateaMeetingButton(){
        Button create_button = (Button)findViewById(R.id.create_meeting_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: throw error if any required fields are not filled in
                //Store field values
                //country, street, city, state, Meeting name, date, time
                final TextInputEditText countryInput = (TextInputEditText) findViewById(R.id.country);
                String country = String.valueOf(countryInput.getText()).trim(); //get country from input
                Log.d("COUNTRY", country);

                final TextInputEditText streetInput = (TextInputEditText) findViewById(R.id.street);
                String street = String.valueOf(streetInput.getText()).trim(); //get country from input

                final TextInputEditText cityInput = (TextInputEditText) findViewById(R.id.city);
                String city = String.valueOf(cityInput.getText()).trim(); //get country from input

                final TextInputEditText stateInput = (TextInputEditText) findViewById(R.id.state);
                String state = String.valueOf(stateInput.getText()).trim(); //get country from input

                final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.meeting_name);
                String meetingName = String.valueOf(nameInput.getText()).trim(); //get country from input

                final TextInputEditText dateInput = (TextInputEditText) findViewById(R.id.date);
                String date = String.valueOf(dateInput.getText()).trim(); //get country from input

                final TextInputEditText timeInput = (TextInputEditText) findViewById(R.id.time);
                String time = String.valueOf(timeInput.getText()).trim(); //get country from input

                if (country.length()<=0 || street.length()<=0 || city.length()<=0 || state.length()<=0 ||meetingName.length()<=0
                        || date.length()<=0 ||time.length()<=0){
                    Toast.makeText(Create_a_Meeting.this, "Please fill in required Fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
