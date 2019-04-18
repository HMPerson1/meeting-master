package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.RemotePicture;

import retrofit2.Call;

public class ProfileDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getUserById(getIntent().getIntExtra("user_id", -1));
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

                        ((TextView)findViewById(R.id.profile_details_name)).setText(
                                profile.first_name + " " + profile.last_name + " (" + profile.username + ")"
                        );
                        ((TextView)findViewById(R.id.profile_details_email)).setText("Email: " + profile.email);

                        if (profile.phone_number.isEmpty()) {
                            ((TextView)findViewById(R.id.profile_details_phone)).setText("Phone number: None");
                        } else {
                            ((TextView)findViewById(R.id.profile_details_phone)).setText("Phone number: " + profile.phone_number);
                        }

                        new RemotePicture(this).execute(profile.profile_picture);
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.UserProfileError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
