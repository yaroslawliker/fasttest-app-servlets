package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;

import java.util.List;

public interface QuizDAO {
    List<QuizPreview> getQuizPreviews();
    List<QuizPreview> getQuizPreviews(int amount);

    Quiz getQuizById(String quizId);
    String saveNewQuiz(Quiz quiz);

    void registerQuizPassed(String username, String quizId, float score);
}
