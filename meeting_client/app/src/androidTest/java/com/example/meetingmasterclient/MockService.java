package com.example.meetingmasterclient;

import com.example.meetingmasterclient.server.MeetingService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockService implements MeetingService {
    int event_id;
    String event_name = "Test";
    String event_date = "2019-04-19";
    String event_time = "10:00";
    String event_duration = "02:00";

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
        LocationData response;
        if (id.equals("1")){
            response = new LocationData("abc","easy","state");
            response.setPk(Integer.valueOf(id));
            response.setNumber_of_uses(1);
        }else if (id.equals("3")){
            response = new LocationData("test3","testCity3","state");
            response.setPk(Integer.valueOf(id));
            response.setNumber_of_uses(1);
        }else{
            response = new LocationData("add","city","state");
            response.setPk(Integer.valueOf(id));
            response.setNumber_of_uses(1);
        }


        return delegate.returningResponse(response).getLocationDetails(id);
    }

    @Override
    public Call<LocationData> newLocation(LocationData data) {
        data.setPk(1);
        return delegate.returningResponse(data).newLocation(data);
    }

    @Override
    public Call<EventsData> getEventfromId(int id) {
        EventsData response = new EventsData();
        event_id = id;
        response.pk = id;
        response.event_name = event_name;
        response.event_date = event_date;
        response.event_time = event_time;
        response.event_duration = event_duration;
        response.event_location = new LocationData(
                "Lawson Computer Science Building", "West Lafayette", "Indiana");
        response.notes = "";
        response.file_attachment = null;

        if (id == 1) {
            response.current_overall_state = 2;
        } else {
            response.current_overall_state = 0;
        }

        return delegate.returningResponse(response).getEventfromId(id);
    }

    @Override
    public Call<EventsData> updateEvent(EventCreationData data, String id) {
        event_name =data.getEvent_name();
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
        response.add(
                new InvitationData(user_id, 2, 2, false)
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
        List<InvitationData> response = new LinkedList<>();
        if (event_id != 0) {
            response.add(new InvitationData("1", event_id, 2, false));
        } else {
            response.add(new InvitationData("1", 1, 2, false));
        }

        return delegate.returningResponse(response).getUsersInvitations();
    }

    @Override
    public Call<List<InvitationData>> getEventInvitations(String event_id) {
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
    public Call<Void> deleteEvent(int id) {
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
    public Call<List<AttendeeLocationData>> getCurrentLocations(String event_id) {
        List<AttendeeLocationData> response = new LinkedList<>();
        if (event_id.equals("1")) {
            response.add(
                    new AttendeeLocationData("1", "Daniel Sanchez", -86.9105, 40.4263)
            );
            response.add(
                    new AttendeeLocationData("2", "Aaron Lynn", -86.919635, 40.432126)
            );
        } else if (event_id.equals("2")) {
            response.add(
                    new AttendeeLocationData("3", "Ariya Lau", -86.911371, 40.422599)
            );
            response.add(
                    new AttendeeLocationData("4", "Michael Zhang", -86.924942, 40.425822)
            );
        } else if (event_id.equals("3")) {
            response.add(
                    new AttendeeLocationData("5", "E.J. Wennerberg", -86.915727, 40.426176)
            );
            response.add(
                    new AttendeeLocationData("1", "Daniel Sanchez", -86.9105, 40.4263)
            );
        } else {
            response.add(
                    new AttendeeLocationData("2", "Aaron Lynn", -86.919635, 40.432126)
            );
            response.add(
                    new AttendeeLocationData("5", "E.J. Wennerberg", -86.915727, 40.426176)
            );
        }
        return delegate.returningResponse(response).getCurrentLocations(event_id);
    }

    @Override
    public Call<List<LocationSuggestionsData>> getSuggestedLocations(String event_id) {
        List<LocationSuggestionsData> response=new ArrayList<>();
        if (event_id.equals("1")){
            LocationSuggestionsData location = new LocationSuggestionsData(Integer.valueOf(event_id),1);
            response.add(location);
        }else if (event_id.equals("3")){
            LocationSuggestionsData location = new LocationSuggestionsData(Integer.valueOf(event_id),3);
            response.add(location);
        }


        return delegate.returningResponse(response).getSuggestedLocations(event_id);
    }

    @Override
    public Call<LocationSuggestionsData> makeSuggestion(LocationSuggestionsData data) {
        LocationSuggestionsData response = new LocationSuggestionsData(2, 1);
        return delegate.returningResponse(response).makeSuggestion(data);
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

    @Override
    public Call<Destination> getDestination(String url) {
        return null;
    }

    @Override
    public Call<UserProfile> getUserByID(int id){
        return null;
    }
}
