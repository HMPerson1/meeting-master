package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button button = findViewById(R.id.search_users);
        TextInputLayout textInput = findViewById(R.id.textInputLayout);
        button.setOnClickListener(v -> {
            Server.getService().users(textInput.getEditText().getText().toString())
                    .enqueue(new Callback<List<MeetingService.UserProfile>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<MeetingService.UserProfile>> call, @NonNull Response<List<MeetingService.UserProfile>> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                System.out.println("response = " + response.body().stream().map(Objects::toString).collect(Collectors.joining(", ")));
                            } else {
                                try {
                                    System.out.println("response.error = " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<MeetingService.UserProfile>> call, @NonNull Throwable t) {
                            t.printStackTrace();
                        }
                    });
        });

    }

}
