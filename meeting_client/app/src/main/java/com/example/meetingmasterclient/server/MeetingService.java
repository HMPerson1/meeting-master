package com.example.meetingmasterclient.server;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;

import retrofit2.http.Multipart;

import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    @Multipart
    @PUT("/current_user/profile_picture")
    Call<ResponseBody> uploadProfilePicture(@Part MultipartBody.Part profile_picture);

    /*
    @Multipart
    @PUT("/rest-auth/user/")
    Call<UserProfile> putCurrentUser(
        @Part("username") RequestBody username,
        @Part("first_name") RequestBody first_name,
        @Part("last_name") RequestBody last_name,
        @Part("email") RequestBody email,
        @Part("phone_number") RequestBody phone_number,
        @Part MultipartBody.Part profile_picture
    );
    */

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

    @GET
    Call<UserProfile> getUser(@Url String url);

    @GET("/locations/{id}")
    Call<LocationData> getLocationDetails(@Path("id") String id);

    @POST("/locations/")
    Call<LocationData> newLocation(@Body LocationData data);

    @GET("/events/{id}")
    Call<EventsData> getEventfromId(@Path("id") String id);

    @PUT("/events/{id}")
    Call<EventsData> updateEvent(@Body EventCreationData data,@Path("id") String id);

    /*
    @Multipart
    @POST("/events/")
    Call<EventData> createEvent(
        @Part("event_name") RequestBody event_name,
        @Part("event_date") RequestBody event_date,
        @Part("event_time") RequestBody event_time,
        @Part("event_duration") RequestBody event_duration,
        @Part("event_location") RequestBody event_location,
        @Part("notes") RequestBody notes,
        @Part MultipartBody.Part file
    );
*/
    @POST("/events/new_event")
    Call<EventsData> createEvent(@Body EventCreationData data);

    @POST("/invitations/")
    Call<InvitationData> postInvitations(@Body InvitationData data);
    @GET("/invitations/{user_id}/")
    Call<List<InvitationData>> getUserInvitations(@Path("user_id") String user_id);

    @GET("/invitations/{event_id}/{user_id}/")
    Call<InvitationData> getUserInvitationStatus(@Path("event_id") String event_id,
                                                 @Path("user_id") String user_id);

    @PUT("/invitations/{event_id}/{user_id}/update_status")
    Call<Void> setInvitationStatus(@Path("event_id") String event_id,
                                   @Path("user_id") String user_id,
                                   @Query("status") int status);     //TODO this probs needs fixing, event_id to string

    @GET("/invitations/user-invitations")
    Call<List<InvitationData>> getUsersInvitations();

    @GET
    Call<EventData> getEvent(@Url String url);

    @GET
    Call<EventsData> getEvents(@Url String url);

    /**
     * needs authentication <br>
     * never fails (if authenticated)
     */
    @PUT("/current_user/firebase_reg_token")
    Call<Void> putFirebaseRegToken(@Body FirebaseRegTokenData data);

    /**
     * TODO someone check this, not sure if correct
     */
    @DELETE("/events/{id}/")
    Call<Void> deleteEvent(@Url String url);

    /**
     * needs authentication <br>
     * never fails (if authenticated)
     */
    @GET("/current_user/ical_url")
    Call<IcalUrlData> getIcalUrl();

    @POST("/TODO/") // TODO
    Call<Void> putCurrentLocation(@Body CurrentLocationData data);

    @GET("/suggestions/event-suggestions/{event_id}")
    Call <List<LocationSuggestionsData>> getSuggestedLocations(@Path("event_id")  String event_id);

    @POST("/suggestions/")
    Call<LocationSuggestionsData> makeSuggestion(@Body LocationSuggestionsData data);

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

        public void setUsername(String username){
            this.username = username;
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

        public UserProfile(int pk, String username, String first_name, String last_name, String email, String phone_number, String profile_picture) {
            this.pk = pk;
            this.username = username;
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
            this.phone_number = phone_number;
            this.profile_picture = profile_picture;
        }

        public UserProfile(){
            this.pk = -1;
            this.username = null;
            this.first_name = null;
            this.last_name = null;
            this.email = null;
            this.phone_number = null;
            this.profile_picture = null;
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

    }
    class EventsData {
        public int pk;
        public String event_name;
        public String event_date;
        public String event_time;
        public String event_duration;
        public LocationData event_location;
        public String notes;
        public String file_attachment;

        public int getPk() {
            return pk;
        }

        public String getEvent_name() {
            return event_name;
        }

        public String getEvent_date() {
            return event_date;
        }

        public String getEvent_time() {
            return event_time;
        }

        public String getEvent_duration() {
            return event_duration;
        }

        public LocationData getEvent_location() {
            return event_location;
        }

        public String getNotes() {
            return notes;
        }

        public String getFile_attachment() {
            return file_attachment;
        }
    }

    class EventData {
        public int id;
        public int event_admin_id;
        public String event_name;
        public String event_date;
        public String event_time;
        public String event_duration;
        public String file_attachment;
        public String notes;
        public int event_location;

        public EventData(int id, int event_admin, String event_name, String event_date, String event_time, String event_duration, String file_attachment, String notes, int event_location) {
            this.id = id;
            this.event_admin_id = event_admin;
            this.event_name = event_name;
            this.event_date = event_date;
            this.event_time = event_time;
            this.event_duration = event_duration;
            this.file_attachment = file_attachment;
            this.notes = notes;
            this.event_location = event_location;
        }

        public int getId() {
            return id;
        }

        public int getEvent_admin() {
            return event_admin_id;
        }

        public String getEvent_name() {
            return event_name;
        }

        public String getEvent_date() {
            return event_date;
        }

        public String getEvent_time() {
            return event_time;
        }

        public String getEvent_duration() {
            return event_duration;
        }

        public String getFile_attachment() {
            return file_attachment;
        }

        public String getNotes() {
            return notes;
        }

        public int getEvent_location() {
            return event_location;
        }
    }

    class EventCreationData{
        public String event_name;
        public String event_date;
        public String event_time;
        public String event_duration;
        public int event_location;
        public String notes;
        public File file_attachment;

        public EventCreationData(String event_name, String event_date, String event_time, String event_duration,
                                 int event_location, String notes, File file_attachment){
            this.event_name = event_name;
            this.event_date = event_date;
            this.event_time = event_time;
            this.event_duration = event_duration;
            this.event_location = event_location;
            this.notes = notes;
            this.file_attachment = file_attachment;
        }

        public EventCreationData(String event_name, int event_location){
            this.event_name = event_name;
            this.event_date = null;
            this.event_time = null;
            this.event_duration = null;
            this.event_location = event_location;
            this.notes = null;
            this.file_attachment = null;
        }

        public String getEvent_name() {
            return event_name;
        }
    }

    class EventDataError{
        public String[] id;
        public String[] event_admin;
        public String[] event_name;
        public String[] event_date;
        public String[] event_time;
        public String[] event_duration;
        public String[] file_attachment;
        public String[] notes;
        public String[] event_location;

        @Override
        @NonNull
        public String toString() {
            return "EventDataError{" +
                    "id=" + Arrays.toString(id) +
                    ", event_admin=" + Arrays.toString(event_admin) +
                    ", event_name=" + Arrays.toString(event_name) +
                    ", event_date=" + Arrays.toString(event_date) +
                    ", event_time=" + Arrays.toString(event_time) +
                    ", event_duration=" + Arrays.toString(event_duration) +
                    ", file_attachment=" + Arrays.toString(file_attachment) +
                    ", notes=" + Arrays.toString(notes) +
                    ", event_location" + Arrays.toString(event_location) +
                    '}';
        }
    }

    class InvitationData {
        public String user_id;
        public int event_id;
        public int status;
        public boolean edit_permission;


        public InvitationData(String user_id, int event_id, int status, boolean edit_permission) {

            this.user_id = user_id;
            this.event_id = event_id;
            this.status = status;
            this.edit_permission = edit_permission;
        }

        public String getUser_id() {
            return user_id;
        }

        public int getEvent_id() {
            return event_id;
        }

        public int getStatus() {
            return status;
        }
    }


    class LocationData {
        int pk;
        String street_address;
        String city;
        String state;
        int number_of_uses;

        public LocationData(String street_address, String city, String state) {
            this.street_address = street_address;
            this.city = city;
            this.state = state;
        }

        public int getPk() {
            return pk;
        }

        public String getStreet_address() {
            return street_address;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public int getNumber_of_uses() {
            return number_of_uses;
        }
    }

    class FirebaseRegTokenData {
        public String firebase_reg_token;

        public FirebaseRegTokenData(String firebase_reg_token) {
            this.firebase_reg_token = firebase_reg_token;
        }

    }

    class IcalUrlData {
        public String ical_url;
    }

    class CurrentLocationData {
        double lat;
        double lon;

        public CurrentLocationData(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    class LocationSuggestionsData {
        int event_id;
        int location_id;

        public LocationSuggestionsData(int event_id, int location_id) {
            this.event_id = event_id;
            this.location_id = location_id;
        }

        public int getEvent_id() {
            return event_id;
        }

        public int getLocation_id() {
            return location_id;
        }
    }
}

