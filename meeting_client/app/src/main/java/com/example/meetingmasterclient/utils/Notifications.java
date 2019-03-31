package com.example.meetingmasterclient.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.meetingmasterclient.R;

public final class Notifications {
    public static final String CHANNEL_INVITE_ID = "invite";
    public static final String CHANNEL_EDIT_ID = "edit";
    public static final String CHANNEL_ARRIVED_HOME_ID = "arrived_home";

    public static void ensureNotificationChannels(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            // invites
            if (notificationManager.getNotificationChannel(CHANNEL_INVITE_ID) == null) {
                NotificationChannel inviteChannel = new NotificationChannel(
                        CHANNEL_INVITE_ID,
                        context.getString(R.string.channel_invite_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                inviteChannel.setDescription(context.getString(R.string.channel_invite_description));
                notificationManager.createNotificationChannel(inviteChannel);
            }

            // edits
            if (notificationManager.getNotificationChannel(CHANNEL_EDIT_ID) == null) {
                NotificationChannel editChannel = new NotificationChannel(
                        CHANNEL_EDIT_ID,
                        context.getString(R.string.channel_edit_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                editChannel.setDescription(context.getString(R.string.channel_edit_description));
                notificationManager.createNotificationChannel(editChannel);
            }

            // arrived home
            if (notificationManager.getNotificationChannel(CHANNEL_ARRIVED_HOME_ID) == null) {
                NotificationChannel arrivedHomeChannel = new NotificationChannel(
                        CHANNEL_ARRIVED_HOME_ID,
                        context.getString(R.string.channel_arrived_home_name),
                        NotificationManager.IMPORTANCE_DEFAULT);
                arrivedHomeChannel.setDescription(context.getString(R.string.channel_edit_description));
                notificationManager.createNotificationChannel(arrivedHomeChannel);
            }
        }
    }
}
