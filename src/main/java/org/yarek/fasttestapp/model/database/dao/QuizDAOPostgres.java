package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.validation.constraints.NotNull;
import org.yarek.fasttestapp.model.database.entities.QuizResultData;
import org.yarek.fasttestapp.model.entities.quiz.Answer;
import org.yarek.fasttestapp.model.entities.quiz.Question;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.database.entities.QuizPreviewData;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuizDAOPostgres implements QuizDAO {

    private HikariDataSource dataSource;
    private GenericDAOPostgres genericDAO;
    private final int deafultPreviewAmount = 10;

    public QuizDAOPostgres(@NotNull HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.genericDAO = new GenericDAOPostgres(dataSource);
    }

    @Override
    public List<QuizPreviewData> getQuizPreviews() {
        return getQuizPreviews(deafultPreviewAmount);
    }

    @Override
    public List<QuizPreviewData> getQuizPreviews(int amount) {
        return genericDAO.findAll(
                "SELECT id, name, description, creation_date, owner FROM quizzes ORDER BY creation_date DESC LIMIT " + amount,
                Map.of(),
                this::extractQuizPreviewDataFromResultSet
        );
    }

    QuizPreviewData extractQuizPreviewDataFromResultSet(ResultSet resultSet) {
        try {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            Date creationDate = resultSet.getDate("creation_date");
            String ownerID = resultSet.getString("owner");

            return new QuizPreviewData(id, ownerID, name, description, creationDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Quiz getQuizById(String quizId) {

        Quiz quiz = genericDAO.findOne(
                "SELECT id, name, description, creation_date, owner FROM quizzes WHERE id = ?",
                Map.of(1, Integer.parseInt(quizId)),
                this::extractQuizFromResultSet
        );

        if (quiz == null) {
            throw new RuntimeException("No such quiz: " + quizId);
        }

        // Getting questions
        List<Question> questions = this.getQuestionsOfQuiz(quizId);
        quiz.setQuestions(questions);

        return quiz;
    }

    Quiz extractQuizFromResultSet(ResultSet resultSet) {
        try {
            String quizId = String.valueOf(resultSet.getInt("id"));
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            Date creationDate = resultSet.getDate("creation_date");
            String ownerID = String.valueOf(resultSet.getInt("owner"));

            // Instantiating quiz
            Quiz quiz = new Quiz();
            quiz.setId(quizId);
            quiz.setName(name);
            quiz.setDescription(description);
            quiz.setOwnerID(ownerID);
            quiz.setCreationDate(creationDate);

            return quiz;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Question> getQuestionsOfQuiz(String quizId) {
        List<Question> questions = new ArrayList<>();

        questions = genericDAO.findAll(
                "SELECT id, content, score FROM questions WHERE quiz = ?",
                Map.of(1,Integer.parseInt(quizId)),
                this::getQuestionsFromResultSet
        );

        return questions;
    }

    Question getQuestionsFromResultSet(ResultSet resultSet) {
        try {
            // Getting question itself
            String id = String.valueOf(resultSet.getInt("id"));
            String content = resultSet.getString("content");
            float score = resultSet.getFloat("score");
            Question question = new Question(content, score);

            // Getting answers
            List<Answer> answers = this.getAnswersOfQuestion(id);
            question.setAnswers(answers);

            return question;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Answer> getAnswersOfQuestion(String questionId) {

        List<Answer> answers;
        answers = genericDAO.findAll(
                "SELECT content, is_correct FROM answers WHERE question = ?",
                Map.of(1, Integer.parseInt(questionId)),
                this::extractAnswerFromResultSet
        );
        return answers;
    }

    Answer extractAnswerFromResultSet(ResultSet resultSet) {
        try {
            String content = resultSet.getString("content");
            boolean isCorrect = resultSet.getBoolean("is_correct");
            return new Answer(content, isCorrect);
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
            String ownerId = quiz.getOwnerID();

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
    public void startQuizPassing(String userID, String quizID, LocalDateTime startTime) {
        try( Connection connection = dataSource.getConnection(); ) {

            String sql = "INSERT INTO results (user_id, quiz, start_time) VALUES (?, ?, ?);";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(userID));
            statement.setInt(2, Integer.parseInt(quizID));
            statement.setTimestamp(3, Timestamp.valueOf(startTime));
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishQuizPassing(String userID, String quizID, LocalDateTime endTime, float score) {
        String sql = "UPDATE results SET finish_time=?, score=? WHERE user_id=? AND quiz=? AND finish_time IS NULL;";

        try( Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(endTime));
            statement.setFloat(2, score);
            statement.setInt(3, Integer.parseInt(userID));
            statement.setInt(4, Integer.parseInt(quizID));
            int modified = statement.executeUpdate();

            // Check if only one row modified (as expected)
            if (modified != 1) {
                String problem;
                if (modified < 1) {
                    problem = "no quiz passing found to finish.";
                } else {
                    problem = "few quiz passing found.";
                }
                throw new RuntimeException(
                        "Error while finishing quiz: " + problem + "User = " + userID
                        + " Quiz = " + quizID + " Modified: " + modified);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUserPassingQuiz(String userID, String quizID) {
        String sql = "SELECT id FROM results WHERE user_id=? AND quiz=? AND finish_time IS NULL;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setInt(1, Integer.parseInt(userID));
            statement.setInt(2, Integer.parseInt(quizID));

            ResultSet resultSet = statement.executeQuery();
            boolean started = resultSet.next();

            resultSet.close();

            return started;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<QuizResultData> getQuizResultsOfUser(String userID) {
        String sql = "SELECT score, start_time, finish_time, user_id, quiz FROM results WHERE user_id=?;";

        return genericDAO.findAll(
                sql,
                Map.of(1, Integer.valueOf(userID)),
                this::extractQuizResultDataFromResultSet
        );
    }

    QuizResultData extractQuizResultDataFromResultSet(ResultSet resultSet) {
        try {
            QuizResultData quizResultData = new QuizResultData();

            quizResultData.setId(null);

            quizResultData.setScore(resultSet.getFloat(1));
            quizResultData.setStartTime(resultSet.getTimestamp(2).toLocalDateTime());
            quizResultData.setUserId(String.valueOf(resultSet.getInt(4)));
            quizResultData.setQuizId(String.valueOf(resultSet.getInt(5)));

            // Finish time may be null in DB
            Timestamp finishTimestamp = resultSet.getTimestamp(3);
            LocalDateTime finishLocalDateTime = finishTimestamp != null ? finishTimestamp.toLocalDateTime() : null;
            quizResultData.setFinishTime(finishLocalDateTime);

            return quizResultData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
