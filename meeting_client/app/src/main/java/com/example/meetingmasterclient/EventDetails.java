package com.example.meetingmasterclient;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.example.meetingmasterclient.server.MeetingService;

public class EventDetails extends AppCompatActivity {

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

        //TODO: get info from backend
        //MeetingService.EventData eventData = new MeetingService.EventData();
    }
}
