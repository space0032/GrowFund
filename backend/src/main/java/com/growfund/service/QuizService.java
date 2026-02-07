package com.growfund.service;

import com.growfund.model.Quiz;
import com.growfund.model.User;
import com.growfund.model.Farm;
import com.growfund.repository.QuizRepository;
import com.growfund.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final FarmRepository farmRepository;

    public Optional<Quiz> getDailyQuiz(User user) {
        // In a real app, track attempts daily.
        // For MVP, just return a random quiz.
        return quizRepository.findRandomQuiz();
    }

    @Transactional
    public boolean submitAnswer(User user, Long quizId, Integer selectedOptionIndex) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getCorrectOptionIndex().equals(selectedOptionIndex)) {
            // Reward User
            Farm farm = farmRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Farm not found for user"));

            farm.setSavings(farm.getSavings() + quiz.getRewardCoins());
            user.setExperience(user.getExperience() + quiz.getRewardExperience());

            // Check level up (simplified)
            if (user.getExperience() >= user.getCurrentLevel() * 1000) {
                user.setCurrentLevel(user.getCurrentLevel() + 1);
            }

            farmRepository.save(farm);
            // user is saved via cascade or we can save user separately if needed,
            // but farm->user relation might handle it or we inject UserRepository

            return true;
        }

        return false;
    }

    public void createSampleQuizzes() {
        if (quizRepository.count() == 0) {
            saveQuiz("What is the best way to reduce risk in farming?",
                    "Monoculture", "Crop Diversification", "Using more fertilizer", "Selling land", 1);

            saveQuiz("What is a Kissan Credit Card (KCC)?",
                    "A debit card for shopping", "A loan scheme for farmers", "A government ID", "A type of seed", 1);

            saveQuiz("Which crop is suitable for Kharif season?",
                    "Wheat", "Rice (Paddy)", "Mustard", "Barley", 1);

            saveQuiz("What does MSP stand for?",
                    "Maximum Selling Price", "Minimum Support Price", "Market Standard Price", "Monthly Savings Plan",
                    1);

            saveQuiz("Why is soil testing important?",
                    "To increase land price", "To know nutrient needs", "To get a loan", "To buy a tractor", 1);
        }
    }

    private void saveQuiz(String q, String a, String b, String c, String d, int correct) {
        Quiz quiz = new Quiz();
        quiz.setQuestion(q);
        quiz.setOptionA(a);
        quiz.setOptionB(b);
        quiz.setOptionC(c);
        quiz.setOptionD(d);
        quiz.setCorrectOptionIndex(correct);
        quiz.setRewardCoins(50L);
        quiz.setRewardExperience(20L);
        quizRepository.save(quiz);
    }
}
