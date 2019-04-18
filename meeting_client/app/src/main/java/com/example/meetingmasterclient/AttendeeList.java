package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AttendeeList extends AppCompatActivity {
    private List<MeetingService.UserProfile> attendeeList;
    private List<MeetingService.InvitationData> attendeeResponse;
    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String eventID;

    private static final String TAG = "DebugLauncherActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventID = Integer.toString(getIntent().getIntExtra("event_id", -1));
        Toast.makeText(AttendeeList.this, "Event id received: " + eventID, Toast.LENGTH_LONG).show();

        recyclerView = findViewById(R.id.recycler_view_invited_people);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AttendeeAdapter();
        recyclerView.setAdapter(adapter);

        attendeeList = new ArrayList<>();
        adapter.setDataSet(attendeeList);

        submitAttendeeRequest();

        //TODO: Before user returns to create a meeting page, store the list of users in the database
        //exit the activity and return to Create a meeting page when the admin presses the save changes button
        //configureSaveButton();
        Button close_button = findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    /*private void configureSaveButton(){
        Button save_button = (Button)findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Boolean> EditingPermit = adapter.getEditingPermit();
                finish();   //return to create a meeting
            }
        });
    }*/

    public void submitAttendeeRequest(){
        Call<List<MeetingService.InvitationData>> c = Server.getService().getEventInvitations(eventID);
        c.enqueue(Server.mkCallback(
            (call, response) -> {
                if (response.isSuccessful()){
                    Toast.makeText(AttendeeList.this, "response.success = " + response.toString()
                            + "\nPopulating list", Toast.LENGTH_LONG).show();
                    populateAttendeeList(response);
                } else {
                    try {
                        Toast.makeText(AttendeeList.this, "response.error = " +
                                response.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("response.error = " + response.errorBody().toString());
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    return;
                }
            },
            (call, t) -> t.printStackTrace()
        ));
    }

    public void populateAttendeeList(Response<List<MeetingService.InvitationData>> response){
        attendeeResponse = response.body();

        if (attendeeResponse == null){
            Toast.makeText(AttendeeList.this, "attendeeList is empty", Toast.LENGTH_LONG).show();
            return;
        }

        Call <MeetingService.UserProfile> userProfileCall;
        for (int i = 0; i < attendeeResponse.size(); i++){
            int userID = Integer.parseInt(attendeeResponse.get(i).getUser_id());
            Toast.makeText(AttendeeList.this, "searching for user: " + userID, Toast.LENGTH_LONG).show();
            userProfileCall = Server.getService().getUserByID(userID);
            userProfileCall.enqueue(Server.mkCallback(
                    (call, res) -> {
                        if (res.isSuccessful()){
                            Toast.makeText(AttendeeList.this, res.toString(), Toast.LENGTH_LONG).show();
                            attendeeList.add(res.body());
                        } else {
                            try {
                                Toast.makeText(AttendeeList.this, "res.error = " + res.toString(), Toast.LENGTH_LONG).show();
                                System.out.println("res.error = " + res.errorBody().toString());
                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            return;
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }

        adapter.setDataSet(attendeeList);

        for (int i = 0; i < attendeeList.size(); i++){
            String user = attendeeList.get(i).getUsername();
            Toast.makeText(AttendeeList.this, "User " + (i+1) + ": " + user, Toast.LENGTH_LONG).show();
        }
    }

    private class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {
        @Nullable
        private List<MeetingService.UserProfile> dataSet;
        private List<Boolean> editingPermit = new ArrayList<Boolean>();
        public List<Boolean> getEditingPermit() {
            return editingPermit;
        }

        @NonNull
        @Override
        public AttendeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AttendeeAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_and_view_button, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AttendeeAdapter.ViewHolder holder, int position) {
            String name = dataSet.get(position).getFirst_name()+" "+ dataSet.get(position).getLast_name();
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
                //TODO fix this
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
    }//AttendeeAdapter
}
