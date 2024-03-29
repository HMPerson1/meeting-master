package com.example.meetingmasterclient.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.meetingmasterclient.MapsActivity;
import com.example.meetingmasterclient.R;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.util.Calendar;

import retrofit2.Call;

public class LeaveNowAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra("event_id", 0);
        if (eventId != 0) {
            Call<MeetingService.EventsData> c = Server.getService().getEventfromId(eventId);
            c.enqueue(Server.mkCallback(
                    (call, response) -> {
                        MeetingService.EventsData event = response.body();
                        setNotification(context, event);
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }
    }

    public static void setNotification(Context context, MeetingService.EventsData event) {
        Intent notifier = new Intent(context, MapsActivity.class);
        notifier.putExtra("event_id", event.getPk());
        PendingIntent pending = PendingIntent.getActivity(context, 0, notifier, 0);

        Calendar currentTime = Calendar.getInstance();
        String content = "You should leave now ("
                + currentTime.get(Calendar.HOUR_OF_DAY) + ":" + currentTime.get(Calendar.MINUTE)
                + ") to arrive on time";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Notifications.CHANNEL_LEAVE_NOW)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(event.event_name)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pending);

        NotificationManagerCompat.from(context).notify(100, builder.build());
    }
}
