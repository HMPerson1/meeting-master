package com.example.meetingmasterclient.utils;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.meetingmasterclient.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.ResponseBody;

public class FileDownload extends AsyncTask<Void, Void, File> {
    private Context context;
    private ResponseBody body;
    private String name;
    private NotificationCompat.Builder notification;
    private static final int NOTIF_ID = 200;

    public FileDownload(Context context, ResponseBody body, String name) {
        this.context = context;
        this.body = body;
        this.name = name;
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
    protected File doInBackground(Void... params) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "attachment_" + name);
            file.delete();
            file.createNewFile();

            InputStream input = body.byteStream();
            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }

            output.close();
            input.close();

            return file;
        } catch(IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(File result) {
        if (result != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.fromFile(result));
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            notification.setProgress(0, 0, false)
                    .setContentText("Download complete")
                    .setContentIntent(pIntent);
        } else {
            notification.setProgress(0, 0, false)
                    .setContentText("An error has ocurred");
        }

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification.build());
    }
}
