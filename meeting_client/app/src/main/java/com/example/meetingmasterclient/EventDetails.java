package com.example.meetingmasterclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetails extends AppCompatActivity {
    public static final String PREFS_NAME = "App_Settings";
    private static final String TAG = "DebugLauncherActivity";
    int eventId;
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

        //get the current intent
        Intent intent = getIntent();
        eventId= intent.getIntExtra("event_id", -1);

        eventId =1234; //for testing

        if (eventId<0){
            finish();  //did not pass event_id
        }

        //TODO: get event info from backend
        Call<MeetingService.EventData> call = Server.getService().getEventfromId(String.valueOf(eventId));
        call.enqueue(new Callback<MeetingService.EventData>() {
            @Override
            public void onResponse(Call<MeetingService.EventData> call, Response<MeetingService.EventData> response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventDetails.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(EventDetails.this,"response" , Toast.LENGTH_LONG).show();
                Toast.makeText(EventDetails.this,response.toString() , Toast.LENGTH_LONG).show();

                //add user to list if successful
                MeetingService.EventData eventInfo =response.body();//store response

                //display the event details to the user
                nameInput.setText(eventInfo.getEvent_name());
                textInputDate.setText(eventInfo.getEvent_date());
                textInputTime.setText(eventInfo.getEvent_time());
                textInputNotes.setText(eventInfo.getNotes());

                /*//display location details to the user
                textInputStreetAddr.setText();
                textInputCity
                textInputState
                textInputRoomNo
                */



            }

            @Override
            public void onFailure(Call<MeetingService.EventData> call, Throwable t) {//error from server

                Toast.makeText(EventDetails.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.notificationSwitch).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.isChecked()){
                    menuItem.setChecked(false);
                }else{
                    menuItem.setChecked(true);
                }

                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String NotificationStatusKey= String.valueOf(eventId) +"checkStatus";
                editor.putBoolean(NotificationStatusKey, menuItem.isChecked());
                editor.commit();
                /* true = notifications on, false= notifications off
                    key= eventId+checkStatus;
                 */
                /*
                //get pref
                Boolean value = sharedPref.getBoolean(NotificationStatusKey, false);
                Log.d(TAG, value.toString());
                */
                return false;
            }
        });

        menu.findItem(R.id.edit_Event).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(EventDetails.this, EventEdition.class));

                return false;

            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_details, menu);
        return true;
    }
}
