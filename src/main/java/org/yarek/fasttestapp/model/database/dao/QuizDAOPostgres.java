package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.yarek.fasttestapp.model.entities.quiz.Answer;
import org.yarek.fasttestapp.model.entities.quiz.Question;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuizDAOPostgres implements QuizDAO {

    private static HikariDataSource dataSource;
    private final int deafultPreviewAmount = 10;

    public static void setDataSource(HikariDataSource dataSource) {
        QuizDAOPostgres.dataSource = dataSource;
    }

    @Override
    public List<QuizPreview> getQuizPreviews() {
        return getQuizPreviews(deafultPreviewAmount);
    }

    @Override
    public List<QuizPreview> getQuizPreviews(int amount) {
        List<QuizPreview> testPreviews = new ArrayList<>();

        try (Connection quizzesConnection = dataSource.getConnection();
             Connection usersConnection = dataSource.getConnection();
        ) {
            // Getting first <amount> quizzes
            Statement quizzStatement = quizzesConnection.createStatement();
            ResultSet quizRs = quizzStatement.executeQuery("SELECT id, name, description, creation_date, owner FROM quizzes ORDER BY creation_date DESC LIMIT " + amount);

            // Iteration through all row for quizzes
            while (quizRs.next()) {
                // Getting on unreferenced data
                String id = quizRs.getString("id");
                String name = quizRs.getString("name");
                String description = quizRs.getString("description");
                Date creationDate = quizRs.getDate("creation_date");

                // Getting owners username
                String ownerId = quizRs.getString("owner");
                String username = getOwner(ownerId);

                // Saving the quiz preview
                QuizPreview preview = new QuizPreview(id, username, name, description, creationDate);
                testPreviews.add(preview);
            }

            quizRs.close();
            quizzStatement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return testPreviews;
    }

    @Override
    public Quiz getQuizById(String quizId) {
        Quiz quiz;
        try (Connection connection = dataSource.getConnection();
        ) {
            PreparedStatement quizStatement = connection.prepareStatement("SELECT id, name, description, creation_date, owner FROM quizzes WHERE id = ?");
            quizStatement.setInt(1, Integer.parseInt(quizId));
            ResultSet quizRs = quizStatement.executeQuery();
            if (!quizRs.next()) {
                throw new RuntimeException("No such quiz: " + quizId);
            }
            String name = quizRs.getString("name");
            String description = quizRs.getString("description");
            Date creationDate = quizRs.getDate("creation_date");
            String ownerId = String.valueOf(quizRs.getInt("owner"));
            quizStatement.close();
            quizRs.close();

            // Getting username from id
            String username = getOwner(ownerId);

            // Instantiating quiz
            quiz = new Quiz();
            quiz.setId(quizId);
            quiz.setName(name);
            quiz.setDescription(description);
            quiz.setOwnerUsername(username);
            quiz.setCreationDate(creationDate);

            // Getting questions
            List<Question> questions = this.getQuestionsOfQuiz(quizId);
            quiz.setQuestions(questions);

            return quiz;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Question> getQuestionsOfQuiz(String quizId) {
        List<Question> questions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT id, content, score FROM questions WHERE quiz = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(quizId));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // Get direct parameters
                String id = String.valueOf(resultSet.getInt("id"));
                String content = resultSet.getString("content");
                float score = resultSet.getFloat("score");
                Question question = new Question(content, score);

                // Getting answers
                List<Answer> answers = this.getAnswersOfQuestion(id);
                question.setAnswers(answers);

                // Adding to result list
                questions.add(question);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return questions;
    }

    private List<Answer> getAnswersOfQuestion(String questionId) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement questionStatement = connection.prepareStatement("SELECT content, is_correct FROM answers WHERE question = ?");
        ) {
            questionStatement.setInt(1, Integer.parseInt(questionId));
            ResultSet questionRs = questionStatement.executeQuery();
            List<Answer> answers = new ArrayList<>();
            while (questionRs.next()) {
                String content = questionRs.getString("content");
                boolean isCorrect = questionRs.getBoolean("is_correct");
                Answer answer = new Answer(content, isCorrect);
                answers.add(answer);
            }
            return answers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String saveNewQuiz(Quiz quiz) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            // Getting user id instead of username
            String ownerId = getOwnerId(quiz.getOwnerUsername());

            // Saving the quiz
            connection.setAutoCommit(false);

            String sql = "INSERT INTO quizzes (name, description, creation_date, owner) VALUES (?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, quiz.getName());
            statement.setString(2, quiz.getDescription());
            statement.setDate(3, new java.sql.Date(quiz.getCreationDate().getTime()));
            statement.setInt(4, Integer.parseInt(ownerId));
            // Executing
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            // Getting back the created id
            if (!rs.next()) {
                throw new RuntimeException("No id was returned after saving a quiz.");
            }
            int quizId = rs.getInt("id");
            rs.close();
            statement.close();

            // Saving questions
            saveQuestions(quiz.getQuestions(), quizId, connection);

            // Commiting transaction
            connection.commit();

            return String.valueOf(quizId);

        } catch (SQLException | RuntimeException e) {
            // Rolling back the transaction if something had failed
            try {
                assert connection != null;
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException(e);

        } finally {
            // Closing the connection
            try {
                assert connection != null;
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveQuestions(List<Question> questions, int quizId, Connection connection) throws SQLException {
        String sql = "INSERT INTO questions (content, score, quiz) VALUES (?, ?, ?);";

        for (Question question : questions) {
            // Save question itself
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, question.getContent());
            statement.setFloat(2, question.getScore());
            statement.setInt(3, quizId);

            statement.executeUpdate();

            // Getting the new question id
            ResultSet rs = statement.getGeneratedKeys();

            if (!rs.next()) {
                throw new RuntimeException("No id was returned after saving a question");
            }

            int questionId = rs.getInt("id");
            rs.close();
            statement.close();

            saveAnswers(question.getAnswers(), questionId, connection);
        }
    }

    private void saveAnswers(List<Answer> answers, int questionId, Connection connection) throws SQLException {
        String sql = "INSERT INTO answers (content, is_correct, question) VALUES (?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (Answer answer : answers) {
            statement.setString(1, answer.getContent());
            statement.setBoolean(2, answer.isCorrect());
            statement.setInt(3, questionId);
            statement.addBatch();
        }
        statement.executeBatch();
        statement.close();
    }

    @Override
    public void registerQuizPassed(String username, String quizId, float score) {
        try( Connection connection = dataSource.getConnection(); ) {

            int userId = Integer.parseInt(getOwnerId(username));

            String sql = "INSERT INTO results (score, user_id, quiz) VALUES (?, ?, ?);";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setFloat(1, score);
            statement.setInt(2, userId);
            statement.setInt(3, Integer.parseInt(quizId));
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getOwner(String ownerId) {
        String username;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement userStatement = connection.prepareStatement("SELECT username FROM users WHERE id = ?"); ) {

            userStatement.setInt(1, Integer.parseInt(ownerId));
            ResultSet userRs = userStatement.executeQuery();
            if (userRs.next()) {
                username = userRs.getString("username");
            } else {
                throw new RuntimeException("No user for the quiz. user id: " + ownerId);
            }
            userRs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return username;
    }

    public String getOwnerId(String username) {
        String ownerId;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement userStatement = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
        ) {
            userStatement.setString(1, username);
            ResultSet userRs = userStatement.executeQuery();

            if (userRs.next()) {
                ownerId = String.valueOf(userRs.getInt("id"));
            } else {
                throw new RuntimeException("No user with username: " + username);
            }
            userRs.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ownerId;
    }
}
