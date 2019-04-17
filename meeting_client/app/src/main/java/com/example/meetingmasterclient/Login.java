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
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;


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
                startActivity(new Intent(Login.this, PasswordReset.class));
            }
        });
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
                    Toast.makeText(Login.this,response.toString() , Toast.LENGTH_LONG).show();
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Server.authenticate(response.body().key);
                    } else {
                        try {
                            assert response.errorBody() != null;
                            System.out.println("response.error = " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();


                        }
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
        Intent debug = new Intent(Login.this, DebugLauncherActivity.class);
        startActivity(debug);
    }
}
