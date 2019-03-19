package com.example.meetingmasterclient;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EventDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //testing changing text fields
        final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.meeting_name);
        nameInput.setText("testing");
    }
}
