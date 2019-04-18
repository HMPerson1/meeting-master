package com.example.meetingmasterclient;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;

public class PasswordReset2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configureConfirmButton();
    }//OnCreate

    private void configureConfirmButton(){
        final TextInputEditText npwd1Text = (TextInputEditText) findViewById(R.id.new_password);

        final TextInputEditText npwd2Text = (TextInputEditText) findViewById(R.id.confirm_pass);

        final TextInputEditText uuidText = (TextInputEditText) findViewById(R.id.uuid);

        final TextInputEditText tknText = (TextInputEditText) findViewById(R.id.token);


        Button confirm_button = (Button)findViewById(R.id.confirm);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String npwd1 = String.valueOf(npwd1Text.getText()); //get Email from input
                String npwd2 = String.valueOf(npwd1Text.getText()); //get Email from input
                String uuid = String.valueOf(uuidText.getText()); //get Email from input
                String tkn = String.valueOf(tknText.getText()); //get Email from input

                Call<Void> c = Server.getService().confirmResetPassword(new MeetingService.ConfirmResetPasswordData(npwd1,npwd2,uuid,tkn));
                c.enqueue(Server.mkCallback(
                        (call, response) -> {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                Toast.makeText(PasswordReset2.this, "Password has been Reset" , Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Server.parseUnsuccessful(response, MeetingService.RegistrationError.class, System.out::println, System.out::println);

                            }
                        },
                        (call, t) -> t.printStackTrace()
                ));
            }
        });
    }
}
