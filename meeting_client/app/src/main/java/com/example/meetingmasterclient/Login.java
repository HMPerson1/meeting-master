package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.io.IOException;

import retrofit2.Call;

import static com.example.meetingmasterclient.utils.PreferanceKeys.PREF_KEY_TOKEN;
import static com.example.meetingmasterclient.utils.PreferanceKeys.PREF_NAME_AUTH_TOKEN;


public class Login extends AppCompatActivity {
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                startActivity(new Intent(Login.this, PasswordReset.class));
            }
        });

        // skip if the user has already logged in
        SharedPreferences preferences = getSharedPreferences(PREF_NAME_AUTH_TOKEN, MODE_PRIVATE);
        if (preferences.contains(PREF_KEY_TOKEN)) {
            Server.authenticate(preferences.getString(PREF_KEY_TOKEN, null));
            startActivity(new Intent(Login.this, EventListView.class));
            finish();
        }
    }

    private boolean validateEmail(){
        return true;
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
        return (validateEmail() && validatePassword());
    }

    public void submitLoginRequest(View v){
        if (!confirmInput(v)) return;

        String username = textInputEmail.getEditText().getText().toString();
        String password = textInputPassword.getEditText().getText().toString();
        Call<MeetingService.AuthToken> c = Server.getService().login(new MeetingService.LoginData(username, password));
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Server.authenticate(response.body().key);
                        Toast.makeText(Login.this, "Login success", Toast.LENGTH_LONG).show();
                        getSharedPreferences(PREF_NAME_AUTH_TOKEN, MODE_PRIVATE).edit()
                                .putString(PREF_KEY_TOKEN, response.body().key).apply();
                        startActivity(new Intent(Login.this, EventListView.class));
                        finish();
                    } else {
                        try {
                            assert response.errorBody() != null;
                            System.out.println("response.error = " + response.errorBody().string());
                            Toast.makeText(Login.this, "Login failure", Toast.LENGTH_LONG).show();
                            Toast.makeText(Login.this,response.toString() , Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
