
package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.DataOutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventNoteAddition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_note_addition);

        addNote();
    }

    private void addNote() {
        Button addButton = (Button)findViewById(R.id.add_note);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText noteField = (TextInputEditText)findViewById(R.id.note);
                String note = noteField.getText().toString();

                sendNote(note);
            }
        });
    }

    private void sendNote(String note) {
        // TODO: Format HTTP request and program response and failure

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // do action
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do action
            }
        });

        queue.add(stringRequest);
    }
}
