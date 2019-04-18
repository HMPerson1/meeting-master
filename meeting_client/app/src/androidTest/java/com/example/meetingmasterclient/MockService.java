package com.example.meetingmasterclient;

import com.example.meetingmasterclient.server.MeetingService;

import androidx.test.espresso.idling.CountingIdlingResource;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockService implements MeetingService {
    private final BehaviorDelegate<MeetingService> delegate;

    public MockService(BehaviorDelegate<MeetingService> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<AuthToken> register(RegistrationData data) {
        return null;
    }

    @Override
    public Call<AuthToken> login(LoginData data) {
        return null;
    }

    @Override
    public Call<Void> logout() {
        return null;
    }

    @Override
    public Call<UserProfile> getCurrentUser() {
        return null;
    }

    @Override
    public Call<UserProfile> putCurrentUser(UserProfile data) {
        return null;
    }

    @Override
    public Call<ResponseBody> uploadProfilePicture(MultipartBody.Part profile_picture) {
        return null;
    }

    @Override
    public Call<Void> resetPassword(ResetPasswordData data) {
        return null;
    }

    @Override
    public Call<Void> confirmResetPassword(ConfirmResetPasswordData data) {
        return null;
    }

    @Override
    public Call<List<UserProfile>> users(String search) {
        return null;
    }

    @Override
    public Call<UserProfile> getUser(String url) {
        return null;
    }

    @Override
    public Call<LocationData> getLocationDetails(String id) {
        return null;
    }

    @Override
    public Call<LocationData> newLocation(LocationData data) {
        return null;
    }

    @Override
    public Call<EventsData> getEventfromId(int id) {
        EventsData response = new EventsData();
        response.pk = id;
        response.event_name = "Test";
        response.event_date = "2019-04-19";
        response.event_time = "10:00";
        response.event_duration = "02:00";
        response.event_location = new LocationData(
                "Lawson Computer Science Building", "West Lafayette", "Indiana");
        response.notes = "";
        response.file_attachment = null;
        response.current_overall_state = 2;

        return delegate.returningResponse(response).getEventfromId(id);
    }

    @Override
    public Call<EventsData> updateEvent(EventCreationData data, String id) {
        return null;
    }

    @Override
    public Call<EventsData> createEvent(EventCreationData data) {
        return null;
    }

    @Override
    public Call<ResponseBody> uploadFile(String id, MultipartBody.Part file) {
        return null;
    }

    @Override
    public Call<ResponseBody> downloadFile(String url) {
        return null;
    }

    @Override
    public Call<InvitationData> postInvitations(InvitationData data) {
        return null;
    }

    @Override
    public Call<List<InvitationData>> getUserInvitations(String user_id) {
        List<InvitationData> response = new ArrayList<>();
        response.add(
                new InvitationData(user_id, 1, 2, false)
        );
        return delegate.returningResponse(response).getUserInvitations(user_id);
    }

    @Override
    public Call<InvitationData> getUserInvitationStatus(String event_id, String user_id) {
        return null;
    }

    @Override
    public Call<InvitationData> setInvitationStatus(String event_id, String user_id, int status) {
        return null;
    }

    @Override
    public Call<List<InvitationData>> getUsersInvitations() {
        return null;
    }

    @Override
    public Call<EventsData> getEvents(String url) {
        return null;
    }

    @Override
    public Call<Void> putFirebaseRegToken(FirebaseRegTokenData data) {
        return null;
    }

    @Override
    public Call<Void> deleteEvent(String url) {
        return null;
    }

    @Override
    public Call<IcalUrlData> getIcalUrl() {
        return null;
    }

    @Override
    public Call<Void> putCurrentLocation(CurrentLocationData data) {
        return null;
    }

    @Override
    public Call<List<CurrentLocationData>> getCurrentLocations() {
        return null;
    }

    @Override
    public Call<List<LocationSuggestionsData>> getSuggestedLocations(String event_id) {
        return null;
    }

    @Override
    public Call<LocationSuggestionsData> makeSuggestion(LocationSuggestionsData data) {
        return null;
    }

    @Override
    public Call<ActiveEventsData> getUserStatus() {
        ActiveEventsData response = new ActiveEventsData(1, 2);
        return delegate.returningResponse(response).getUserStatus();
    }

    @Override
    public Call<ActiveEventsData> putUserStatus(ActiveEventsData data) {
        return null;
    }

    @Override
    public Call<Void> deleteUserStatus() {
        return null;
    }
}
