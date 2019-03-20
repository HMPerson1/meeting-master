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



        /*
        Call<List<MeetingService.UserProfile>> c = Server.getService().users("");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT);
                        return;
                    }

                    MeetingService.UserProfile profile = response.body().get(0);

                    // TODO: Set image
                    //((ImageView)findViewById(R.id.profile_details_picture));
                    ((TextView)findViewById(R.id.profile_details_first_name)).setText(profile.first_name);
                    ((TextView)findViewById(R.id.profile_details_last_name)).setText(profile.last_name);
                    ((TextView)findViewById(R.id.profile_details_username)).setText(profile.username);
                    ((TextView)findViewById(R.id.profile_details_email)).setText(profile.email);
                    ((TextView)findViewById(R.id.profile_details_phone)).setText(profile.phone_number);

                },
                (call, t) -> t.printStackTrace()
        ));*/
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
                        // TODO: set TextField values to retrieved profile
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.UserProfileError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
