package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ProfileEdition extends AppCompatActivity {
    private TextInputEditText textInputFirstName;
    private TextInputLayout textInputLastName;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputEmailAddress;
    private TextInputLayout textInputPhoneNumber;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edition);

        //initialize user fields
        textInputFirstName = findViewById(R.id.thisisatest//text_edit_first_name);
        textInputLastName = findViewById(R.id.text_edit_last_name);
        textInputUsername = findViewById(R.id.text_edit_username);
        textInputEmailAddress = findViewById(R.id.text_edit_email);
        textInputPhoneNumber = findViewById(R.id.text_edit_phone_number);
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

    public void sendProfileEditionRequest(View view){

    }



}
