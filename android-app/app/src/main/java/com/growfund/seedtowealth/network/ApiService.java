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
import retrofit2.http.Query;

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

    @PUT("api/farms/{id}/name")
    Call<Farm> updateFarmName(@Path("id") Long farmId, @Body Map<String, String> request);

    @POST("api/farms/expand")
    Call<Farm> expandFarm();

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

    // Leaderboard
    @GET("api/leaderboard")
    Call<List<com.growfund.seedtowealth.model.LeaderboardEntry>> getLeaderboard();

    // Investments
    @POST("investments")
    Call<com.growfund.seedtowealth.model.Investment> createInvestment(
            @Body com.growfund.seedtowealth.model.Investment investment);

    @GET("investments/user/my-active-investments")
    Call<List<com.growfund.seedtowealth.model.Investment>> getMyActiveInvestments();

    // Market Trends
    @GET("api/market/trends")
    Call<java.util.Map<String, Double>> getMarketTrends();

    // Weather
    @GET("api/weather/current")
    Call<java.util.Map<String, Object>> getCurrentWeather();

    // Achievements
    @GET("api/achievements")
    Call<List<com.growfund.seedtowealth.model.Achievement>> getAchievements();

    // Quiz
    @GET("api/quizzes/daily")
    Call<com.growfund.seedtowealth.model.Quiz> getDailyQuiz();

    @POST("api/quizzes/{id}/submit")
    Call<Boolean> submitQuiz(@Path("id") Long quizId, @retrofit2.http.Query("optionIndex") Integer optionIndex);

    // Random Events
    @GET("api/events/active")
    Call<List<com.growfund.seedtowealth.model.RandomEvent>> getActiveEvents();

    @GET("api/events/history")
    Call<List<com.growfund.seedtowealth.model.RandomEvent>> getEventHistory();

    @POST("api/events/generate")
    Call<java.util.Map<String, Object>> generateEvent();

    @GET("api/events/active/crop/{cropType}")
    Call<List<com.growfund.seedtowealth.model.RandomEvent>> getActiveEventsForCrop(@Path("cropType") String cropType);

    // Equipment
    @GET("api/equipment")
    Call<List<com.growfund.seedtowealth.model.Equipment>> getAllEquipment();

    @GET("api/equipment/farm/{farmId}")
    Call<List<com.growfund.seedtowealth.model.FarmEquipment>> getFarmEquipment(@Path("farmId") Long farmId);

    @POST("api/equipment/purchase")
    Call<java.util.Map<String, Object>> purchaseEquipment(@Query("farmId") Long farmId,
            @Query("equipmentId") Long equipmentId);

    @GET("api/equipment/bonuses/{farmId}")
    Call<java.util.Map<String, Double>> getEquipmentBonuses(@Path("farmId") Long farmId);
}
