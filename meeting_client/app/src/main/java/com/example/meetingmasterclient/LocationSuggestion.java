package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;

public class LocationSuggestion extends AppCompatActivity {
    private TextInputLayout textInputStreetAddr;
    private TextInputLayout textInputCity;
    private TextInputLayout textInputState;
    private TextInputLayout textInputRoomNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_suggestion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textInputStreetAddr = findViewById(R.id.suggestion_street_address);
        textInputCity = findViewById(R.id.suggestion_city);
        textInputState = findViewById(R.id.suggestion_state);
        textInputRoomNo = findViewById(R.id.suggestion_room_no);

        ((Button)findViewById(R.id.suggest_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    makeLocation();
                }
            }
        });
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

    private boolean validateInput() {
        return validateStreetAddr() & validateCity() & validateState();
    }

    private void makeLocation() {
        Call<MeetingService.LocationData> c = Server.getService().newLocation(new MeetingService.LocationData(
                textInputStreetAddr.getEditText().getText().toString(),
                textInputCity.getEditText().getText().toString(),
                textInputState.getEditText().getText().toString()));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        suggest(response.body().getPk());
                    } else {
                        Toast.makeText(getApplicationContext(), "Location error", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void suggest(int location_id) {
        int event_id = getIntent().getIntExtra("event_id", 0);

        if (event_id != 0) {
            Call<MeetingService.LocationSuggestionsData> c = Server.getService().makeSuggestion(
                    new MeetingService.LocationSuggestionsData(event_id, location_id)
            );

            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Location suggested successfully",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }
    }
}
