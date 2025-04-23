package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.validation.constraints.NotNull;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

import java.sql.*;
import java.util.Map;

public class UserDAOPostgres implements UserDAO {

    private HikariDataSource dataSource;
    private GenericDAOPostgres genericDAO;


    public UserDAOPostgres(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.genericDAO = new GenericDAOPostgres(dataSource);
    }

    public HikariDataSource getDataSource() { return dataSource; }
    public void setDataSource(@NotNull HikariDataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public void registerUser(User user) throws UsernameAlreadyExistsException {

        // Getting users params
        String username = user.getUsername();
        String password = user.getPassword();
        User.Role role = user.getRole();

        // Checking if new username is unique
        User sameUser = this.getUser(username);
        if (sameUser != null) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        // Saving the user
       genericDAO.executeUpdate(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                Map.of(1, username, 2, password, 3, role.name())
       );
    }

    @Override
    public User getUser(String username) {

        return genericDAO.findOne(
                "SELECT id, username, password, role FROM users WHERE username = ?",
                Map.of(1, username),
                this::getUserFromResultSet
        );
    }

    User getUserFromResultSet(ResultSet resultSet) {
        try {
            User user = new User();
            user.setId(String.valueOf(resultSet.getInt("id")));
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
            user.setRole(User.Role.valueOf(resultSet.getString("role")));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsernameByID(String userID) {
        String username = genericDAO.findOne(
                "SELECT username FROM users WHERE id = ?",
                Map.of(1, userID),
                (ResultSet resultSet) -> {
                    try {
                        return resultSet.getString("username");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        if (username==null) {
            throw new RuntimeException("No user for the quiz. user id: " + userID);
        }

        return username;
    }
}
