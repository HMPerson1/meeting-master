package com.example.meetingmasterclient.server;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Server {
    private static final String TAG = "Server";

    private static final String BASE_URL = "http:/0.0.0.0:8000";

    private static Server instance;
    private @NonNull
    Retrofit retrofit;
    private @NonNull
    MeetingService service;
    private @Nullable
    String authToken = null;

    private Server(@NonNull Retrofit retrofit, @NonNull MeetingService service) {
        this.retrofit = retrofit;
        this.service = service;
    }

    private static Server getInstance() {
        if (instance == null) {
            // Add authorization header when we have an authToken
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        if (instance != null && instance.authToken != null) {
                            Log.d(TAG, "authorizing request");
                            return chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Token " + instance.authToken).build());
                        } else {
                            return chain.proceed(chain.request());
                        }
                    })
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            MeetingService service = retrofit.create(MeetingService.class);
            instance = new Server(retrofit, service);
        }
        return instance;
    }

    public static MeetingService getService() {
        return getInstance().service;
    }

    public static void authenticate(String authToken) {
        getInstance().authToken = authToken;
        Log.d(TAG, "authenticated");
        // tell the server our firebase registration token
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            getService().putFirebaseRegToken(new MeetingService.FirebaseRegTokenData(task.getResult().getToken()))
                    .enqueue(mkCallback(
                            (call, response) -> {
                            }, (call, t) -> t.printStackTrace()));
        });
    }

    public static <T> void parseUnsuccessful(@NonNull Response<?> response, Class<T> errorResponseClass, Consumer<T> badRequest, IntConsumer otherError) {
        if (response.isSuccessful()) throw new IllegalArgumentException();
        if (instance == null) throw new IllegalStateException();
        if (response.code() == 400) {
            try {
                assert response.errorBody() != null;
                badRequest.accept(instance.retrofit.<T>responseBodyConverter(errorResponseClass, new Annotation[0]).convert(response.errorBody()));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        otherError.accept(response.code());
    }

    public static <T> Callback<T> mkCallback(@NonNull BiConsumer<Call<T>, Response<T>> onResponse, @NonNull BiConsumer<Call<T>, Throwable> onFailure) {
        return new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                onResponse.accept(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                onFailure.accept(call, t);
            }
        };
    }

}
