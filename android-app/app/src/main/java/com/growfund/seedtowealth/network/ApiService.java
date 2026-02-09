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
        @POST("users/sync")
        Call<User> syncUser(@Body User user);

        // Farm endpoints
        @POST("farms")
        Call<Farm> createFarm(@Body Map<String, String> request);

        @GET("farms/my-farm")
        Call<Farm> getMyFarm();

        @PUT("farms/{id}/savings")
        Call<Farm> updateSavings(@Path("id") Long farmId, @Body Map<String, Long> request);

        @PUT("farms/{id}/name")
        Call<Farm> updateFarmName(@Path("id") Long farmId, @Body Map<String, String> request);

        @POST("farms/expand")
        Call<Farm> expandFarm();

        // Crop endpoints
        @POST("farms/{farmId}/crops")
        Call<Crop> plantCrop(@Path("farmId") Long farmId, @Body Map<String, Object> request);

        @GET("farms/{farmId}/crops/estimate-cost")
        Call<Long> getPlantingCostEstimate(
                        @Path("farmId") Long farmId,
                        @Query("cropType") String cropType,
                        @Query("areaPlanted") Double areaPlanted);

        @GET("farms/{farmId}/crops")
        Call<List<Crop>> getCrops(@Path("farmId") Long farmId);

        @GET("crops/{id}")
        Call<Crop> getCrop(@Path("id") Long cropId);

        @PUT("crops/{id}/harvest")
        Call<Crop> harvestCrop(@Path("id") Long cropId);

        @DELETE("crops/{id}")
        Call<Void> deleteCrop(@Path("id") Long cropId);

        @GET("farms/{farmId}/crops/limits/{cropType}")
        Call<Map<String, Double>> getCropLimit(
                        @Path("farmId") Long farmId,
                        @Path("cropType") String cropType);

        // Leaderboard
        @GET("leaderboard")
        Call<List<com.growfund.seedtowealth.model.LeaderboardEntry>> getLeaderboard();

        // Investments
        @POST("investments")
        Call<com.growfund.seedtowealth.model.Investment> createInvestment(
                        @Body com.growfund.seedtowealth.model.Investment investment);

        @GET("investments/user/my-active-investments")
        Call<List<com.growfund.seedtowealth.model.Investment>> getMyActiveInvestments();

        // Market Trends
        @GET("market/trends")
        Call<java.util.Map<String, Double>> getMarketTrends();

        // Weather
        @GET("weather/current")
        Call<java.util.Map<String, Object>> getCurrentWeather();

        // Achievements
        @GET("achievements")
        Call<List<com.growfund.seedtowealth.model.Achievement>> getAchievements();

        // Quiz
        @GET("quizzes/daily")
        Call<com.growfund.seedtowealth.model.Quiz> getDailyQuiz();

        @POST("quizzes/{id}/submit")
        Call<Boolean> submitQuiz(@Path("id") Long quizId, @retrofit2.http.Query("optionIndex") Integer optionIndex);

        // Random Events
        @GET("events/active")
        Call<List<com.growfund.seedtowealth.model.RandomEvent>> getActiveEvents();

        @GET("events/history")
        Call<List<com.growfund.seedtowealth.model.RandomEvent>> getEventHistory();

        @POST("events/generate")
        Call<java.util.Map<String, Object>> generateEvent();

        @GET("events/active/crop/{cropType}")
        Call<List<com.growfund.seedtowealth.model.RandomEvent>> getActiveEventsForCrop(
                        @Path("cropType") String cropType);

        // Equipment
        @GET("equipment")
        Call<List<com.growfund.seedtowealth.model.Equipment>> getAllEquipment();

        @GET("equipment/farm/{farmId}")
        Call<List<com.growfund.seedtowealth.model.FarmEquipment>> getFarmEquipment(@Path("farmId") Long farmId);

        @POST("equipment/purchase")
        Call<java.util.Map<String, Object>> purchaseEquipment(@Query("farmId") Long farmId,
                        @Query("equipmentId") Long equipmentId);

        @GET("equipment/bonuses/{farmId}")
        Call<java.util.Map<String, Double>> getEquipmentBonuses(@Path("farmId") Long farmId);

        // Analytics
        @GET("analytics/dashboard/{farmId}")
        Call<com.growfund.seedtowealth.model.AnalyticsData> getFarmAnalytics(@Path("farmId") Long farmId);

        @GET("analytics/recommendations/{farmId}")
        Call<List<com.growfund.seedtowealth.model.Recommendation>> getRecommendations(@Path("farmId") Long farmId);

        @POST("feedback")
        Call<Void> submitFeedback(@Body java.util.Map<String, String> feedbackData);
}
