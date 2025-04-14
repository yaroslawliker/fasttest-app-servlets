package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

import java.sql.*;

public class UserDAOPostgres implements UserDAO {

    private static HikariDataSource dataSource;

    public static void setDataSource(HikariDataSource dataSource) {
        UserDAOPostgres.dataSource = dataSource;
    }

    @Override
    public void registerUser(User user) throws UsernameAlreadyExistsException {

        try(Connection connection = dataSource.getConnection();) {

            // Getting users params
            String username = user.getUsername();
            String password = user.getPassword();

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
            String saveUserSQL = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";

            PreparedStatement saveStatement = connection.prepareStatement(saveUserSQL);
            saveStatement.setString(2, username);
            saveStatement.setString(3, password);
            saveStatement.executeUpdate();

            saveStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Error with database", e);
        }

    }

    @Override
    public User getUser(String username) {
        try(Connection connection = dataSource.getConnection()) {
            String selectUserSQL = "SELECT id, username FROM users WHERE username = ?";
            PreparedStatement saveStatement = connection.prepareStatement(selectUserSQL);
            saveStatement.setString(1, username);
            ResultSet resultSet = saveStatement.executeQuery();

            User user = new User();
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));

            saveStatement.close();

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error with database", e);
        }
    }
}
