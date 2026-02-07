package com.growfund.repository;

import com.growfund.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Fetch a random quiz for the day
    @Query(value = "SELECT * FROM quizzes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Quiz> findRandomQuiz();
}
