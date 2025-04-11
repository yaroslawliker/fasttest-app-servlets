package org.yarek.fasttestapp.model.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super("Username already exists: " + message);
    }
}
