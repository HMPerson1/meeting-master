package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;

public class ProfileEdition extends AppCompatActivity {
    private TextInputEditText textInputFirstName;
    private TextInputEditText textInputLastName;
    private TextInputEditText textInputUsername;
    private TextInputEditText textInputEmailAddress;
    private TextInputEditText textInputPhoneNumber;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edition);

        //initialize user fields
        textInputFirstName = findViewById(R.id.text_input_edit_first_name);
        textInputLastName = findViewById(R.id.text_input_edit_last_name);
        textInputUsername = findViewById(R.id.text_input_edit_username);
        textInputEmailAddress = findViewById(R.id.text_input_edit_email);
        textInputPhoneNumber = findViewById(R.id.text_input_edit_phone_number);

        getCurrentProfile(textInputFirstName, textInputLastName, textInputUsername, textInputEmailAddress,
                textInputPhoneNumber);
        //TODO remove next two lines of code whenever getCurrentProfile() is fixed
        String name = "name";
        textInputFirstName.setText(name);

        confirmButton = findViewById(R.id.confirm_profile_changes_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendProfileEditionRequest(view);
            }
        });
    }

    public void getCurrentProfile(TextInputEditText first, TextInputEditText last,
                                  TextInputEditText username, TextInputEditText email,
                                  TextInputEditText phone){
        Call<MeetingService.UserProfile> c = Server.getService().getCurrentUser();
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        Toast.makeText(ProfileEdition.this, "Successful current user response: "
                                + response.toString(), Toast.LENGTH_LONG).show();
                        Log.d("Current user response", response.toString());
                    } else {
                        Toast.makeText(ProfileEdition.this, "Error: " + response.toString(),
                                Toast.LENGTH_LONG).show();
                        Log.d("Current user error", response.toString());
                    }
                },
        (call, t) -> t.printStackTrace()
        ));
    }

    public void sendProfileEditionRequest(View view){

    }
    
}
