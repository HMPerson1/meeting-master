package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class InvitationListActivity extends AppCompatActivity {
    private static final String TAG = "InvitationListActivity";
    private static final String authToken = ""; // TODO
    private InvitesAdapter adapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.invites_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InvitesAdapter();
        recyclerView.setAdapter(adapter);
        /*
        requestQueue = Volley.newRequestQueue(this, new HurlStack());
        requestQueue.start();*/

        //get user_id

        String user_id ="0";
/*
        Call<List<MeetingService.InvitationData>> call = Server.getService().getUserInvitations(user_id);
        call.enqueue(new Callback<List<MeetingService.InvitationData>>() {
            @Override
            public void onResponse(Call<List<MeetingService.InvitationData>> call, retrofit2.Response<List<MeetingService.InvitationData>> response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(InvitationListActivity.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(InvitationListActivity.this,"response" , Toast.LENGTH_LONG).show();
                Toast.makeText(InvitationListActivity.this,response.toString() , Toast.LENGTH_LONG).show();

                //add user to list if successful
                List<MeetingService.InvitationData> invitations =response.body();//store response
                final List<String> list = new ArrayList<>();
                for (MeetingService.InvitationData invitation : invitations){
                    JSONArray jArray = new JSONArray();
                    jArray.put(invitation);
                }//end for

            }

            @Override
            public void onFailure(Call<List<MeetingService.InvitationData>> call, Throwable t) {//error from server

                Toast.makeText(InvitationListActivity.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }
        });
*/




        // TODO: for testing
        try {
            //adapter.setDataSet(new JSONArray(new MeetingService.InvitationData(10,"event", ));
            adapter.setDataSet(new JSONArray("[{\"pk\": 1, \"name\": \"The First Event\"},{\"pk\": 2, \"name\": \"Event 2: Electric Boogaloo\"}]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String url = "https://localhost:8000/user/0/invitations"; // TODO
        requestQueue.add(new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        adapter.setDataSet(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "onErrorResponse: fetch invites", error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> ret = new HashMap<>(super.getHeaders());
                ret.put("Authorization", "Token " + authToken);
                return ret;
            }
        });
    }

    private void performInviteResponse(int eventId, String action) {
        String url = "https://localhost:8000/user/0/invitations/" + eventId + "/" + action; // TODO
        requestQueue.add(new JsonArrayRequest(
                Request.Method.POST, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(TAG, "onErrorResponse: perform invite response", error);
                        Toast.makeText(InvitationListActivity.this, "Could not perform action", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> ret = new HashMap<>(super.getHeaders());
                ret.put("Authorization", "Token " + authToken);
                return ret;
            }
        });
    }

    private class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.ViewHolder> {

        @Nullable
        private JSONArray dataSet;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.invitation_list_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (dataSet == null) {
                Log.e(TAG, "onBindViewHolder: dataSet was null");
                return;
            }

            try {
                JSONObject event = dataSet.getJSONObject(position);
                holder.eventId = event.getInt("pk");
                holder.eventNameView.setText(event.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return dataSet != null ? dataSet.length() : 0;
        }

        void setDataSet(JSONArray dataSet) {
            this.dataSet = dataSet;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            int eventId = -1;
            TextView eventNameView;

            ViewHolder(@NonNull View view) {
                super(view);
                eventNameView = view.findViewById(R.id.event_name);
                Button acceptButton = view.findViewById(R.id.button_accept);
                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performInviteResponse(eventId, "accept");
                    }
                });
                Button declineButton = view.findViewById(R.id.button_decline);
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performInviteResponse(eventId, "decline");
                    }
                });
            }
        }
    }
}
