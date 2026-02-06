package com.growfund.seedtowealth.network;

import com.growfund.seedtowealth.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/users/sync")
    Call<User> syncUser(@Body User user);
}
