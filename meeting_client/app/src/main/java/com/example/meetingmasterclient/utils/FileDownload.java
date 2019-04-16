package com.example.meetingmasterclient.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.meetingmasterclient.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FileDownload extends AsyncTask<String, Void, String> {
    private Context context;
    private NotificationCompat.Builder notification;
    private static final int NOTIF_ID = 200;

    public FileDownload(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        notification = new NotificationCompat.Builder(context, Notifications.CHANNEL_DOWNLOADING_FILE)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Attachment")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setProgress(0, 0, true);

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification.build());
    }

    @Override
    protected String doInBackground(String... params) {
        /*
        try {
            URL url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
        } catch(Exception e) {
            return null;
        }
        */
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        notification.setProgress(0, 0, false)
                .setContentText("Download complete");

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification.build());
    }
}
