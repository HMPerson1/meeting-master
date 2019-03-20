package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.util.List;

import retrofit2.Call;

public class ProfileDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUserById(getIntent().getIntExtra("id", -1));
    }

    private void getUserById(int id) {
        if (id == -1) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<MeetingService.UserProfile> c = Server.getService().getUser("/users/" + id + "/");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        MeetingService.UserProfile profile = response.body();

                        // TODO: Set profile picture
                        ((TextView)findViewById(R.id.profile_details_first_name)).setText("First name: " + profile.first_name);
                        ((TextView)findViewById(R.id.profile_details_last_name)).setText("Last name: " + profile.last_name);
                        ((TextView)findViewById(R.id.profile_details_username)).setText("Username: " + profile.username);
                        ((TextView)findViewById(R.id.profile_details_email)).setText("Email: " + profile.email);
                        ((TextView)findViewById(R.id.profile_details_phone)).setText("Phone number: " + profile.phone_number);
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.UserProfileError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
