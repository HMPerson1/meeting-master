package com.example.meetingmasterclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.Upload;
import java.io.FileDescriptor;
import java.io.IOException;
import retrofit2.Call;

public class Registration extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    private Uri profilePictureUri;
    private boolean hasPicture;
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

        hasPicture = false;

        ((Button)findViewById(R.id.upload_profile_picture_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });
    }

    // methods for picture uploading

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
            hasPicture = true;

            new AsyncTask<Uri, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Uri... uri) {
                    try {
                        ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri[0], "r");
                        FileDescriptor fd = pfd.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fd);
                        pfd.close();
                        return image;
                    } catch(IOException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    if (result != null) {
                        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(result);
                    } else {
                        Toast.makeText(getApplicationContext(), "The picture has been loaded, but cannot be shown", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(profilePictureUri);
        }
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
       return true;
    }

    public boolean confirmInput(View v){
        boolean help = !validatePassword() | !validateEmail() | !validateFirstName() | !validateLastName()
                | !validateUsername() | validatePhoneNumber();
        System.out.println(help);
        return (!validatePassword() | !validateEmail() | !validateFirstName() | !validateLastName()
                | !validateUsername() | validatePhoneNumber());
    }

    public void sendRegistrationRequest(View v){
        if (!confirmInput(v)) return;

        //parse information to be sent to server for registration
        String username = textInputUsername.getEditText().getText().toString().trim();
        String email = textInputEmailAddress.getEditText().getText().toString().trim();
        String password1 = textInputPassword.getEditText().getText().toString().trim();
        String password2 = textInputConfirmPassword.getEditText().getText().toString().trim();
        String first_name = textInputFirstName.getEditText().getText().toString().trim();
        String last_name = textInputLastName.getEditText().getText().toString().trim();
        String phone_number = textInputPhoneNumber.getEditText().getText().toString().trim();


        Call<MeetingService.AuthToken> c = Server.getService().register(new MeetingService
                .RegistrationData(username,first_name,last_name,email,password1,password2,phone_number));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Server.authenticate(response.body().key);

                        if (hasPicture) {
                            Upload.uploadPictureToServer(getApplicationContext(), profilePictureUri);
                        }

                        Toast.makeText(Registration.this, "Registration Success", Toast.LENGTH_LONG).show();

                    } else {
                        String error = null;
                        Server.parseUnsuccessful(response, MeetingService.RegistrationError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
