package org.yarek.fasttestapp.model.impl;

import org.yarek.fasttestapp.model.Model;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

import java.util.List;

public class NoMemoryModel implements Model {

    @Override
    public void registerUser(User user) throws UsernameAlreadyExistsException {

    }

    @Override
    public String authenticate(String username, String password) {
        return "";
    }

    @Override
    public User getUser(String userId) {
        return null;
    }

    @Override
    public void getTestPreviews(List<QuizPreview> quizPreviews) {

    }

    @Override
    public void getTestPreviews(List<QuizPreview> quizPreviews, int amount) {

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
