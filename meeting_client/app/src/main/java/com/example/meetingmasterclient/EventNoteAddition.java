
package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class EventNoteAddition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_note_addition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        JSONObject noteParam = new JSONObject();
        // TODO: Add note to JSON


        // TODO: Add URL
        JsonObjectRequest noteRequest = new JsonObjectRequest(Request.Method.POST, "", noteParam, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Note added successfully", Toast.LENGTH_SHORT).show();
                ((TextInputEditText)findViewById(R.id.note)).setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
