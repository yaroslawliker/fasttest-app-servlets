package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.yarek.fasttestapp.model.Constants;
import org.yarek.fasttestapp.model.database.LoaderSQL;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
class UserDAOPostgresTest {

    static HikariDataSource dataSource;

    // ----------------
    // Technicals methods
    // ----------------

    @BeforeAll
    static void setup() {
        setUpHikari();
    }

    private static void setUpHikari() {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");

        dataSource.setJdbcUrl(Constants.DATABASE_URL);
        dataSource.setUsername(Constants.DATABASE_USER);
        dataSource.setPassword(Constants.DATABASE_PASSWORD);

        dataSource.setSchema("test");
        dataSource.setMaximumPoolSize(10);

        if (!Objects.equals(dataSource.getSchema(), "test")) {
            throw new TestInstantiationException("FORGOT TO MENTION SCHEMA test!!!");
        }
    }

    @BeforeEach
    public void InitDatabase() throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        String sql = LoaderSQL.load("create_users_table");
        stmt.executeUpdate(sql);
        stmt.close();
    }

    @AfterEach
    public void cleanUpDatabase() throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        String sql = "DROP TABLE users;";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    // -----
    // Tests
    // -----

    @Test
    void testGetExistingUser() throws SQLException {
        // Adding a user into DB
        Connection conn = dataSource.getConnection();
        String sql = "INSERT INTO users (username, password, role) VALUES ('exampleUser123', 'examplePass123', ?);";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, User.Role.USER.name());

        stmt.executeUpdate();
        stmt.close();

        UserDAO userDAO = new UserDAOPostgres(dataSource);

        User user = userDAO.getUser("exampleUser123");
        assertEquals("exampleUser123", user.getUsername());
        assertEquals("examplePass123", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    void testGetNonExistingUser() throws SQLException {
        UserDAO userDAO = new UserDAOPostgres(dataSource);
        User user = userDAO.getUser("noExistingUser");
        assertNull(user);
    }

    @Test
    void registerUserTest() throws SQLException {
        User user = new User("cooluser123", "myC00lPassword", User.Role.USER);
        UserDAO userDAO = new UserDAOPostgres(dataSource);

        userDAO.registerUser(user);

        User userFromDB = userDAO.getUser(user.getUsername());

        assertEquals(user.getUsername(), userFromDB.getUsername());
        assertEquals(user.getPassword(), userFromDB.getPassword());
        assertEquals(user.getRole(), userFromDB.getRole());
    }

    @Test
    void testRegisterSameUsername() throws SQLException {
        User user = new User("cooluser123", "myC00lPassword", User.Role.USER);
        UserDAO userDAO = new UserDAOPostgres(dataSource);

        User sameUsername = new User("cooluser123", "anotherPassword", User.Role.USER);

        userDAO.registerUser(user);
        assertThrows(UsernameAlreadyExistsException.class, () -> userDAO.registerUser(sameUsername));
    }

    @Test
    void testGetUsernameById() {
        UserDAO userDAO = new UserDAOPostgres(dataSource);

        String unexistingUserID = "1000";
        assertThrows(RuntimeException.class, () -> userDAO.getUsernameByID(unexistingUserID));

        User user = new User("cooluser123", "myC00lPassword", User.Role.USER);
        userDAO.registerUser(user);

        String username = userDAO.getUsernameByID("1");

        assertEquals("cooluser123", username);
    }

}