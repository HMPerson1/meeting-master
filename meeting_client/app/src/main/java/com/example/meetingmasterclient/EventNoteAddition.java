
package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EventNoteAddition extends AppCompatActivity {
    TextInputEditText noteField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_note_addition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addButton = (Button)findViewById(R.id.add_note);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteField = (TextInputEditText)findViewById(R.id.note);
                String note = noteField.getText().toString();

                if (!note.isEmpty()) {
                    sendNote(note);
                }
            }
        });
    }

    RequestQueue noteQueue;

    private void sendNote(String note) {
        noteQueue = Volley.newRequestQueue(this);

        JSONObject noteParam = new JSONObject();

        try {
            noteParam.put("note", note);

            // TODO: Add URL
            JsonObjectRequest noteRequest = new JsonObjectRequest(Request.Method.POST, "", noteParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Note added successfully", Toast.LENGTH_SHORT).show();
                    noteField.setText("");

                    noteQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "An error has occurred in the network", Toast.LENGTH_SHORT).show();

                    noteQueue.stop();
                }
            });

            noteQueue.add(noteRequest);
        } catch(JSONException e) {
            Toast.makeText(getApplicationContext(), "An error has occurred while processing the information", Toast.LENGTH_SHORT).show();

            noteQueue.stop();
        }
    }
}
