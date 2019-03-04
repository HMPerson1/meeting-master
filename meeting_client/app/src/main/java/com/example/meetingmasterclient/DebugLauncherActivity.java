package com.example.meetingmasterclient;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class DebugLauncherActivity extends AppCompatActivity {

    private static final String TAG = "DebugLauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout linearLayout = findViewById(R.id.activities_list);

        ActivityInfo[] activityInfos = new ActivityInfo[0];
        try {
            activityInfos = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).activities;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onCreate: get activity list", e);
        }
        Arrays.stream(activityInfos).map((Function<ActivityInfo, Class>) activityInfo -> {
            try {
                return getClassLoader().loadClass(activityInfo.name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).forEach(activityClass -> {
            Button button = new Button(DebugLauncherActivity.this);
            button.setText(activityClass.getSimpleName());
            button.setOnClickListener(v -> startActivity(new Intent(DebugLauncherActivity.this, activityClass)));
            linearLayout.addView(button);
        });

        ensureGoogleApiAvailability();
        ensureNotificationChannels();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ensureGoogleApiAvailability();
    }

    // TODO: copy to real main activity
    private void ensureGoogleApiAvailability() {
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                finish();
            }
        });
    }

    // TODO: copy to real main activity
    private void ensureNotificationChannels() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            // invites
            if (notificationManager.getNotificationChannel(Constants.CHANNEL_INVITE_ID) != null) {
                NotificationChannel inviteChannel = new NotificationChannel(
                        Constants.CHANNEL_INVITE_ID,
                        getString(R.string.channel_invite_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                inviteChannel.setDescription(getString(R.string.channel_invite_description));
                notificationManager.createNotificationChannel(inviteChannel);
            }

            // edits
            if (notificationManager.getNotificationChannel(Constants.CHANNEL_EDIT_ID) != null){
                NotificationChannel editChannel = new NotificationChannel(
                        Constants.CHANNEL_EDIT_ID,
                        getString(R.string.channel_edit_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                editChannel.setDescription(getString(R.string.channel_edit_description));
                notificationManager.createNotificationChannel(editChannel);
            }
        }
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
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                });
        }
        return true;
    }
}
