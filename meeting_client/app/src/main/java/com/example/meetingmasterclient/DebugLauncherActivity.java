package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
        TextInputLayout inputEventId = findViewById(R.id.debug_input_event_id);
        TextInputLayout inputUserId = findViewById(R.id.debug_input_user_id);

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
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                finish();
            }
        });
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
