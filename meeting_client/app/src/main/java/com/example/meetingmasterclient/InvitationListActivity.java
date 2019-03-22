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
    int user_id;
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

        requestQueue = Volley.newRequestQueue(this, new HurlStack());
        requestQueue.start();

        //get user_id
        user_id=-1;
        Call<MeetingService.UserProfile> call3 = Server.getService().getCurrentUser();
        call3.enqueue(new Callback<MeetingService.UserProfile>() {
            @Override
            public void onResponse(Call<MeetingService.UserProfile> call, retrofit2.Response<MeetingService.UserProfile> response) {
                if (!response.isSuccessful()) { //404 error?
                    Toast.makeText(InvitationListActivity.this, "Oops, Something is wrong: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(InvitationListActivity.this, "response"+response.body(), Toast.LENGTH_LONG).show();
                Toast.makeText(InvitationListActivity.this, response.toString(), Toast.LENGTH_LONG).show();


                MeetingService.UserProfile currentUser = response.body();//store response
                user_id =currentUser.getPk();


            }

            @Override
            public void onFailure(Call<MeetingService.UserProfile> call, Throwable t) {//error from server

                Toast.makeText(InvitationListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


        if (user_id<0) { //user is logged in
            JSONArray jArray = new JSONArray();
            Call<List<MeetingService.InvitationData>> call = Server.getService().getUserInvitations(String.valueOf(user_id));
            call.enqueue(new Callback<List<MeetingService.InvitationData>>() {
                @Override
                public void onResponse(Call<List<MeetingService.InvitationData>> call, retrofit2.Response<List<MeetingService.InvitationData>> response) {
                    if (!response.isSuccessful()) { //404 error?
                        Toast.makeText(InvitationListActivity.this, "Oops, Something is wrong: " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(InvitationListActivity.this, "response"+response.body(), Toast.LENGTH_LONG).show();
                    Toast.makeText(InvitationListActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                    //add user to list if successful
                    List<MeetingService.InvitationData> invitations = response.body();//store response
                    final List<String> list = new ArrayList<>();
                    for (MeetingService.InvitationData invitation : invitations) {
                        //get event name
                        String eventId = String.valueOf(invitation.getEvent_id());

                        Call<MeetingService.EventData> call2 = Server.getService().getEventfromId(String.valueOf(eventId));
                        call2.enqueue(new Callback<MeetingService.EventData>() {
                            @Override
                            public void onResponse(Call<MeetingService.EventData> call, retrofit2.Response<MeetingService.EventData> response) {
                                if (!response.isSuccessful()) { //404 error?
                                    Toast.makeText(InvitationListActivity.this, "Oops, Something is wrong: " + response.code(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Toast.makeText(InvitationListActivity.this, "response"+response.body(), Toast.LENGTH_LONG).show();
                                Toast.makeText(InvitationListActivity.this, response.toString(), Toast.LENGTH_LONG).show();


                                MeetingService.EventData eventInfo = response.body();//store response

                                try {
                                    JSONObject jo = new JSONObject();
                                    jo.put("pk", eventInfo.getId());
                                    jo.put("name", eventInfo.getEvent_name());
                                    jArray.put(jo);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onFailure(Call<MeetingService.EventData> call, Throwable t) {//error from server

                                Toast.makeText(InvitationListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        });

                    }//end for

                    adapter.setDataSet(jArray); //display invites



                }


                @Override
                public void onFailure(Call<List<MeetingService.InvitationData>> call, Throwable t) {//error from server

                    Toast.makeText(InvitationListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        }//end if





        // TODO: for testing
        try {
            JSONObject jo = new JSONObject();
            jo.put("pk", "1");
            jo.put("name", "The First Event");
            JSONArray jArray = new JSONArray();
            jArray.put(jo);

            JSONObject jo2 = new JSONObject();
            jo2.put("pk", "2");
            jo2.put("name", "Event 2: Electric Boogaloo");

            jArray.put(jo2);
            adapter.setDataSet(jArray);

            //adapter.setDataSet(new JSONArray("[{\"pk\": 1, \"name\": \"The First Event\"},{\"pk\": 2, \"name\": \"Event 2: Electric Boogaloo\"}]"));
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
