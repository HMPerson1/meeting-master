package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /**Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });**/

        //init. user fields
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);

        //register button
        Button regbtn = (Button)findViewById(R.id.register_button);
        regbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Login.this, Registration.class));
            }
        });

        Button fpbtn = (Button)findViewById(R.id.forget_password_button);
        fpbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Login.this, Registration.class));
            }
        });
    }

    private boolean validateEmail(){
        String email = textInputEmail.getEditText().getText().toString();
        if (email.isEmpty()){
            textInputEmail.setError("Email cannot be empty");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            textInputEmail.setError("Not a valid email address");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String password = textInputPassword.getEditText().getText().toString();
        if (password.isEmpty()){
            textInputPassword.setError("Password cannot be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    public boolean confirmInput(View v){
        return (!validateEmail() || !validatePassword());
    }

    public void submitLoginRequest(View v){
        if (!confirmInput(v)) return;

        //TODO parse information to be sent to server for login
    }
}
