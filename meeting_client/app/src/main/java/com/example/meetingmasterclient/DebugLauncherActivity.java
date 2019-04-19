package com.example.meetingmasterclient;

import android.Manifest.permission;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.meetingmasterclient.utils.Notifications;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.example.meetingmasterclient.utils.PreferanceKeys.PREF_KEY_TOKEN;
import static com.example.meetingmasterclient.utils.PreferanceKeys.PREF_NAME_AUTH_TOKEN;

public class DebugLauncherActivity extends AppCompatActivity {

    private static final String TAG = "DebugLauncherActivity";
    private static final int REQUEST_CODE_REQUEST_PERMS_FOR_START_LOCSERV = 33;
    private static final int REQUEST_CODE_CHECK_SETTINGS = 34;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout linearLayout = findViewById(R.id.activities_list);
        TextInputLayout inputEventId = findViewById(R.id.debug_input_event_id);
        TextInputLayout inputUserId = findViewById(R.id.debug_input_user_id);

        ActivityInfo[] activityInfos = new ActivityInfo[0];
        try {
            activityInfos = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).activities;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onCreate: get activity list", e);
        }
        Arrays.stream(activityInfos).flatMap(activityInfo -> {
            try {
                return Stream.of(getClassLoader().loadClass(activityInfo.name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Stream.empty();
        }).forEach(activityClass -> {
            Button button = new Button(DebugLauncherActivity.this);
            button.setText(activityClass.getSimpleName());
            button.setOnClickListener(v -> {
                Intent intent = new Intent(DebugLauncherActivity.this, activityClass);
                String event_id_str = inputEventId.getEditText().getText().toString();
                if (!event_id_str.isEmpty()) {
                    intent.putExtra("event_id", Integer.parseInt(event_id_str));
                }
                String user_id_str = inputUserId.getEditText().getText().toString();
                if (!user_id_str.isEmpty()) {
                    intent.putExtra("user_id", Integer.parseInt(user_id_str));
                }
                startActivity(intent);
            });
            linearLayout.addView(button);
        });

        ensureGoogleApiAvailability();
        Notifications.ensureNotificationChannels(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureGoogleApiAvailability();
    }

    // TODO: copy to real main activity
    private void ensureGoogleApiAvailability() {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                .addOnFailureListener(this, e -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_debug_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_fcm_token:
                FirebaseInstanceId.getInstance().getInstanceId(
                ).addOnSuccessListener(this, instanceIdResult -> {
                    // Get new Instance ID token
                    String token = instanceIdResult.getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(this, e ->
                        Log.w(TAG, "getInstanceId failed", e));
                return true;
            case R.id.locserv_start:
                tryStartLocserv();
                return true;
            case R.id.locserv_stop:
                stopService(new Intent(this, LocationUpdateService.class));
                return true;
            case R.id.logout:
                getSharedPreferences(PREF_NAME_AUTH_TOKEN, MODE_PRIVATE).edit().remove(PREF_KEY_TOKEN).apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void tryStartLocserv() {
        LocationServices.getSettingsClient(this).checkLocationSettings(
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(LocationUpdateService.LOCATION_REQUEST).build()
        ).addOnSuccessListener(this, locationSettingsResponse -> {
            if (locationSettingsResponse.getLocationSettingsStates().isLocationUsable()) {
                if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationUpdateService.start(this, 0);
                } else {
                    // need permissions
                    requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_REQUEST_PERMS_FOR_START_LOCSERV);
                }
            }
        }).addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // need location to be on
                    ((ResolvableApiException) e).startResolutionForResult(this, REQUEST_CODE_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_REQUEST_PERMS_FOR_START_LOCSERV:
                if (grantResults.length < 1) {
                    // user cancelled
                    handleDenyLocation();
                    return;
                }
                switch (grantResults[0]) {
                    case PackageManager.PERMISSION_GRANTED:
                        tryStartLocserv();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        handleDenyLocation();
                        break;
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        tryStartLocserv();
                        break;
                    case RESULT_CANCELED:
                        handleDenyLocation();
                        break;
                }
                return;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleDenyLocation() {
        Toast.makeText(this, "Your location won't be shown on the map", Toast.LENGTH_SHORT).show();
    }
}
