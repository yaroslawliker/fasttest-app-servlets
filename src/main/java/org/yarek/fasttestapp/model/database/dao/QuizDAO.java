package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizDAO {
    List<QuizPreview> getQuizPreviews();
    List<QuizPreview> getQuizPreviews(int amount);

    Quiz getQuizById(String quizId);
    String saveNewQuiz(Quiz quiz);

    void startQuizPassing(String userID, String quizID, LocalDateTime startTime);
    void finishQuizPassing(String userID, String quizID, LocalDateTime endTime, float score);
}
