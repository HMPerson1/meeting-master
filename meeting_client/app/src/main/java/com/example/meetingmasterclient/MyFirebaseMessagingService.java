package com.example.meetingmasterclient;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String PREF_KEY_LAST_ID = "last_id";
    private static final String PREF_NAME = "notifications";
    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            String kind = Objects.requireNonNull(remoteMessage.getData().get("kind"), "data.kind == null");
            String eventId = Objects.requireNonNull(remoteMessage.getData().get("event_id"), "data.event_id == null");
            String eventName = Objects.requireNonNull(remoteMessage.getData().get("event_name"), "data.event_name == null");
            String channelId;
            String contentTitle;
            String contentText;
            switch (kind) {
                case "invite": {
                    channelId = Constants.CHANNEL_INVITE_ID;
                    contentTitle = getString(R.string.notification_invite_title);
                    contentText = getString(R.string.notification_invite_body, eventName);
                    break;
                }
                case "edit": {
                    channelId = Constants.CHANNEL_EDIT_ID;
                    if (!shouldShowEditNotification(eventId)) {
                        return;
                    }
                    contentTitle = getString(R.string.notification_edit_title);
                    contentText = getString(R.string.notification_edit_body, eventName);
                    break;
                }
                default:
                    Log.e(TAG, "onMessageReceived: unknown data.kind: " + kind);
                    return;
            }

            Intent intent = new Intent(this, null); // TODO: eventdetailsactivity
            intent.putExtra("event_id", eventId);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);

            // our notifications are "fire-and-forget" so we don't keep track of ids
            NotificationManagerCompat.from(this).notify(nextNotificationId(), notification.build());
        } catch (NullPointerException e) {
            Log.w(TAG, "onMessageReceived: ", e);
        }
    }

    private boolean shouldShowEditNotification(String eventId) {
        // TODO
        return true;
    }

    public int nextNotificationId() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        synchronized (getApplication()) {
            int id = preferences.getInt(PREF_KEY_LAST_ID, 0) + 1;
            preferences.edit().putInt(PREF_KEY_LAST_ID, id).apply();
            return id;
        }
    }
}
