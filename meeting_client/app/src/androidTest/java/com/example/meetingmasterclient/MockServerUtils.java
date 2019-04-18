package com.example.meetingmasterclient;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class MockServerUtils {
    public static void setUpMockService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Server.BASE_URL)
                .build();

        NetworkBehavior behavior = NetworkBehavior.create();
        MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build();

        BehaviorDelegate<MeetingService> delegate = mockRetrofit.create(MeetingService.class);
        MockService service = new MockService(delegate);
        Server.setMockService(service);
    }
}
