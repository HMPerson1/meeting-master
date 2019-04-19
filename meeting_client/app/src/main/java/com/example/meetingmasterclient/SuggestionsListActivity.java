package com.example.meetingmasterclient;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
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
    int eventID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        eventID = intent.getIntExtra("event_id", -1);


        RecyclerView recyclerView = findViewById(R.id.suggestions_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SuggestionsListActivity.LocationSuggestionsAdapter();
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this, new HurlStack());
        requestQueue.start();




        Call<List<MeetingService.LocationSuggestionsData>> call = Server.getService().getSuggestedLocations(Integer.toString(eventID));
        call.enqueue(new Callback<List<MeetingService.LocationSuggestionsData>>() {
            @Override
            public void onResponse(Call<List<MeetingService.LocationSuggestionsData>> call, retrofit2.Response<List<MeetingService.LocationSuggestionsData>> response) {
                if (!response.isSuccessful()) { //404 error?
                    Toast.makeText(SuggestionsListActivity.this, "Oops, Something is wrong: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                //Toast.makeText(SuggestionsListActivity.this, "response"+response.body(), Toast.LENGTH_LONG).show();


                //add user to list if successful
                List<MeetingService.LocationSuggestionsData> locations = response.body();//store response
                Log.d("add", String.valueOf(locations.get(0).getLocation_id()));

                getLocationInfo(locations);


            }


            @Override
            public void onFailure(Call<List<MeetingService.LocationSuggestionsData>> call, Throwable t) {//error from server

                Toast.makeText(SuggestionsListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });


        // TODO: for testing
        /*try {

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
        }*/

    }

    private void getLocationInfo(List<MeetingService.LocationSuggestionsData> locations){
        for ( MeetingService.LocationSuggestionsData locationSuggestion:locations) {
            int locationID = locationSuggestion.getLocation_id();
            Call<MeetingService.LocationData> call2 = Server.getService().getLocationDetails(String.valueOf(locationID));
            call2.enqueue(new Callback<MeetingService.LocationData>() {
                @Override
                public void onResponse(Call<MeetingService.LocationData> call, retrofit2.Response<MeetingService.LocationData> response) {
                    if (!response.isSuccessful()) { //404 error?
                        return;
                    }

                    MeetingService.LocationData locationInfo = response.body();//store response


                    try {
                        JSONObject jo = new JSONObject();
                        jo.put("address", locationInfo.getStreet_address());
                        jo.put("state", locationInfo.getState());
                        jo.put("city", locationInfo.getCity());
                        jo.put("locationID", locationID);
                        jArray.put(jo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.setDataSet(jArray); //display suggestions


                }

                @Override
                public void onFailure(Call<MeetingService.LocationData> call, Throwable t) {//error from server

                    Toast.makeText(SuggestionsListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        }
    }//getLocationInfo

    @Override
    protected void onStart() {
        super.onStart();
        String url = "https://localhost:8000/user/0/invitations"; // TODO


    }

    private void pickLocation(int locationID, int eventId) {
        //send location id to edit event activity change activity
        Intent suggested = new Intent();
        suggested.putExtra("event_id", eventID);
        suggested.putExtra("location_id", locationID);
        setResult(Activity.RESULT_OK,suggested);
        finish();

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
                holder.locationId = location.getInt("locationID");
                holder.locationAddressView.setText(location.getString("address"));
                holder.locationStateView.setText(location.getString("state"));
                holder.locationCityView.setText(location.getString("city"));
                holder.position = holder.getAdapterPosition();
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(SuggestionsListActivity.this, "click" , Toast.LENGTH_LONG).show();
                        pickLocation(holder.locationId,holder.eventId);
                    }
                });

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

        private class ViewHolder extends RecyclerView.ViewHolder{
            int eventId = -1;
            int locationId=-1;
            TextView locationAddressView;
            TextView locationStateView;
            TextView locationCityView;
            int position;
            LinearLayout linearLayout;

            ViewHolder(@NonNull View view) {
                super(view);
                locationAddressView = view.findViewById(R.id.address);
                locationStateView = view.findViewById(R.id.state_info);
                locationCityView = view.findViewById(R.id.city_info);
                linearLayout = view.findViewById(R.id.tableLayout);



            }


        }


    }



}
