package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.yarek.fasttestapp.model.entities.User;
import org.yarek.fasttestapp.model.exceptions.UsernameAlreadyExistsException;

public class UserDAOPostgres implements UserDAO {

    private static final HikariDataSource dataSource = new HikariDataSource();

    static {
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/fasttestapp");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setMaximumPoolSize(5);
    }

    public UserDAOPostgres() {

    }



    @Override
    public String registerUser(User user) throws UsernameAlreadyExistsException {
        return "";
    }

    @Override
    public User getUser(String userId) {
        return null;
    }
}
