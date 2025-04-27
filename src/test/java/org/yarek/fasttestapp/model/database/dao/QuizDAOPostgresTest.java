package org.yarek.fasttestapp.model.database.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.yarek.fasttestapp.devscripts.PostgresScripts;
import org.yarek.fasttestapp.model.Constants;
import org.yarek.fasttestapp.model.database.LoaderSQL;
import org.yarek.fasttestapp.model.database.entities.QuizResultData;
import org.yarek.fasttestapp.model.entities.quiz.Answer;
import org.yarek.fasttestapp.model.entities.quiz.Question;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.database.entities.QuizPreviewData;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
class QuizDAOPostgresTest {

    static HikariDataSource dataSource;

    // ----------------
    // Technicals methods
    // ----------------

    @BeforeAll
    static void setup() {
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

        stmt = conn.createStatement();
        sql = LoaderSQL.load("create_quiz_tables");
        stmt.executeUpdate(sql);
        stmt.close();

        conn = dataSource.getConnection();
        stmt = conn.createStatement();
        sql = LoaderSQL.load("add_dumb_users_quizzes");
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }

    @AfterEach
    public void cleanUpDatabase() throws SQLException {
        PostgresScripts.dropTables("test");
    }

    @Test
    public void testGetQuizPreviews() throws SQLException {

        // Testing results
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);
        List<QuizPreviewData> previews = quizDAO.getQuizPreviews(2);

        assertEquals(2, previews.size());

        assertEquals("My quiz3", previews.get(0).getName());
        assertEquals("This is my quiz3", previews.get(0).getDescription());
        assertEquals(Date.valueOf("2024-03-30"), previews.get(0).getCreationDate());

        assertEquals("My quiz2", previews.get(1).getName());
        assertEquals("This is my quiz2", previews.get(1).getDescription());
        assertEquals(Date.valueOf("2024-02-20"), previews.get(1).getCreationDate());
    }

    @Test
    public void testGetQuizPreviewsOfAuthor() throws SQLException {

        // Testing results
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);
        List<QuizPreviewData> previews = quizDAO.getQuizPreviewsOfAuthor("1", 2);

        assertEquals(2, previews.size());

        assertEquals("My quiz3", previews.get(0).getName());
        assertEquals("This is my quiz3", previews.get(0).getDescription());
        assertEquals(Date.valueOf("2024-03-30"), previews.get(0).getCreationDate());

        assertEquals("My quiz1", previews.get(1).getName());
        assertEquals("This is my quiz1", previews.get(1).getDescription());
        assertEquals(Date.valueOf("2024-01-10"), previews.get(1).getCreationDate());
    }

    @Test
    public void testGetQuestById() throws SQLException {
        // Prepare DB data
        // Insert questions and answers
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        String sql = LoaderSQL.load("add_dumb_questions_answers");
        stmt.executeUpdate(sql);
        stmt.close();

        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);
        Quiz quiz = quizDAO.getQuizById("1");
        assertNotNull(quiz);
        assertEquals("1", quiz.getId());
        assertEquals("My quiz1", quiz.getName());
        assertEquals("This is my quiz1", quiz.getDescription());
        assertEquals(Date.valueOf("2024-01-10"), quiz.getCreationDate());

        Question question;
        Answer answer;

        // Question 1
        question = quiz.getQuestions().get(0);
        assertNotNull(question);
        assertEquals("Is this good question?1", question.getContent());
        assertEquals(1, question.getScore());
        assertEquals(3, question.getAnswers().size());
        // Answers
        answer = question.getAnswers().get(0);
        assertEquals("Is this answer right?1", answer.getContent());
        assertTrue(answer.isCorrect());
        answer = question.getAnswers().get(1);
        assertEquals("Is this answer right?2", answer.getContent());
        assertFalse(answer.isCorrect());
        answer = question.getAnswers().get(2);
        assertEquals("Is this answer right?3", answer.getContent());
        assertFalse(answer.isCorrect());

        // Question 2
        question = quiz.getQuestions().get(1);
        assertNotNull(question);
        assertEquals("Is this good question?2", question.getContent());
        assertEquals(2, question.getScore());
        assertEquals(3, question.getAnswers().size());

        // Question 3
        question = quiz.getQuestions().get(2);
        assertNotNull(question);
        assertEquals("Is this good question?3", question.getContent());
        assertEquals(0.5, question.getScore());
        assertEquals(3, question.getAnswers().size());
        // Answers
        answer = question.getAnswers().get(0);
        assertEquals("Is this answer right?1", answer.getContent());
        assertTrue(answer.isCorrect());
        answer = question.getAnswers().get(1);
        assertEquals("Is this answer right?2", answer.getContent());
        assertFalse(answer.isCorrect());
        answer = question.getAnswers().get(2);
        assertEquals("Is this answer right?3", answer.getContent());
        assertTrue(answer.isCorrect());
    }

    @Test
    public void testSaveQuiz() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        // Setting up quiz
        Quiz originalQuiz = new Quiz();
        originalQuiz.setName("My quiz");
        originalQuiz.setDescription("This is my quiz");
        originalQuiz.setCreationDate(Date.valueOf("2024-03-30"));
        originalQuiz.setOwnerID("1");

        Question question;
        Answer answer;

        // Question 1
        question = new Question("Is question1 correct?");
        answer = new Answer("Is this answer right?1", true);
        question.getAnswers().add(answer);
        answer = new Answer("Is this answer right?2", false);
        question.getAnswers().add(answer);

        originalQuiz.getQuestions().add(question);

        // Question 2
        question = new Question("Is question2 correct?", 2.5f);
        answer = new Answer("Is this answer right?1", true);
        question.getAnswers().add(answer);
        answer = new Answer("Is this answer right?2", false);
        question.getAnswers().add(answer);
        answer = new Answer("Is this answer right?3", true);
        question.getAnswers().add(answer);

        originalQuiz.getQuestions().add(question);

        // Saving
        String newID = quizDAO.saveNewQuiz(originalQuiz);
        Quiz quizFromDB = quizDAO.getQuizById(newID);

        assertNotNull(quizFromDB);
        assertEquals(newID, quizFromDB.getId());
        assertEquals(originalQuiz.getName(), quizFromDB.getName());
        assertEquals(originalQuiz.getDescription(), quizFromDB.getDescription());
        assertEquals(originalQuiz.getCreationDate(), quizFromDB.getCreationDate());

        assertEquals(originalQuiz.getQuestions().size(), quizFromDB.getQuestions().size());

        // Questions

        Question originalQuestion;
        Question questionFromDB;
        Answer originalAnswer;
        Answer AnswerFromDB;

        // Question 1
        originalQuestion = originalQuiz.getQuestions().get(0);
        questionFromDB = quizFromDB.getQuestions().get(0);
        assertEquals(originalQuestion.getContent(), questionFromDB.getContent());
        assertEquals(originalQuestion.getScore(), questionFromDB.getScore());
        assertEquals(originalQuestion.getAnswers().size(), questionFromDB.getAnswers().size());
        // Answer 1
        originalAnswer = originalQuestion.getAnswers().get(0);
        AnswerFromDB = questionFromDB.getAnswers().get(0);
        assertEquals(originalAnswer.getContent(), AnswerFromDB.getContent());
        assertEquals(originalAnswer.isCorrect(), AnswerFromDB.isCorrect());
        // Answer 2
        originalAnswer = originalQuestion.getAnswers().get(1);
        AnswerFromDB = questionFromDB.getAnswers().get(1);
        assertEquals(originalAnswer.getContent(), AnswerFromDB.getContent());
        assertEquals(originalAnswer.isCorrect(), AnswerFromDB.isCorrect());

        // Question 2
        originalQuestion = originalQuiz.getQuestions().get(1);
        questionFromDB = quizFromDB.getQuestions().get(1);
        assertEquals(originalQuestion.getContent(), questionFromDB.getContent());
        assertEquals(originalQuestion.getScore(), questionFromDB.getScore());
        assertEquals(originalQuestion.getAnswers().size(), questionFromDB.getAnswers().size());
        // Answer 1
        originalAnswer = originalQuestion.getAnswers().get(0);
        AnswerFromDB = questionFromDB.getAnswers().get(0);
        assertEquals(originalAnswer.getContent(), AnswerFromDB.getContent());
        assertEquals(originalAnswer.isCorrect(), AnswerFromDB.isCorrect());
        // Answer 2
        originalAnswer = originalQuestion.getAnswers().get(1);
        AnswerFromDB = questionFromDB.getAnswers().get(1);
        assertEquals(originalAnswer.getContent(), AnswerFromDB.getContent());
        assertEquals(originalAnswer.isCorrect(), AnswerFromDB.isCorrect());

    }

    @Test
    public void testStartQuizPassing() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        LocalDateTime time = LocalDateTime.of(2024, 3, 1, 10, 0);
        quizDAO.startQuizPassing("1", "1", time);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM results");
        assertTrue(resultSet.next());

        assertEquals(1, resultSet.getInt("user_id"));
        assertEquals(1, resultSet.getInt("quiz"));
        assertEquals(Timestamp.valueOf(time), resultSet.getTimestamp("start_time"));

        resultSet.close();
        statement.close();
        connection.close();
    }

    @Test
    public void testFinishQuizPassing() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        LocalDateTime startTime = LocalDateTime.of(2024, 3, 1, 10, 0);
        quizDAO.startQuizPassing("1", "1", startTime);

        LocalDateTime finishTime = LocalDateTime.of(2024, 3, 1, 10, 20);
        quizDAO.finishQuizPassing("1", "1", finishTime, 95.2f);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM results");
        assertTrue(resultSet.next());

        assertEquals(95.2f, resultSet.getFloat("score"));

        assertEquals(Timestamp.valueOf(startTime), resultSet.getTimestamp("start_time"));
        assertEquals(Timestamp.valueOf(finishTime), resultSet.getTimestamp("finish_time"));

        assertEquals(1, resultSet.getInt("user_id"));
        assertEquals(1, resultSet.getInt("quiz"));

        resultSet.close();
        statement.close();
        connection.close();

    }

    @Test
    public void testFinishNotExistingQuizPassing() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        LocalDateTime finishTime = LocalDateTime.of(2024, 3, 1, 10, 20);
        assertThrows(RuntimeException.class, () -> {
            quizDAO.finishQuizPassing("1", "1", finishTime, 95.2f);
        });
    }

    @Test
    public void testIsUserQuizPassing() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        String userId = "1";
        String quizId = "1";

        assertFalse(quizDAO.isUserPassingQuiz(userId, quizId));

        LocalDateTime startTime = LocalDateTime.of(2024, 3, 1, 10, 0);
        quizDAO.startQuizPassing(userId, quizId, startTime);

        assertTrue(quizDAO.isUserPassingQuiz(userId, quizId));

        LocalDateTime finishTime = LocalDateTime.of(2024, 3, 1, 10, 20);
        quizDAO.finishQuizPassing(userId, quizId, finishTime, 95.2f);

        assertFalse(quizDAO.isUserPassingQuiz(userId, quizId));
    }

    @Test
    void testGetQuizResultsOfUser() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        String userId = "1";

        // Preparing data
        LocalDateTime startTime1 = LocalDateTime.of(2024, 3, 1, 10, 0);
        LocalDateTime finishTime1 = LocalDateTime.of(2024, 3, 1, 10, 20);
        String quizId1 = "1";
        float score1 = 95.2f;

        LocalDateTime startTime2 = LocalDateTime.of(2024, 3, 4, 10, 0);
        LocalDateTime finishTime2 = LocalDateTime.of(2024, 3, 4, 10, 20);
        String quizId2 = "2";
        float score2 = 30f;

        // Saving to db
        quizDAO.startQuizPassing(userId, quizId1, startTime1);
        quizDAO.finishQuizPassing(userId, quizId1, finishTime1, score1);
        quizDAO.startQuizPassing(userId, quizId2, startTime2);
        quizDAO.finishQuizPassing(userId, quizId2, finishTime2, score2);

        // Extracting from db
        List<QuizResultData> QuizResults = quizDAO.getQuizResultsOfUser(userId);

        // Checking size
        assertEquals(2, QuizResults.size());

        // Checking quiz result 1
        QuizResultData firstResult = QuizResults.getFirst();

        assertEquals(score1, firstResult.getScore());
        assertEquals(startTime1, firstResult.getStartTime());
        assertEquals(finishTime1, firstResult.getFinishTime());
        assertEquals(userId, firstResult.getUserId());
        assertEquals(quizId1, firstResult.getQuizId());

        // Checking quiz result 1
        QuizResultData secondResult = QuizResults.get(1);

        assertEquals(score2, secondResult.getScore());
        assertEquals(startTime2, secondResult.getStartTime());
        assertEquals(finishTime2, secondResult.getFinishTime());
        assertEquals(userId, secondResult.getUserId());
        assertEquals(quizId2, secondResult.getQuizId());

    }

    @Test
    public void testGetQuizResultsByQuizId() {
        QuizDAO quizDAO = new QuizDAOPostgres(dataSource);

        String quizId = "1";

        // Preparing data
        LocalDateTime startTime1 = LocalDateTime.of(2024, 3, 1, 10, 0);
        LocalDateTime finishTime1 = LocalDateTime.of(2024, 3, 1, 10, 20);
        String userId1 = "1";
        float score1 = 95.2f;

        LocalDateTime startTime2 = LocalDateTime.of(2024, 3, 4, 10, 0);
        LocalDateTime finishTime2 = LocalDateTime.of(2024, 3, 4, 10, 20);
        String userId2 = "2";
        float score2 = 30f;

        // Saving to db
        quizDAO.startQuizPassing(userId1, quizId, startTime1);
        quizDAO.finishQuizPassing(userId1, quizId, finishTime1, score1);
        quizDAO.startQuizPassing(userId2, quizId, startTime2);
        quizDAO.finishQuizPassing(userId2, quizId, finishTime2, score2);

        // Extracting from db
        List<QuizResultData> QuizResults = quizDAO.getQuizResultsByQuizId(quizId);

        // Checking size
        assertEquals(2, QuizResults.size());

        // Checking quiz result 1
        QuizResultData firstResult = QuizResults.getFirst();

        assertEquals(score1, firstResult.getScore());
        assertEquals(startTime1, firstResult.getStartTime());
        assertEquals(finishTime1, firstResult.getFinishTime());
        assertEquals(userId1, firstResult.getUserId());
        assertEquals(quizId, firstResult.getQuizId());

        // Checking quiz result 1
        QuizResultData secondResult = QuizResults.get(1);

        assertEquals(score2, secondResult.getScore());
        assertEquals(startTime2, secondResult.getStartTime());
        assertEquals(finishTime2, secondResult.getFinishTime());
        assertEquals(userId2, secondResult.getUserId());
        assertEquals(quizId, secondResult.getQuizId());
    }
}