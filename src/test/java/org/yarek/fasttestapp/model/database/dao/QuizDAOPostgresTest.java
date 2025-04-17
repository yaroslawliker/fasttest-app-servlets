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
import org.yarek.fasttestapp.model.entities.quiz.Answer;
import org.yarek.fasttestapp.model.entities.quiz.Question;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.entities.quiz.QuizPreview;

import java.sql.*;
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

        QuizDAOPostgres.setDataSource(dataSource);
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
        PostgresScripts.clearTestSchema();
    }

    @Test
    public void testGetQuizPreviews() throws SQLException {

        // Testing results
        QuizDAO quizDAO = new QuizDAOPostgres();
        List<QuizPreview> previews = quizDAO.getQuizPreviews(2);

        assertEquals(2, previews.size());

        assertEquals("My quiz3", previews.get(0).getName());
        assertEquals("This is my quiz3", previews.get(0).getDescription());
        assertEquals(Date.valueOf("2024-03-30"), previews.get(0).getCreationDate());

        assertEquals("My quiz2", previews.get(1).getName());
        assertEquals("This is my quiz2", previews.get(1).getDescription());
        assertEquals(Date.valueOf("2024-02-20"), previews.get(1).getCreationDate());
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

        QuizDAO quizDAO = new QuizDAOPostgres();
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
        QuizDAO quizDAO = new QuizDAOPostgres();

        // Setting up quiz
        Quiz originalQuiz = new Quiz();
        originalQuiz.setName("My quiz");
        originalQuiz.setDescription("This is my quiz");
        originalQuiz.setCreationDate(Date.valueOf("2024-03-30"));
        originalQuiz.setOwnerUsername("exampleuser1");

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
    public void testRegisterQuizPassed() throws SQLException {
        QuizDAO quizDAO = new QuizDAOPostgres();

        quizDAO.registerQuizPassed("exampleuser1", "1", 3);

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM results");
        assertTrue(resultSet.next());

        int userId = resultSet.getInt("user_id");
        String username = ((QuizDAOPostgres) quizDAO).getOwner(String.valueOf(userId));

        assertEquals("exampleuser1", username);
        assertEquals(1, resultSet.getInt("id"));
        assertEquals(3, resultSet.getInt("score"));
    }
}