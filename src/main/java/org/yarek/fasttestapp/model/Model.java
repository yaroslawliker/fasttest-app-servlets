package org.yarek.fasttestapp.model;

import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.entities.test.Test;
import org.yarek.fasttestapp.model.entities.test.TestPreview;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

import java.util.List;

public interface Model {
    void registerUser(User user);

    /**
     * Checks if the given password matches given username
     * @return user id if authentication is correct, null otherwise
     */
    String authenticate(String username, String password);
    User getUser(String userId) throws UsernameAlreadyExistsException;

    void getTestPreviews(List<TestPreview> testPreviews);
    void getTestPreviews(List<TestPreview> testPreviews, int amount);

    Test getTestById(String testId);
    void saveNewTest(Test test);
}
