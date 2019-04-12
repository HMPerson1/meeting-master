package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class UserSearch extends AppCompatActivity {
    private TextInputLayout textInputSearch;
    private List<MeetingService.UserProfile> userResults;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init button
        Button button = findViewById(R.id.search_users);
        textInputSearch = findViewById(R.id.textInputSearch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSearchRequest(view);
            }
        });

        //init RecyclerView
        recyclerView = findViewById(R.id.search_results);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //mAdapter = new UserSearch();
        recyclerView.setAdapter(mAdapter);

        /*
        adapter = new AttendeeList.AttendeeAdapter();
        recyclerView.setAdapter(adapter);*/
    }

    private boolean validateUserSearch(View v){
        String search = textInputSearch.getEditText().getText().toString().trim();
        if (search.isEmpty()){
            textInputSearch.setError("Search field cannot be empty");
            return false;
        } else {
            textInputSearch.setError(null);
            return true;
        }
    }

    public void submitSearchRequest(View v){
        if (!validateUserSearch(v)) return;
        String inputSearch = textInputSearch.getEditText().getText().toString();
        Log.d("UserSearch", "inputSearch = " + inputSearch);

        Call<List<MeetingService.UserProfile>> c = Server.getService().users(inputSearch);
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    Toast.makeText(UserSearch.this,response.toString(), Toast.LENGTH_LONG).show();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        printResults(response);
                        populateList(response);
                    } else {
                        try {
                            System.out.println("response.error = " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    public void printResults(Response<List<MeetingService.UserProfile>> response){
        System.out.println("response = " + response.body().stream().map(Objects::toString).collect(Collectors.joining(", ")));
        userResults = response.body();

        if (userResults == null){
            textInputSearch.setError("Search field returned no results");
            return;
        } else {
            textInputSearch.setError(null);
        }

        Toast.makeText(UserSearch.this, "UserResults: " + userResults,
                Toast.LENGTH_LONG).show();

        for (int i = 0; i < userResults.size(); i++){
            String user = userResults.get(i).getUsername();
            Toast.makeText(UserSearch.this, "User " + (i+1) + ": " + user, Toast.LENGTH_LONG).show();
        }
    }

    public void populateList(Response<List<MeetingService.UserProfile>> response){

    }
}

private class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder>{
    @Nullable
    private List<MeetingService.UserProfile> dataSet;

    @NonNull
    @Override
    public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

    }
}