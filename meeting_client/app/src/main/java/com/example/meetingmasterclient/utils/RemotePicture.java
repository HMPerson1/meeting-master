package com.example.meetingmasterclient.utils;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.meetingmasterclient.R;

import java.io.InputStream;
import java.net.URL;

public class RemotePicture extends AsyncTask<String, Void, Drawable> {
    private AppCompatActivity activity;

    public RemotePicture(AppCompatActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected Drawable doInBackground(String... url) {
        try {
            InputStream stream = (InputStream) new URL(url[0]).getContent();
            Drawable draw = Drawable.createFromStream(stream, "");
            return draw;
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable result) {
        if (result != null) {
            ((ImageView)activity.findViewById(R.id.profile_details_picture)).setImageDrawable(result);
        } else {
            Log.d("Picture", "The picture cannot be shown");
            //Toast.makeText(activity.getApplicationContext(), "The picture cannot be shown", Toast.LENGTH_SHORT).show();
        }
    }
}
