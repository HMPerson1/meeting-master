package com.example.meetingmasterclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AttendeeList extends AppCompatActivity {
    private List<MeetingService.UserProfile> attendeeList;
    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private int eventID;

    private static final String TAG = "DebugLauncherActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        eventID = getIntent().getIntExtra("event_id", -1);

        recyclerView = findViewById(R.id.recycler_view_invited_people);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AttendeeAdapter();
        recyclerView.setAdapter(adapter);

        attendeeList = new ArrayList<>();
        adapter.setDataSet(attendeeList);

        /* for testing
        final List<MeetingService.UserProfile> list = new ArrayList<>(); //used for testing functionality of list

        MeetingService.UserProfile test = new MeetingService.UserProfile();
        test.setFirst_name("john");
        test.setLast_name("meyer");

        MeetingService.UserProfile test2 = new MeetingService.UserProfile();
        test2.setFirst_name("john");
        test2.setLast_name("bull");

        list.add(test);
        list.add(test2);

        adapter.setDataSet(list);*/

        //TODO: Before user returns to create a meeting page, store the list of users in the database
        //exit the activity and return to Create a meeting page when the admin presses the save changes button
        configureSaveButton();
    }

    private void configureSaveButton(){
        Button save_button = (Button)findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Boolean> EditingPermit = adapter.getEditingPermit();
                finish();   //return to create a meeting
            }
        });
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
                    .inflate(R.layout.text_and_toggle_button, parent, false));
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
                super(view);
                personName= view.findViewById(R.id.text);

                ToggleButton permissions = view.findViewById(R.id.switch1);
                editingPermit.add(false);
                permissions.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (permissions.getText() == permissions.getTextOn()) {
                            editingPermit.set(getAdapterPosition(),true);
                        } else {
                            editingPermit.set(getAdapterPosition(),false);
                        }
                    }
                });


            }
        }


    }//AttendeeAdapter
}
