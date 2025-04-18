package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.validation.constraints.NotNull;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

import java.sql.*;

public class UserDAOPostgres implements UserDAO {

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(@NotNull HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private HikariDataSource dataSource;

    public UserDAOPostgres(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void registerUser(User user) throws UsernameAlreadyExistsException {

        try(Connection connection = dataSource.getConnection();) {

            // Getting users params
            String username = user.getUsername();
            String password = user.getPassword();
            User.Role role = user.getRole();

            // Checking if new username is unique
            String getSameUsernameSQL = "SELECT username FROM users WHERE username = ?;";
            PreparedStatement checkStatement = connection.prepareStatement(getSameUsernameSQL);
            checkStatement.setString(1, username);


            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                throw new UsernameAlreadyExistsException(user.getUsername());
            }

            checkStatement.close();

            // Saving the user
            String saveUserSQL = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

            PreparedStatement saveStatement = connection.prepareStatement(saveUserSQL);
            saveStatement.setString(1, username);
            saveStatement.setString(2, password);
            saveStatement.setString(3, role.name());
            saveStatement.executeUpdate();

            saveStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Error with database", e);
        }

    }

    @Override
    public User getUser(String username) {
        try(Connection connection = dataSource.getConnection()) {
            String selectUserSQL = "SELECT username, password, role FROM users WHERE username = ?";
            PreparedStatement saveStatement = connection.prepareStatement(selectUserSQL);
            saveStatement.setString(1, username);
            ResultSet resultSet = saveStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(User.Role.valueOf(resultSet.getString("role")));

                saveStatement.close();

                return user;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error with database", e);
        }
    }

    @Override
    public String getUsernameByID(String userID) {
        String username;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement userStatement = connection.prepareStatement("SELECT username FROM users WHERE id = ?"); ) {

            userStatement.setInt(1, Integer.parseInt(userID));
            ResultSet userRs = userStatement.executeQuery();
            if (userRs.next()) {
                username = userRs.getString("username");
            } else {
                throw new RuntimeException("No user for the quiz. user id: " + userID);
            }
            userRs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return username;
    }
}
