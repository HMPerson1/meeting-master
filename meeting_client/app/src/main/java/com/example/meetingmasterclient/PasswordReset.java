
package com.example.meetingmasterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import org.json.JSONObject;

import retrofit2.Call;


public class PasswordReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Volley.newRequestQueue(this);

        //send server email
        //server sends email to user
        //send server new password and email, token.



        Button send_button = (Button)findViewById(R.id.send_reset);
        final TextInputEditText email_Input = (TextInputEditText) findViewById(R.id.email);
        String email = String.valueOf(email_Input.getText()); //get Email from input
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get response from server about whether the password is valid
                Call<Void> c = Server.getService().resetPassword(new MeetingService.ResetPasswordData(email));
                c.enqueue(Server.mkCallback(
                        (call, response) -> {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                Toast.makeText(PasswordReset.this, response.body().toString() , Toast.LENGTH_LONG).show();
                            } else {
                                Server.parseUnsuccessful(response, MeetingService.RegistrationError.class, System.out::println, System.out::println);
                                startActivity(new Intent(PasswordReset.this, PasswordReset2.class));
                            }
                        },
                        (call, t) -> t.printStackTrace()
                ));
            }
        });




    }//onCreate





}
