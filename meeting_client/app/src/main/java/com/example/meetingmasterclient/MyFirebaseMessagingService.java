package com.example.meetingmasterclient;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.meetingmasterclient.utils.Notifications;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

class Notifier extends ContextWrapper {
    private static final String PREF_KEY_LAST_ID = "last_id";
    private static final String PREF_NAME = "notifications";
    private static final String TAG = "Notifier";

    public Notifier(Context base) {
        super(base);
    }

    void onMessageRecieved(Map<String, String> data) {
        try {
            String kind = Objects.requireNonNull(data.get("kind"), "data.kind == null");
            String channelId;
            String contentTitle;
            String contentText;
            Class<?> activityToStart = null;
            Bundle intentExtras = new Bundle();

            switch (kind) {
                case "invite": {
                    String eventId = Objects.requireNonNull(data.get("event_id"), "data.event_id == null");
                    String eventName = Objects.requireNonNull(data.get("event_name"), "data.event_name == null");
                    channelId = Notifications.CHANNEL_INVITE_ID;
                    contentTitle = getString(R.string.notification_invite_title);
                    contentText = getString(R.string.notification_invite_body, eventName);
                    activityToStart = EventDetails.class;
                    intentExtras.putInt("event_id", Integer.parseInt(eventId));
                    break;
                }
                case "edit": {
                    String eventId = Objects.requireNonNull(data.get("event_id"), "data.event_id == null");
                    String eventName = Objects.requireNonNull(data.get("event_name"), "data.event_name == null");
                    channelId = Notifications.CHANNEL_EDIT_ID;
                    if (!shouldShowEditNotification(eventId)) {
                        return;
                    }
                    contentTitle = getString(R.string.notification_edit_title);
                    contentText = getString(R.string.notification_edit_body, eventName);
                    activityToStart = EventDetails.class;
                    intentExtras.putInt("event_id", Integer.parseInt(eventId));
                    break;
                }
                case "arrived_home": {
                    String userFullName = Objects.requireNonNull(data.get("user_full_name"), "data.user_full_name == null");
                    channelId = Notifications.CHANNEL_ARRIVED_HOME_ID;
                    contentTitle = getString(R.string.notification_arrived_home_title);
                    contentText = getString(R.string.notification_arrived_home_body, userFullName);
                    break;
                }
                default:
                    Log.e(TAG, "onMessageReceived: unknown data.kind: " + kind);
                    return;
            }

            PendingIntent pendingIntent;
            if (activityToStart != null) {
                Intent intent = new Intent(this, activityToStart);
                intent.putExtras(intentExtras);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);
                pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                // auto-cancel only works if pendingIntent is non-null
                // https://stackoverflow.com/a/16196933
                pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
            }

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            // our notifications are "fire-and-forget" so we don't keep track of ids
            NotificationManagerCompat.from(this).notify(nextNotificationId(), notification.build());
        } catch (NullPointerException | NumberFormatException e) {
            Log.w(TAG, "onMessageReceived: ", e);
        }
    }

    private boolean shouldShowEditNotification(String eventId) {
        return getSharedPreferences(EventDetails.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(eventId + "checkStatus", true);
    }

    public int nextNotificationId() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        synchronized (getApplicationContext()) {
            int id = preferences.getInt(PREF_KEY_LAST_ID, 0) + 1;
            preferences.edit().putInt(PREF_KEY_LAST_ID, id).apply();
            return id;
        }
    }
}

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Notifier notifier;

    @Override
    public void onCreate() {
        notifier = new Notifier(getApplication());
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        notifier.onMessageRecieved(remoteMessage.getData());
    }
}

