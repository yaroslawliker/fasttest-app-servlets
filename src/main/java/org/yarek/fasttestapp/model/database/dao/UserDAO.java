package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

public interface UserDAO {
    /**
     * Checks if the given password matches given username
     */
    void registerUser(User user) throws UsernameAlreadyExistsException;
    User getUser(String username);
    String getUsernameByID(String userID);
}
