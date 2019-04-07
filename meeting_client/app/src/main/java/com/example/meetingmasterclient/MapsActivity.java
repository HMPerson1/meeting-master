package com.example.meetingmasterclient;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng currentLocation;
    private static final int LOCATION_PERMISSION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
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
        mLocationPermissionGranted = true;

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

        updateLocationUI();
        getCurrentLocation();

        // Mock user location
        LatLng pmu = new LatLng(40.4263, -86.9105);
        mMap.addMarker(new MarkerOptions().position(pmu).title("Daniel is here"));
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
                        getRoute();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch(SecurityException e) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRoute() {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        int eventId = getIntent().getIntExtra("event_id", 0);
        if (eventId != 0) {
            Call<MeetingService.EventsData> c = Server.getService().getEvents("/events/" + eventId);
            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        MeetingService.EventsData event = response.body();
                        MeetingService.LocationData location = event.event_location;

                        try {
                            ArrayList<Address> dest = (ArrayList<Address>)geocoder
                                    .getFromLocationName(
                                            location.getStreet_address()
                                                    + ", "
                                                    + location.getCity() + ", "
                                                    + location.getState(),1);

                            LatLng destination = new LatLng(dest.get(0).getLatitude(), dest.get(0).getLongitude());

                            (new Route(getApplicationContext(), mMap, currentLocation, destination)).execute();
                        } catch(IOException e) {
                            System.err.println("IO ERROR MAPS");
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }
    }
}
