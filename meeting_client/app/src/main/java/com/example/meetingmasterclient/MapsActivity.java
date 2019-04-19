package com.example.meetingmasterclient;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.Route;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private int eventID;
    public GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng currentLocation;
    private static final int LOCATION_PERMISSION = 10;
    private DrawerLayout drawerLayout;
    private List<MeetingService.AttendeeLocationData> locationData = new LinkedList<>();
    private LinkedList<Marker> markers = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        RecyclerView etaRecyclerView = findViewById(R.id.eta_list);
        etaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        EtaAdapter adapter1 = new EtaAdapter();
        etaRecyclerView.setAdapter(adapter1);

        eventID = getIntent().getIntExtra("event_id", 0);
        if (eventID != 0) {
            drawerLayout = findViewById(R.id.drawer_layout);

            getLocationPermission();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            super.onBackPressed();
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mapInit();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapInit();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "You cannot use the map feature without these permissions",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void mapInit() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*try {
            LocationUpdateService.start(getApplicationContext(), getIntent().getIntExtra("event_id", 0));
        } catch(SecurityException e) {
            e.printStackTrace();
        }*/

        updateLocationUI();
        Timer timer = new Timer();
        timer.schedule(new Locate(), 0, 15000);
        //getCurrentLocation();

        // Mock user location
        LatLng pmu = new LatLng(40.4263, -86.9105);
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (SecurityException e)  {
            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();

            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location current = (Location)task.getResult();
                        currentLocation = new LatLng(current.getLatitude(), current.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                        getLocations();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch(SecurityException e) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocations() {
        Server.getService().getCurrentLocations(String.valueOf(eventID)).enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        locationData.clear();
                        locationData.add(new MeetingService.AttendeeLocationData("", "You", currentLocation.latitude, currentLocation.longitude));
                        List<MeetingService.AttendeeLocationData> locations = response.body();
                        for (int i = 0; i < locations.size(); i++) {
                            locationData.add(locations.get(i));
                        }

                        getRoutes(locations);
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not get users' locations", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void getRoutes(List<MeetingService.AttendeeLocationData> origins) {
        Server.getService().getEventfromId(eventID).enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        MeetingService.LocationData location = response.body().event_location;

                        Geocoder geocoder = new Geocoder(getApplicationContext());

                        try {
                            Address dest = ((ArrayList<Address>)geocoder
                                    .getFromLocationName(
                                            location.getStreet_address()
                                                    + ", "
                                                    + location.getCity() + ", "
                                                    + location.getState(),1)).get(0);

                            LatLng destination = new LatLng(dest.getLatitude(), dest.getLongitude());

                            (new Route(this, locationToLatLng(origins), destination)).execute();

                        } catch(IOException e) {
                            System.err.println("IO ERROR MAPS");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not get event location", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    public LatLng[] locationToLatLng(List<MeetingService.AttendeeLocationData> locations) {
        LatLng[] arr = new LatLng[locations.size() + 1];
        arr[0] = currentLocation;
        MeetingService.AttendeeLocationData current;

        for (int i = 0; i < locations.size(); i++) {
            current = locations.get(i);
            arr[i + 1] = new LatLng(current.lat, current.lon);
        }

        return arr;
    }

    public void onRoutesCompleted(JSONObject[] routes) {
        while (!markers.isEmpty()) {
            Marker m = markers.remove();
            m.remove();
        }

        for (int i = 0; i < routes.length; i++) {
            MeetingService.AttendeeLocationData current = locationData.get(i);
            markers.add(mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(current.lat, current.lon))
                    .title(current.user_full_name)));
        }

        // For Ariya: Do the ETA stuff here
    }

    class Locate extends TimerTask {
        @Override
        public void run() {
            getCurrentLocation();
        }
    }

    class EtaAdapter extends RecyclerView.Adapter<EtaAdapter.EtaViewHolder> {

        // TODO: real data
        // from routes[0].legs[0].duration.text
        String[][] data = {{"John Doe", "1 min"}, {"Jane Doe", "2 min"}};

        @NonNull
        @Override
        public EtaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_eta, parent, false);
            TextView nameView = view.findViewById(R.id.eta_item_name);
            TextView timeView = view.findViewById(R.id.eta_item_time);
            return new EtaViewHolder(view, nameView, timeView);
        }

        @Override
        public void onBindViewHolder(@NonNull EtaViewHolder etaViewHolder, int position) {
            etaViewHolder.nameView.setText(data[position][0]);
            etaViewHolder.timeView.setText(data[position][1]);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        class EtaViewHolder extends RecyclerView.ViewHolder {
            private final TextView nameView;
            private final TextView timeView;

            EtaViewHolder(@NonNull View itemView, TextView nameView, TextView timeView) {
                super(itemView);
                this.nameView = nameView;
                this.timeView = timeView;
            }
        }
    }
}
