package com.growfund.seedtowealth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.growfund.seedtowealth.model.Quiz;
import com.growfund.seedtowealth.network.ApiClient;
import com.growfund.seedtowealth.utils.ErrorHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText;
    private Button optionA, optionB, optionC, optionD;
    private ProgressBar loadingProgress;
    private Quiz currentQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        questionText = findViewById(R.id.questionText);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        loadingProgress = findViewById(R.id.loadingProgress);

        optionA.setOnClickListener(v -> submitAnswer(0));
        optionB.setOnClickListener(v -> submitAnswer(1));
        optionC.setOnClickListener(v -> submitAnswer(2));
        optionD.setOnClickListener(v -> submitAnswer(3));

        loadDailyQuiz();
    }

    private void loadDailyQuiz() {
        loadingProgress.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getDailyQuiz().enqueue(new Callback<Quiz>() {
            @Override
            public void onResponse(Call<Quiz> call, Response<Quiz> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentQuiz = response.body();
                    displayQuiz(currentQuiz);
                } else {
                    questionText.setText("No daily challenge available right now.");
                    disableOptions();
                }
            }

            @Override
            public void onFailure(Call<Quiz> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                ErrorHandler.handleError(QuizActivity.this, t);
            }
        });
    }

    private void displayQuiz(Quiz quiz) {
        questionText.setText(quiz.getQuestion());
        optionA.setText(quiz.getOptionA());
        optionB.setText(quiz.getOptionB());
        optionC.setText(quiz.getOptionC());
        optionD.setText(quiz.getOptionD());
        enableOptions();
    }

    private void submitAnswer(int optionIndex) {
        if (currentQuiz == null)
            return;

        loadingProgress.setVisibility(View.VISIBLE);
        disableOptions();

        ApiClient.getApiService().submitQuiz(currentQuiz.getId(), optionIndex).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingProgress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    boolean isCorrect = response.body();
                    if (isCorrect) {
                        com.growfund.seedtowealth.utils.SoundManager.playSuccessSound(QuizActivity.this);
                        Toast.makeText(QuizActivity.this, "Correct! +50 Coins", Toast.LENGTH_LONG).show();
                        highlightCorrect(optionIndex);
                    } else {
                        com.growfund.seedtowealth.utils.SoundManager.playFailureSound(QuizActivity.this);
                        Toast.makeText(QuizActivity.this, "Incorrect. Try again tomorrow!", Toast.LENGTH_LONG).show();
                        highlightIncorrect(optionIndex);
                    }
                    // Close after short delay? Or let user stay
                    questionText.postDelayed(() -> finish(), 2000);
                } else {
                    ErrorHandler.handleApiError(QuizActivity.this, response);
                    enableOptions();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                ErrorHandler.handleError(QuizActivity.this, t);
                enableOptions();
            }
        });
    }

    private void highlightCorrect(int index) {
        Button btn = getButton(index);
        btn.setBackgroundColor(0xFF4CAF50); // Green
        btn.setTextColor(0xFFFFFFFF);
    }

    private void highlightIncorrect(int index) {
        Button btn = getButton(index);
        btn.setBackgroundColor(0xFFF44336); // Red
        btn.setTextColor(0xFFFFFFFF);
    }

    private Button getButton(int index) {
        switch (index) {
            case 0:
                return optionA;
            case 1:
                return optionB;
            case 2:
                return optionC;
            case 3:
                return optionD;
            default:
                return optionA;
        }
    }

    private void disableOptions() {
        optionA.setEnabled(false);
        optionB.setEnabled(false);
        optionC.setEnabled(false);
        optionD.setEnabled(false);
    }

    private void enableOptions() {
        optionA.setEnabled(true);
        optionB.setEnabled(true);
        optionC.setEnabled(true);
        optionD.setEnabled(true);
    }
}
