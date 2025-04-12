package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

public interface UserDAO {
    /**
     * Checks if the given password matches given username
     * @return user id if authentication is correct, null otherwise
     */
    String registerUser(User user) throws UsernameAlreadyExistsException;
    User getUser(String userId);
}
