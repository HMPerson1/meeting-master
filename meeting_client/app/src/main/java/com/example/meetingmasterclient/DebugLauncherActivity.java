package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class DebugLauncherActivity extends AppCompatActivity {

    private static final String TAG = "DebugLauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_launcher);

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
    }
}
