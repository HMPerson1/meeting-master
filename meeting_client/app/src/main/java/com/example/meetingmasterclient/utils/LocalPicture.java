package com.example.meetingmasterclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.meetingmasterclient.R;

import java.io.FileDescriptor;
import java.io.IOException;

public class LocalPicture extends AsyncTask<Uri, Void, Bitmap> {
    private AppCompatActivity activity;

    public LocalPicture(AppCompatActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected Bitmap doInBackground(Uri... uri) {
        try {
            ParcelFileDescriptor pfd = activity.getApplicationContext().getContentResolver().openFileDescriptor(uri[0], "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fd);
            pfd.close();
            return image;
        } catch(IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            ((ImageView)activity.findViewById(R.id.imageView)).setImageBitmap(result);
        } else {
            Toast.makeText(activity.getApplicationContext(), "The picture has been loaded, but cannot be shown", Toast.LENGTH_SHORT).show();
        }
    }
}