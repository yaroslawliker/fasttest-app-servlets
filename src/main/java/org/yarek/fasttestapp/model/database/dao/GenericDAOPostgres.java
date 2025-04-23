package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GenericDAOPostgres {
    HikariDataSource dataSource;

    public GenericDAOPostgres(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> T findOne(String query, Map<Integer, Object> params, Function<ResultSet, T> function) {
        List<T> list = findAll(query, params, function);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.getFirst();
        }
    }

    public <T> List<T> findAll(String query, Map<Integer, Object> params, Function<ResultSet, T> extractData) {

        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            this.fillupPreparedStatement(preparedStatement, params);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(extractData.apply(resultSet));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(String query, Map<Integer, Object> params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            this.fillupPreparedStatement(preparedStatement, params);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillupPreparedStatement(PreparedStatement preparedStatement, Map<Integer, Object> params) throws SQLException {
        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            switch (entry.getValue()) {
                case String s -> preparedStatement.setString(entry.getKey(), s);
                case Integer i -> preparedStatement.setObject(entry.getKey(), i);
                case null -> preparedStatement.setNull(entry.getKey(), java.sql.Types.NULL);
                case Boolean b -> preparedStatement.setBoolean(entry.getKey(), b);
                default -> preparedStatement.setObject(entry.getKey(), entry.getValue());
            }
        }
    }
}
