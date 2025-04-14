package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/fasttestapp");
        dataSource.setUsername("postgres");
        dataSource.setPassword("1234");
        dataSource.setSchema("test");
        dataSource.setMaximumPoolSize(10);

        if (!Objects.equals(dataSource.getSchema(), "test")) {
            throw new TestInstantiationException("FORGOT TO MENTION SCHEMA test!!!");
        }

        UserDAOPostgres.setDataSource(dataSource);

    }

    @BeforeEach
    public void InitDatabase() throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE users (id SERIAL PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, ROLE VARCHAR(10) NOT NULL);";
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

        UserDAO userDAO = new UserDAOPostgres();

        User user = userDAO.getUser("exampleUser123");
        assertEquals("exampleUser123", user.getUsername());
        assertEquals("examplePass123", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    void testGetNonExistingUser() throws SQLException {
        UserDAO userDAO = new UserDAOPostgres();
        User user = userDAO.getUser("noExistingUser");
        assertNull(user);
    }

    @Test
    void registerUserTest() throws SQLException {
        User user = new User("cooluser123", "myC00lPassword", User.Role.USER);
        UserDAO userDAO = new UserDAOPostgres();

        userDAO.registerUser(user);

        User userFromDB = userDAO.getUser(user.getUsername());

        assertEquals(user.getUsername(), userFromDB.getUsername());
        assertEquals(user.getPassword(), userFromDB.getPassword());
        assertEquals(user.getRole(), userFromDB.getRole());
    }

    @Test
    void testRegisterSameUsername() throws SQLException {
        User user = new User("cooluser123", "myC00lPassword", User.Role.USER);
        UserDAO userDAO = new UserDAOPostgres();

        User sameUsername = new User("cooluser123", "anotherPassword", User.Role.USER);

        userDAO.registerUser(user);
        assertThrows(UsernameAlreadyExistsException.class, () -> userDAO.registerUser(sameUsername));
    }

}