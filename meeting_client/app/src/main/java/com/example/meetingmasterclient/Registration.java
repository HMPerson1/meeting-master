package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Registration extends AppCompatActivity {
    private TextInputLayout textInputFirstName;
    private TextInputLayout textInputLastName;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputEmailAddress;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private TextInputLayout textInputPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        /**Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });**/

        //initialize all user fields
        textInputFirstName = findViewById(R.id.text_input_first_name);
        textInputLastName = findViewById(R.id.text_input_last_name);
        textInputUsername = findViewById(R.id.text_input_username);
        textInputEmailAddress = findViewById(R.id.text_input_email);
        textInputPhoneNumber = findViewById(R.id.text_input_phone_number);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirm_password);

        Button registerButton = findViewById(R.id.register_button);
    }

    //check for form completion

    private boolean passwordsMatch() {
        String password = textInputPassword.getEditText().getText().toString();
        String passwordC = textInputConfirmPassword.getEditText().getText().toString();
        if (!password.equals(passwordC)){
            textInputPassword.setError("Passwords must match");
            textInputConfirmPassword.setError("Passwords must match");
            return false;
        } else {
            textInputPassword.setError(null);
            textInputConfirmPassword.setError(null);
            return true;
        }
    }

    private boolean validateUsername(){
        String username = textInputUsername.getEditText().getText().toString();
        if (username.isEmpty()){
            textInputUsername.setError("Username cannot be empty");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(){
        String firstName = textInputFirstName.getEditText().getText().toString();
        if (firstName.isEmpty()){
            textInputFirstName.setError("First name cannot be empty");
            return false;
        } else {
            textInputFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName(){
        String lastName = textInputLastName.getEditText().getText().toString();
        if (lastName.isEmpty()){
            textInputLastName.setError("Last name cannot be empty");
            return false;
        } else {
            textInputLastName.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String email = textInputEmailAddress.getEditText().getText().toString();
        if (email.isEmpty()){
            textInputEmailAddress.setError("Email cannot be empty");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            textInputEmailAddress.setError("Not a valid email address");
            return false;
        } else {
            textInputEmailAddress.setError(null);
            return true;
        }
    }

    public void confirmInput(View v){
        if (!passwordsMatch() || !validateEmail() || !validateFirstName() || !validateLastName()
                || !validateUsername()){
            return;
        }

        //TODO parse information to be sent to server for registration
    }
}
