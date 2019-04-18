package com.example.meetingmasterclient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.LocalPicture;
import com.example.meetingmasterclient.utils.Upload;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEdition extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 10;
    private static final int FILE_PERMISSION = 20;
    private TextInputEditText textInputFirstName;
    private TextInputEditText textInputLastName;
    private TextInputEditText textInputUsername;
    private TextInputEditText textInputEmailAddress;
    private TextInputEditText textInputPhoneNumber;
    private Button confirmButton;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNum;
    private int userID;
    private String profPic;
    private boolean pictureChanged;
    private Uri profilePictureUri;

    MeetingService.UserProfile currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edition);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize user fields
        textInputFirstName = findViewById(R.id.text_input_edit_first_name);
        textInputLastName = findViewById(R.id.text_input_edit_last_name);
        textInputUsername = findViewById(R.id.text_input_edit_username);
        textInputEmailAddress = findViewById(R.id.text_input_edit_email);
        textInputPhoneNumber = findViewById(R.id.text_input_edit_phone_number);

        pictureChanged = false;

        Call<MeetingService.UserProfile> c = Server.getService().getCurrentUser();
        c.enqueue(new Callback<MeetingService.UserProfile>() {
            @Override
            public void onResponse(Call<MeetingService.UserProfile> call, Response<MeetingService.UserProfile> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(ProfileEdition.this, "Error: " + response.toString(),
                            Toast.LENGTH_LONG).show();
                    Log.d("Current user error", response.toString());
                    return;
                } else {
                    Toast.makeText(ProfileEdition.this, "Current user success",
                            Toast.LENGTH_LONG).show();
                    Log.d("Current user success", response.toString());
                }

                //autofill
                currentUser = response.body();
                userID = currentUser.getPk();
                firstName = currentUser.getFirst_name();
                lastName = currentUser.getLast_name();
                username= currentUser.getUsername();
                email = currentUser.getEmail();
                phoneNum = currentUser.getPhone_number();
                profPic = currentUser.getProfile_picture();

                textInputFirstName.setText(firstName);
                textInputLastName.setText(lastName);
                textInputUsername.setText(username);
                textInputEmailAddress.setText(email);
                textInputPhoneNumber.setText(phoneNum);
                //TODO autopopulate profPic
            }

            @Override
            public void onFailure(Call<MeetingService.UserProfile> call, Throwable t) {
                Toast.makeText(ProfileEdition.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        ((Button)findViewById(R.id.upload_profile_picture_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Upload.checkFilePermissions(getApplicationContext())) {
                    uploadPicture();
                } else {
                    ActivityCompat.requestPermissions(ProfileEdition.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            FILE_PERMISSION);
                }
            }
        });

        confirmButton = findViewById(R.id.confirm_profile_changes_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendProfileEditionRequest(view);
            }
        });
    }

    public void sendProfileEditionRequest(View view){
        username = textInputUsername.getText().toString().trim();
        firstName = textInputFirstName.getText().toString().trim();
        lastName = textInputLastName.getText().toString().trim();
        email = textInputEmailAddress.getText().toString().trim();
        phoneNum = textInputPhoneNumber.getText().toString().trim();
        //TODO deal with profile pic here too

        Call<MeetingService.UserProfile> c = Server.getService().putCurrentUser(new MeetingService.UserProfile(
                userID, username, firstName, lastName, email, phoneNum, profPic));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    Toast.makeText(ProfileEdition.this, response.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Profile edition response", response.toString());
                    if (response.isSuccessful()){
                        assert response.body() != null;
                        if (pictureChanged) {
                            Upload.uploadPictureToServer(getApplicationContext(), profilePictureUri);
                        }
                        Toast.makeText(ProfileEdition.this, "Success", Toast.LENGTH_LONG).show();
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.UserProfileError.class, userProfileError -> {
                            Toast.makeText(ProfileEdition.this, userProfileError.toString(), Toast.LENGTH_LONG).show();
                            Log.d("Error", userProfileError.toString());
                            System.out.println(userProfileError.toString());
                        }, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void uploadPicture() {
        Intent fileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        fileIntent.setType("image/*");

        startActivityForResult(fileIntent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            profilePictureUri = resultData.getData();
            pictureChanged = true;

            new LocalPicture(this).execute(profilePictureUri);
        }
    }
}
