
package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
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

                if (!note.isEmpty()) {
                    // TODO: Create and send HTTP request to server to add the note

                }
            }
        });
    }
}
