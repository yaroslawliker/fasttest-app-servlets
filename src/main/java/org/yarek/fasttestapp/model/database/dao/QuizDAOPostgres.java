package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;

import java.util.List;

public class QuizDAOPostgres implements QuizDAO {
    @Override
    public List<QuizPreview> getTestPreviews() {
        return List.of();
    }

    @Override
    public List<QuizPreview> getTestPreviews(int amount) {
        return List.of();
    }

    @Override
    public Quiz getTestById(String testId) {
        return null;
    }

    @Override
    public void saveNewTest(Quiz quiz) {

    }

    @Override
    public void registerQuizPassed(String username, String quizId, float score) {

    }
}
