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

    List<Question> getQuestionsOfQuiz(String quizId) {
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

    List<Answer> getAnswersOfQuestion(String questionId) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement questionStatement = connection.prepareStatement("SELECT content, is_correct FROM answers WHERE question = ?");
        ) {
            questionStatement.setString(1, questionId);
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
    public void saveNewQuiz(Quiz quiz) {

    }

    @Override
    public void registerQuizPassed(String username, String quizId, float score) {

    }

    private String getOwner(String ownerId) {
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
}
