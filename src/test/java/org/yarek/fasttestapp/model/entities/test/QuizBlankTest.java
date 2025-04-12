package org.yarek.fasttestapp.model.entities.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizBlankTest {

    Quiz quiz;
    QuizBlank quizBlank;

    @BeforeEach
    public void init() {
        quiz = new Quiz();

        Question question;

        question = new Question("Which pets are cool?");
        question.addAnswer(new Answer("Cats", false));
        question.addAnswer(new Answer("Dogs", false));
        question.addAnswer(new Answer("Frogs", false));
        question.addAnswer(new Answer("All of them", true));
        quiz.addQuestion(question);

        question = new Question("Which coffee drink is valid");
        question.setScore(3);
        question.addAnswer(new Answer("Americano", false));
        question.addAnswer(new Answer("Espresso", true));
        question.addAnswer(new Answer("Americano with milk", false));
        question.addAnswer(new Answer("Late", true));
        quiz.addQuestion(question);

        quizBlank = new QuizBlank(quiz);

    }

    @Test
    void registerAndCheckAnswer() {
        quizBlank.registerAnswer(0,3);

        assertTrue(quizBlank.checkAnswer(0, 3));
        assertFalse(quizBlank.checkAnswer(0, 0));
    }

    @Test
    void getMaxScoreArrayTest() {
        float[] expectedMaxScoreArray = {1,3};
        float[] actualMaxScoreArray = quizBlank.getMaxScoreArray();
        assertArrayEquals(expectedMaxScoreArray, actualMaxScoreArray);

    }

    @Test
    void checkAllCorrectScore() {
        quizBlank.registerAnswer(0,3);
        quizBlank.registerAnswer(1,1);
        quizBlank.registerAnswer(1,3);

        float[] exceptedResults = {1,3};
        float[] actualResults = quizBlank.getScoredArray();

        assertEquals(2, actualResults.length);
        assertArrayEquals(exceptedResults, actualResults);
    }

    @Test
    void checkEmptyQuizBlank() {

        float[] exceptedResults = {0,0};
        float[] actualResults = quizBlank.getScoredArray();

        assertEquals(2, actualResults.length);
        assertArrayEquals(exceptedResults, actualResults);
    }

    @Test void checkPartlyCorrectQuizBlank() {

        quizBlank.registerAnswer(1,1);

        float exceptedResult = 1.5f;
        float actualResults = quizBlank.getScoredArray()[1];

        assertEquals(exceptedResult, actualResults);
    }

    @Test void checkCorrectWrongAnswerBalance() {
        quizBlank.registerAnswer(1,1);
        quizBlank.registerAnswer(1,2);

        float exceptedResult = 0;
        float actualResults = quizBlank.getScoredArray()[1];
        assertEquals(exceptedResult, actualResults);
    }

    @Test void checkMoreWrongThanCorrectAnswers(){
        quizBlank.registerAnswer(1,0);
        quizBlank.registerAnswer(1,1);
        quizBlank.registerAnswer(1,2);

        float exceptedResult = 0;
        float actualResults = quizBlank.getScoredArray()[1];
        assertEquals(exceptedResult, actualResults);
    }

    @Test
    void getMaxScoreTest() {
        float expected = 1f+3f;
        float actual = quizBlank.getMaxScore();
        assertEquals(expected, actual);
    }

    @Test
    void getScoreTest() {
        quizBlank.registerAnswer(0,3);
        quizBlank.registerAnswer(1,1);

        float expected = 1f+1.5f;
        float actual = quizBlank.getScore();
        assertEquals(expected, actual);
    }
}