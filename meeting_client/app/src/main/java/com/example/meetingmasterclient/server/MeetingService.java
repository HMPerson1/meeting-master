package com.example.meetingmasterclient.server;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface MeetingService {
    /**
     * may fail with {@link RegistrationError}
     */
    @POST("/register/")
    Call<AuthToken> register(@Body RegistrationData data);

    /**
     * may fail with "Unable to log in with provided credentials."
     */
    @POST("/rest-auth/login/")
    Call<AuthToken> login(@Body LoginData data);

    /**
     * needs authentication <br>
     * never fails (if authenticated)
     */
    @POST("/rest-auth/logout/")
    Call<Void> logout();

    /**
     * needs authentication <br>
     * never fails (if authenticated)
     */
    @GET("/rest-auth/user/")
    Call<UserProfile> getCurrentUser();

    /**
     * needs authentication <br>
     * data.pk is ignored <br>
     * may fail with {@link UserProfileError}
     */
    @PUT("/rest-auth/user/")
    Call<UserProfile> putCurrentUser(@Body UserProfile data);

    /**
     * may fail with {@link ResetPasswordError}
     */
    @POST("/rest-auth/password/reset/")
    Call<Void> resetPassword(@Body ResetPasswordData data);

    /**
     * may fail with {@link ConfirmResetPasswordError}
     */
    @POST("/rest-auth/password/reset/confirm/")
    Call<Void> confirmResetPassword(@Body ConfirmResetPasswordData data);

    /**
     * needs authentication <br>
     * never fails (if authenticated)
     */
    @GET("/users/")
    Call<List<UserProfile>> users(@Query("search") String search);

    @PATCH("/events/{id}/")
    Call<Void> users(@Body EventsData data);


    /* ******************** *
     * Dumb data containers *
     * ******************** */

    class AuthToken {
        public String key;
    }

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


        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

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
        public int getPk() {
            return pk;
        }

        public String getUsername() {
            return username;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public String getProfile_picture() {
            return profile_picture;
        }
    }

    class UserProfileError {
        public String[] pk;
        public String[] username;
        public String[] first_name;
        public String[] last_name;
        public String[] email;
        public String[] phone_number;
        public String[] profile_picture;
        public String[] non_field_errors;

        @Override
        @NonNull
        public String toString() {
            return "UserProfileError{" +
                    "pk=" + Arrays.toString(pk) +
                    ", username=" + Arrays.toString(username) +
                    ", first_name=" + Arrays.toString(first_name) +
                    ", last_name=" + Arrays.toString(last_name) +
                    ", email=" + Arrays.toString(email) +
                    ", phone_number=" + Arrays.toString(phone_number) +
                    ", profile_picture=" + Arrays.toString(profile_picture) +
                    ", non_field_errors=" + Arrays.toString(non_field_errors) +
                    '}';
        }
    }

    class LoginData {
        public String username;
        public String password;

        public LoginData(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    class ResetPasswordData {
        public String email;

        public ResetPasswordData(String email) {
            this.email = email;
        }
    }

    class ResetPasswordError {
        public String[] email;
        public String[] non_field_errors;

        @Override
        @NonNull
        public String toString() {
            return "ResetPasswordError{" +
                    "email=" + Arrays.toString(email) +
                    ", non_field_errors=" + Arrays.toString(non_field_errors) +
                    '}';
        }
    }

    class ConfirmResetPasswordData {
        public String new_password1;
        public String new_password2;
        public String uid;
        public String token;

        public ConfirmResetPasswordData(String new_password1, String new_password2, String uid, String token) {
            this.new_password1 = new_password1;
            this.new_password2 = new_password2;
            this.uid = uid;
            this.token = token;
        }
    }

    class ConfirmResetPasswordError {
        public String[] new_password1;
        public String[] new_password2;
        public String[] uid;
        public String[] token;
        public String[] non_field_errors;

        @Override
        @NonNull
        public String toString() {
            return "ConfirmResetPasswordError{" +
                    "new_password1=" + Arrays.toString(new_password1) +
                    ", new_password2=" + Arrays.toString(new_password2) +
                    ", uid=" + Arrays.toString(uid) +
                    ", token=" + Arrays.toString(token) +
                    ", non_field_errors=" + Arrays.toString(non_field_errors) +
                    '}';
        }
    }//Confirm Password Reset Error

    class EventsData{
        public String event_name;
        public String event_date;
        public String duration;
        public String notes;
    }//EventsData
}
