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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


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

    public void confirmInput(View v){
        //check that both fields are filled
        if (!validateEmail() || !validatePassword()){
            return;
        }



        submitLoginRequest(v);





    }

    public void submitLoginRequest(View v){
        confirmInput(v);

        //TODO parse information to be sent to server for login

        //check with server to see whether password and email match
        String url = "";
        URL object;


        JSONObject json = new JSONObject();
        try {
            json.put("username", textInputEmail.getEditText().getText().toString());
            json.put("password", textInputPassword.getEditText().getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Send put request to server
        try {
            object = new URL(url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("PUT");

            //get response from server about whether the password is valid
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, url, json, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //login successful, switch activity to homepage
                            Toast.makeText(Login.this,response.toString() , Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Login.this,"Invalid Username or Password" , Toast.LENGTH_LONG).show();

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
