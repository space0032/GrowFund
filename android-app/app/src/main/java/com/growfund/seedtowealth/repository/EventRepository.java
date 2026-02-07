package com.growfund.seedtowealth.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.growfund.seedtowealth.database.AppDatabase;
import com.growfund.seedtowealth.database.RandomEventDao;
import com.growfund.seedtowealth.model.RandomEvent;
import com.growfund.seedtowealth.network.ApiClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {

    private final RandomEventDao eventDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public EventRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        eventDao = db.randomEventDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Get active events from local database (LiveData).
     */
    public LiveData<List<RandomEvent>> getActiveEventsLiveData() {
        return eventDao.getActiveEventsLiveData();
    }

    /**
     * Get active events from local database (synchronous).
     */
    public void getActiveEvents(RepositoryCallback<List<RandomEvent>> callback) {
        executorService.execute(() -> {
            List<RandomEvent> events = eventDao.getActiveEvents();
            mainHandler.post(() -> callback.onSuccess(events));
        });
    }

    /**
     * Fetch active events from server and update local database.
     */
    public void fetchActiveEventsFromServer(RepositoryCallback<List<RandomEvent>> callback) {
        ApiClient.getApiService().getActiveEvents().enqueue(new Callback<List<RandomEvent>>() {
            @Override
            public void onResponse(Call<List<RandomEvent>> call, Response<List<RandomEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RandomEvent> events = response.body();

                    // Save to local database
                    executorService.execute(() -> {
                        eventDao.insertAll(events);
                        mainHandler.post(() -> callback.onSuccess(events));
                    });
                } else {
                    mainHandler.post(() -> callback.onError("Failed to fetch events"));
                }
            }

            @Override
            public void onFailure(Call<List<RandomEvent>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(t.getMessage()));
            }
        });
    }

    /**
     * Trigger event generation on server.
     */
    public void generateEvent(RepositoryCallback<Boolean> callback) {
        ApiClient.getApiService().generateEvent().enqueue(new Callback<java.util.Map<String, Object>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, Object>> call,
                    Response<java.util.Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean generated = (Boolean) response.body().get("generated");

                    // Refresh events from server
                    if (Boolean.TRUE.equals(generated)) {
                        fetchActiveEventsFromServer(new RepositoryCallback<List<RandomEvent>>() {
                            @Override
                            public void onSuccess(List<RandomEvent> result) {
                                callback.onSuccess(true);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onSuccess(false);
                            }
                        });
                    } else {
                        callback.onSuccess(false);
                    }
                } else {
                    callback.onError("Failed to generate event");
                }
            }

            @Override
            public void onFailure(Call<java.util.Map<String, Object>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Get event history from server.
     */
    public void getEventHistory(RepositoryCallback<List<RandomEvent>> callback) {
        ApiClient.getApiService().getEventHistory().enqueue(new Callback<List<RandomEvent>>() {
            @Override
            public void onResponse(Call<List<RandomEvent>> call, Response<List<RandomEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch event history");
                }
            }

            @Override
            public void onFailure(Call<List<RandomEvent>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    /**
     * Deactivate expired events in local database.
     */
    public void deactivateExpiredEvents() {
        executorService.execute(() -> {
            String currentTime = java.time.LocalDateTime.now().toString();
            eventDao.deactivateExpiredEvents(currentTime);
        });
    }

    public interface RepositoryCallback<T> {
        void onSuccess(T result);

        void onError(String error);
    }
}
