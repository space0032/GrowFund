package com.growfund.seedtowealth.network;

import com.growfund.seedtowealth.model.User;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ApiService {
    @POST("/api/users/sync")
    Call<User> syncUser(@Header("Authorization") String token);

    @GET("/api/users/me")
    Call<User> getCurrentUser(@Header("Authorization") String token);
}
