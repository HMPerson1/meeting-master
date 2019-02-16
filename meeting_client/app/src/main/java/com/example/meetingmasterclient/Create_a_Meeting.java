package com.example.meetingmasterclient;

import android.content.Intent;
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

        //TODO: throw error if any required fields are not filled in
        //TODO: Switch activity to add user activity if admin clicks add user button
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
}
