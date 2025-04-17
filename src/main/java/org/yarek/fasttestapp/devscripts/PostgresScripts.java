package org.yarek.fasttestapp.devscripts;

import org.yarek.fasttestapp.model.Constants;
import org.yarek.fasttestapp.model.database.LoaderSQL;

import java.sql.*;

public class PostgresScripts {
    public static void main(String[] args) {

        try {
            String param0 = args[0];
            String schema;
            switch (param0) {
                case "dropTables":
                    schema = args[1];
                    dropTables(schema);
                    break;
                case "initTables":
                    schema = args[1];
                    initTables(schema);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown parameter: " + param0);

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("PostgresScripts entrypoint must have at least 1 param!");
        }
    }

    public static void initTables(String schema) {
        try (Connection connection =
                     DriverManager.getConnection(
                             Constants.DATABASE_URL,
                             Constants.DATABASE_USER,
                             Constants.DATABASE_PASSWORD)) {

            String createUsersSQL = LoaderSQL.load("create_users_table");
            PreparedStatement ps = connection.prepareStatement(createUsersSQL);
            ps.executeUpdate();
            ps.close();

            String createQuizSQL = LoaderSQL.load("create_quiz_tables");
            ps = connection.prepareStatement(createQuizSQL);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void dropTables(String schema) {
        try (Connection connection = 
                     DriverManager.getConnection(
                             Constants.DATABASE_URL, 
                             Constants.DATABASE_USER, 
                             Constants.DATABASE_PASSWORD)) {
            
            Statement stmt = connection.createStatement();
            String sql = "DROP TABLE IF EXISTS ";

            for (String table : new String[] { "results", "answers", "questions", "quizzes", "users" }) {

                stmt.addBatch(sql + schema + '.' + table + ";");
            }
            stmt.executeBatch();
            stmt.close();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
