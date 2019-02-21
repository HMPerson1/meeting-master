package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ProfileEdition extends AppCompatActivity {
    private TextInputLayout textInputFirstName;
    private TextInputLayout textInputLastName;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputEmailAddress;
    private TextInputLayout textInputPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edition);

        //initialize user fields
        textInputFirstName = findViewById(R.id.textInputLayout2); //TODO change id name
        textInputLastName = findViewById(R.id.last_name_layout);
        textInputUsername = findViewById(R.id.username_layout);
        textInputEmailAddress = findViewById(R.id.email_layout);
        textInputPhoneNumber = findViewById(R.id.phone_layout);
    }



}
