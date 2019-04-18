package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class UserSearch extends AppCompatActivity {
    private TextInputLayout textInputSearch;
    private List<MeetingService.UserProfile> userResults;
    private RecyclerView recyclerView;
    private UserSearchAdapter mAdapter;
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

        //init UserSearchAdapter
        mAdapter = new UserSearchAdapter();
        recyclerView.setAdapter(mAdapter);

        userResults = new ArrayList<>();
        /*
        MeetingService.UserProfile userProfileTest = new MeetingService.UserProfile();
        userProfileTest.setUsername("test");
        userResults.add(userProfileTest);*/

        mAdapter.setDataSet(userResults);
    }

    private boolean validateUserSearch(View v) {
        String search = textInputSearch.getEditText().getText().toString().trim();
        if (search.isEmpty()) {
            textInputSearch.setError("Search field cannot be empty");
            return false;
        } else {
            textInputSearch.setError(null);
            return true;
        }
    }

    public void submitSearchRequest(View v) {
        if (!validateUserSearch(v)) return;
        String inputSearch = textInputSearch.getEditText().getText().toString();
        Log.d("UserSearch", "inputSearch = " + inputSearch);

        Call<List<MeetingService.UserProfile>> c = Server.getService().users(inputSearch);
        c.enqueue(Server.mkCallback(
            (call, response) -> {
                Toast.makeText(UserSearch.this, response.toString(), Toast.LENGTH_LONG).show();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    printAndPopulateResults(response);
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

    public void printAndPopulateResults(Response<List<MeetingService.UserProfile>> response) {
        System.out.println("response = " + response.body().stream().map(Objects::toString).collect(Collectors.joining(", ")));
        userResults = response.body();

        if (userResults == null) {
            textInputSearch.setError("Search field returned no results");
            return;
        } else {
            textInputSearch.setError(null);
        }

        mAdapter.setDataSet(userResults);

        /*
        Toast.makeText(UserSearch.this, "UserResults: " + userResults,
                Toast.LENGTH_LONG).show();*/

        for (int i = 0; i < userResults.size(); i++) {
            String user = userResults.get(i).getUsername();
           // Toast.makeText(UserSearch.this, "User " + (i + 1) + ": " + user, Toast.LENGTH_LONG).show();
        }
    }

    private class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {
        @Nullable
        private List<MeetingService.UserProfile> dataSet;

        @NonNull
        @Override
        public UserSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserSearchAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_and_view_button, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserSearchAdapter.ViewHolder holder, int position) {
            String name = dataSet.get(position).getUsername();
            holder.personName.setText(name);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        void setDataSet(List<MeetingService.UserProfile> dataSet) {
            this.dataSet = dataSet;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            TextView personName;

            ViewHolder(@NonNull View view) {
                super(view);
                personName = view.findViewById(R.id.userSearchResultText);
                Button viewButton = view.findViewById(R.id.viewUserSearchResultButton);
                viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), ProfileDetails.class);
                        int position = getAdapterPosition();
                        intent.putExtra("user_id", dataSet.get(position).getPk());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}