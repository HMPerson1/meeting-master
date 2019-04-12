package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class SuggestionsListActivity extends AppCompatActivity {
    private static final String TAG = "SuggestionsListActivity";
    private static final String authToken = ""; // TODO
    private SuggestionsListActivity.LocationSuggestionsAdapter adapter;
    private RequestQueue requestQueue;
    int user_id;
    JSONArray jArray = new JSONArray();
    List<String> eventIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        RecyclerView recyclerView = findViewById(R.id.suggestions_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SuggestionsListActivity.LocationSuggestionsAdapter();
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this, new HurlStack());
        requestQueue.start();

        Call<MeetingService.UserProfile> c = Server.getService().getCurrentUser();
        c.enqueue(new Callback<MeetingService.UserProfile>() {
            @Override
            public void onResponse(Call<MeetingService.UserProfile> call, retrofit2.Response<MeetingService.UserProfile> response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(SuggestionsListActivity.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                    return;
                }
                //get user id
                MeetingService.UserProfile userProf =response.body();//store response
                user_id=userProf.getPk();

            }

            @Override
            public void onFailure(Call<MeetingService.UserProfile> call, Throwable t) {//error from server

                Toast.makeText(SuggestionsListActivity.this,t.getMessage() , Toast.LENGTH_LONG).show();

            }
        });


/* placeholder for getting location suggestions from backend
        Call<List<MeetingService.InvitationData>> call = Server.getService().getUsersInvitations();
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

                for (MeetingService.InvitationData invitation : invitations) {
                    //get event name
                    if (invitation.getStatus()!=1){ //do not display if invitation not pending
                        continue;
                    }
                    String eventId = String.valueOf(invitation.getEvent_id());
                    eventIDs.add(eventId);




                }//end for
                //getEvents(response.body());





            }


            @Override
            public void onFailure(Call<List<MeetingService.InvitationData>> call, Throwable t) {//error from server

                Toast.makeText(SuggestionsListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        */

        // TODO: for testing
        try {
            JSONObject jo = new JSONObject();
            jo.put("address", "12 railroad blvd");
            jo.put("state", "neverland");
            jo.put("city","amnesia");
            jArray.put(jo);
            JSONObject jo2 = new JSONObject();
            jo2.put("address", "random address");
            jo2.put("state", "somewhere");
            jo2.put("city","itty");
            jArray.put(jo2);
            adapter.setDataSet(jArray);
            //adapter.setDataSet(new JSONArray("[{\"pk\": 1, \"name\": \"The First Event\"},{\"pk\": 2, \"name\": \"Event 2: Electric Boogaloo\"}]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /* //placeholder for retrieval for location endpoint
    private void getEvents(List<MeetingService.InvitationData> invitations){
        for (String eventId:eventIDs) {
            Call<MeetingService.EventsData> call2 = Server.getService().getEventfromId(String.valueOf(eventId));
            call2.enqueue(new Callback<MeetingService.EventsData>() {
                @Override
                public void onResponse(Call<MeetingService.EventsData> call, retrofit2.Response<MeetingService.EventsData> response) {
                    if (!response.isSuccessful()) { //404 error?
                        Toast.makeText(InvitationListActivity.this, "Oops, Something is wrong: " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(InvitationListActivity.this, "response" + response.body(), Toast.LENGTH_LONG).show();
                    Toast.makeText(InvitationListActivity.this, response.toString(), Toast.LENGTH_LONG).show();


                    MeetingService.EventsData eventInfo = response.body();//store response

                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("pk", eventInfo.getPk());
                        jo.put("name", eventInfo.getEvent_name());
                        jArray.put(jo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.setDataSet(jArray); //display suggestions


                }

                @Override
                public void onFailure(Call<MeetingService.EventsData> call, Throwable t) {//error from server

                    Toast.makeText(SuggestionsListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        }
    }//getEvents

    */
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
                        Log.w(TAG, "onErrorResponse: fetch Location", error);
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

    private void pickLocation(int eventId, String action,int position) {
        //TODO send location id to edit event activity change activity
    }

    private class LocationSuggestionsAdapter extends RecyclerView.Adapter<SuggestionsListActivity.LocationSuggestionsAdapter.ViewHolder> {

        @Nullable
        private JSONArray dataSet;

        @NonNull
        @Override
        public SuggestionsListActivity.LocationSuggestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SuggestionsListActivity.LocationSuggestionsAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_suggestion_info, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestionsListActivity.LocationSuggestionsAdapter.ViewHolder holder, int position) {
            if (dataSet == null) {
                Log.e(TAG, "onBindViewHolder: dataSet was null");
                return;
            }

            try {
                JSONObject location = dataSet.getJSONObject(position);
                //holder.eventId = event.getInt("pk");
                holder.locationAddressView.setText(location.getString("address"));
                holder.locationStateView.setText(location.getString("state"));
                holder.locationCityView.setText(location.getString("city"));
                holder.position = holder.getAdapterPosition();
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
            int locationId=-1;
            TextView locationAddressView;
            TextView locationStateView;
            TextView locationCityView;
            int position;

            ViewHolder(@NonNull View view) {
                super(view);
                locationAddressView = view.findViewById(R.id.address);
                locationStateView = view.findViewById(R.id.state_info);
                locationCityView = view.findViewById(R.id.city_info);

            }
        }


    }

}
