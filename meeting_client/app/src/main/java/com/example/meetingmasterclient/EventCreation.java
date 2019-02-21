package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class EventCreation extends AppCompatActivity {
    private TextInputLayout textInputStreetAddr;
    private TextInputLayout textInputCity;
    private TextInputLayout textInputState;
    private TextInputLayout textInputRoomNo;
    private TextInputLayout textInputEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        /**Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });**/
        
        textInputStreetAddr = findViewById(R.id.text_input_street_address);
        textInputCity = findViewById(R.id.text_input_city);
        textInputState = findViewById(R.id.text_input_state);
        textInputRoomNo = findViewById(R.id.text_input_room_no);
        textInputEmail = findViewById(R.id.text_input_email);
    }
    
    //check for form completion

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

    public boolean confirmInput(View v) {
        return (!validateStreetAddr() || !validateCity() || !validateStreetAddr());
    }

    public String formJSON(){
        //parse information to be sent to server for registration
        String addr = textInputStreetAddr.getEditText().getText().toString();
        String city = textInputCity.getEditText().getText().toString();
        String state = textInputState.getEditText().getText().toString();
        String roomNo = textInputRoomNo.getEditText().getText().toString();
        String email = textInputEmail.getEditText().getText().toString();
        //TODO get emails as an array for multiple invitees

        JSONObject json = new JSONObject();
        try{
            json.put("addr", addr);
            json.put("city", city);
            json.put("state", state);
            if (!roomNo.isEmpty()){
                json.put("roomNo", roomNo);
            }
            json.put("email", email);
        } catch (JSONException j){
            j.printStackTrace();
        }

        System.out.println(json.toString());
        return json.toString();
    }

    public void submitInvitation(View v){
        if(!confirmInput(v)) return;
        //TODO look up user by email, send invite to server, and display user on this page
        String json = formJSON();
    }

}
