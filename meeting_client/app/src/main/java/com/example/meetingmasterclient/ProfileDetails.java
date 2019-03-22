package com.example.meetingmasterclient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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

                        ((TextView)findViewById(R.id.profile_details_first_name)).setText("First name: " + profile.first_name);
                        ((TextView)findViewById(R.id.profile_details_last_name)).setText("Last name: " + profile.last_name);
                        ((TextView)findViewById(R.id.profile_details_username)).setText("Username: " + profile.username);
                        ((TextView)findViewById(R.id.profile_details_email)).setText("Email: " + profile.email);
                        ((TextView)findViewById(R.id.profile_details_phone)).setText("Phone number: " + profile.phone_number);

                        new AsyncTask<String, Void, Drawable>() {
                            @Override
                            protected Drawable doInBackground(String... url) {
                                return getProfilePicture(url[0]);
                            }

                            @Override
                            protected void onPostExecute(Drawable result) {
                                if (result != null) {
                                    ((ImageView)findViewById(R.id.profile_details_picture)).setImageDrawable(result);
                                } else {
                                    Toast.makeText(getApplicationContext(), "The picture cannot be shown", Toast.LENGTH_SHORT).show();
                                }
                            }

                            private Drawable getProfilePicture(String url) {
                                try {
                                    InputStream stream = (InputStream) new URL(url).getContent();
                                    Drawable draw = Drawable.createFromStream(stream, "");
                                    return draw;
                                } catch(Exception e) {
                                    return null;
                                }
                            }
                        }.execute(profile.profile_picture);
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.UserProfileError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
