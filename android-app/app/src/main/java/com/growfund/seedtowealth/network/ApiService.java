package com.growfund.seedtowealth.network;

import com.growfund.seedtowealth.model.Crop;
import com.growfund.seedtowealth.model.Farm;
import com.growfund.seedtowealth.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/users/sync")
    Call<User> syncUser(@Body User user);

    // Farm endpoints
    @POST("api/farms")
    Call<Farm> createFarm(@Body Map<String, String> request);

    @GET("api/farms/my-farm")
    Call<Farm> getMyFarm();

    @PUT("api/farms/{id}/savings")
    Call<Farm> updateSavings(@Path("id") Long farmId, @Body Map<String, Long> request);

    // Crop endpoints
    @POST("api/farms/{farmId}/crops")
    Call<Crop> plantCrop(@Path("farmId") Long farmId, @Body Map<String, Object> request);

    @GET("api/farms/{farmId}/crops")
    Call<List<Crop>> getCrops(@Path("farmId") Long farmId);

    @GET("api/crops/{id}")
    Call<Crop> getCrop(@Path("id") Long cropId);

    @PUT("api/crops/{id}/harvest")
    Call<Crop> harvestCrop(@Path("id") Long cropId);

    @DELETE("api/crops/{id}")
    Call<Void> deleteCrop(@Path("id") Long cropId);
}
