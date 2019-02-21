package com.example.meetingmasterclient;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

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
    }

    //methods to check for form completion

    private boolean passwordsMatch(String password, String passwordC) {
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

    private boolean validatePassword(){
        String password = textInputPassword.getEditText().getText().toString();
        String passwordC = textInputConfirmPassword.getEditText().getText().toString();
        if (password.length() < 6){
            textInputPassword.setError("Password must be at least 6 characters");
        }
        if (password.contains(" ")){
            textInputPassword.setError("Password cannot contain spaces");
            return false;
        }
        if (password.isEmpty() && passwordC.isEmpty()) {
            textInputPassword.setError("Password cannot be empty");
            textInputConfirmPassword.setError("Password cannot be empty");
            return false;
        } else if (password.isEmpty() && !passwordC.isEmpty()) {
            textInputPassword.setError("Password cannot be empty");
            return false;
        } else if (!password.isEmpty() && passwordC.isEmpty()){
            textInputConfirmPassword.setError("Password cannot be empty");
            return false;
        } else {
            textInputPassword.setError(null);
        }
        return passwordsMatch(password, passwordC);
    }

    private boolean validateUsername(){
        String username = textInputUsername.getEditText().getText().toString().trim();
        if (username.isEmpty()){
            textInputUsername.setError("Username cannot be empty");
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(){
        String firstName = textInputFirstName.getEditText().getText().toString().trim();
        if (firstName.isEmpty()){
            textInputFirstName.setError("First name cannot be empty");
            return false;
        } else {
            textInputFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName(){
        String lastName = textInputLastName.getEditText().getText().toString().trim();
        if (lastName.isEmpty()){
            textInputLastName.setError("Last name cannot be empty");
            return false;
        } else {
            textInputLastName.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String email = textInputEmailAddress.getEditText().getText().toString().trim();
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

    private boolean validatePhoneNumber(){
        String phone = textInputPhoneNumber.getEditText().getText().toString().trim();
        if (phone.isEmpty()){
            textInputPhoneNumber.setError("Phone number cannot be empty");
            return false;
        } else {
            textInputPhoneNumber.setError(null);
            return true;
        }
    }

    public boolean confirmInput(View v){
        return (!validatePassword() | !validateEmail() | !validateFirstName() | !validateLastName()
                | !validateUsername() | validatePhoneNumber());
    }

    public String formJSON(){
        //parse information to be sent to server for registration
        String username = textInputUsername.getEditText().getText().toString().trim();
        String email = textInputEmailAddress.getEditText().getText().toString().trim();
        String password1 = textInputPassword.getEditText().getText().toString().trim();
        String password2 = textInputConfirmPassword.getEditText().getText().toString().trim();
        String first_name = textInputFirstName.getEditText().getText().toString().trim();
        String last_name = textInputLastName.getEditText().getText().toString().trim();
        String phone_number = textInputPhoneNumber.getEditText().getText().toString().trim();

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("first_name", first_name);
            json.put("last_name", last_name);
            json.put("email", email);
            json.put("password1", password1);
            json.put("password2", password2);
            json.put("phone_number", phone_number);
            //TODO implement picture in future sprint
        } catch (JSONException j){
            j.printStackTrace();
        }

        System.out.println(json.toString());
        return json.toString();
    }

    public void sendRegistrationRequest(View v){
        if (!confirmInput(v)) return;

        String json = formJSON();
        HttpURLConnection http;
        try {
            URL url;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
