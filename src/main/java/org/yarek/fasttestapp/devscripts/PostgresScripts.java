package org.yarek.fasttestapp.devscripts;

import org.postgresql.jdbc.PgConnection;
import org.yarek.fasttestapp.model.Constants;

import java.sql.*;

public class PostgresScripts {
    public static void main(String[] args) {

        try {
            String param0 = args[0];
            switch (param0) {
                case "clearTestSchema":
                    clearTestSchema();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown parameter: " + param0);

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("PostgresScripts entrypoint must have at least 1 param!");
        }
    }

    public static void clearTestSchema() {
        try (Connection connection = 
                     DriverManager.getConnection(
                             Constants.DATABASE_URL, 
                             Constants.DATABASE_USER, 
                             Constants.DATABASE_PASSWORD)) {
            
            Statement stmt = connection.createStatement();
            String sql = "DROP TABLE IF EXISTS test.";

            for (String table : new String[] { "results", "answers", "questions", "quizzes", "users" }) {

                stmt.addBatch(sql+table+";");
            }
            stmt.executeBatch();
            stmt.close();
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
