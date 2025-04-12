package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;

import java.util.List;

public interface QuizDAO {
    List<QuizPreview> getTestPreviews();
    List<QuizPreview> getTestPreviews(int amount);

    Quiz getTestById(String testId);
    void saveNewTest(Quiz quiz);

    void registerQuizPassed(String username, String quizId, float score);
}
