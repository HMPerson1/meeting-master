package com.example.meetingmasterclient.server;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MeetingService {
    @POST("/register/")
    Call<AuthToken> register(@Body RegistrationData data);

    @POST("/rest-auth/login/")
    Call<AuthToken> login(@Body LoginData data);

    @GET("/users/")
    Call<List<UserProfile>> users(@Query("search") String search);

    class RegistrationData {
        public String username;
        public String first_name;
        public String last_name;
        public String email;
        public String password1;
        public String password2;
        public String phone_number;

        public RegistrationData(String username, String first_name, String last_name, String email, String password1, String password2, String phone_number) {
            this.username = username;
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.password1 = password1;
            this.password2 = password2;
            this.phone_number = phone_number;
        }
    }

    class RegistrationError {
        public String[] username;
        public String[] first_name;
        public String[] last_name;
        public String[] email;
        public String[] password1;
        public String[] password2;
        public String[] phone_number;
        public String[] non_field_errors;

        @Override
        @NonNull
        public String toString() {
            return "RegistrationError{" +
                    "username=" + Arrays.toString(username) +
                    ", first_name=" + Arrays.toString(first_name) +
                    ", last_name=" + Arrays.toString(last_name) +
                    ", email=" + Arrays.toString(email) +
                    ", password1=" + Arrays.toString(password1) +
                    ", password2=" + Arrays.toString(password2) +
                    ", phone_number=" + Arrays.toString(phone_number) +
                    ", non_field_errors=" + Arrays.toString(non_field_errors) +
                    '}';
        }
    }

    class UserProfile {
        public int pk;
        public String username;
        public String first_name;
        public String last_name;
        public String email;
        public String phone_number;
        public String profile_picture;

        @Override
        @NonNull
        public String toString() {
            return "UserProfile{" +
                    "pk=" + pk +
                    ", username='" + username + '\'' +
                    ", first_name='" + first_name + '\'' +
                    ", last_name='" + last_name + '\'' +
                    ", email='" + email + '\'' +
                    ", phone_number='" + phone_number + '\'' +
                    ", profile_picture='" + profile_picture + '\'' +
                    '}';
        }


    }

    class AuthToken {
        public String key;
    }

    class LoginData {
        public String username;
        public String password;

        public LoginData(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
