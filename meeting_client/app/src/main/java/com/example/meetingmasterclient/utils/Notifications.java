package com.example.meetingmasterclient.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;

import com.example.meetingmasterclient.R;

import java.util.Arrays;

public final class Notifications {
    public static final String CHANNEL_INVITE_ID = "invite";
    public static final String CHANNEL_EDIT_ID = "edit";
    public static final String CHANNEL_ARRIVED_HOME_ID = "arrived_home";
    public static final String CHANNEL_LIVE_LOCATION_ID = "live_location";
    public static final String CHANNEL_EVENT_STARTING_SOON = "event_starting_soon";
    public static final String CHANNEL_LEAVE_NOW = "leave_now";
    public static final String CHANNEL_DOWNLOADING_FILE = "download_file";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel mkNotificationChannel(String id, CharSequence name, @Importance int importance, String description) {
        NotificationChannel ret = new NotificationChannel(id, name, importance);
        ret.setDescription(description);
        return ret;
    }

    public static void ensureNotificationChannels(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel[] channels = new NotificationChannel[]{
                    // invites
                    mkNotificationChannel(
                            CHANNEL_INVITE_ID,
                            context.getString(R.string.channel_invite_name),
                            NotificationManager.IMPORTANCE_DEFAULT,
                            context.getString(R.string.channel_invite_description)),
                    // edits
                    mkNotificationChannel(
                            CHANNEL_EDIT_ID,
                            context.getString(R.string.channel_edit_name),
                            NotificationManager.IMPORTANCE_DEFAULT,
                            context.getString(R.string.channel_edit_description)),
                    // arrived home
                    mkNotificationChannel(
                            CHANNEL_ARRIVED_HOME_ID,
                            context.getString(R.string.channel_arrived_home_name),
                            NotificationManager.IMPORTANCE_DEFAULT,
                            context.getString(R.string.channel_arrived_home_description)),
                    // live location
                    mkNotificationChannel(
                            CHANNEL_LIVE_LOCATION_ID,
                            context.getString(R.string.channel_live_location_name),
                            NotificationManager.IMPORTANCE_LOW,
                            context.getString(R.string.channel_live_location_description)),
                    mkNotificationChannel(
                            CHANNEL_LEAVE_NOW,
                            context.getString(R.string.channel_leave_now_name),
                            NotificationManager.IMPORTANCE_DEFAULT,
                            context.getString(R.string.channel_leave_now_description)),
                    mkNotificationChannel(
                            CHANNEL_DOWNLOADING_FILE,
                            context.getString(R.string.channel_download_file_name),
                            NotificationManager.IMPORTANCE_DEFAULT,
                            context.getString(R.string.channel_download_file_description))
            };
            context.getSystemService(NotificationManager.class)
                    .createNotificationChannels(Arrays.asList(channels));
        }
    }

    @IntDef({NotificationManager.IMPORTANCE_DEFAULT, NotificationManager.IMPORTANCE_HIGH, NotificationManager.IMPORTANCE_LOW, NotificationManager.IMPORTANCE_MIN, NotificationManager.IMPORTANCE_NONE, NotificationManager.IMPORTANCE_UNSPECIFIED})
    private @interface Importance {
    }
}
