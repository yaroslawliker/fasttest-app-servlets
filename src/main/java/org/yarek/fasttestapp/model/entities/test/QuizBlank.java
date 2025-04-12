package org.yarek.fasttestapp.model.entities.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a filled up quiz, ready to be checked
 * Must be created from an existing quiz. Can be checked against it
 */
public class QuizBlank {

    Quiz quiz;
    private List<AnsweredQuestion> answeredQuestionS;

    public QuizBlank(Quiz quiz) {

        this.quiz = quiz;

        answeredQuestionS = new ArrayList<AnsweredQuestion>();

        for (Question question : quiz.getQuestions()) {
            int answerAmount = question.getAnswers().size();
            AnsweredQuestion answeredQuestion = new AnsweredQuestion(answerAmount);
            answeredQuestionS.add(answeredQuestion);
        }
    }

    public void registerAnswer(int questionIndex, int answerIndex) {
        answeredQuestionS.get(questionIndex).chooseAnswer(answerIndex);
    }

    public boolean checkAnswer(int questionIndex, int answerIndex) {
        return answeredQuestionS.get(questionIndex).isChoice(answerIndex);
    }

    /**
     * Gets scored array, which contains scores for each question.
     * Returns formula
     *      score = (correct - wrong) / total_correct * maxScore
     *  or 0, if score < 0
     *
     */
    public float[] getScoredArray() {
        float[] scoredArray = new float[answeredQuestionS.size()];

        // Check every answer
        for (int i = 0; i < answeredQuestionS.size(); i++) {
            AnsweredQuestion answeredQuestion = answeredQuestionS.get(i);
            int correctlyMarkedAnswers = 0;
            int totalCorrectAnswers = 0;
            for (int j = 0; j < answeredQuestion.getAnswerCount(); j++) {
                boolean isChecked  = answeredQuestion.isChoice(j);
                boolean isCorrect = quiz.getQuestions().get(i).getAnswers().get(j).isCorrect();

                if (isCorrect) {
                    totalCorrectAnswers++;
                }

                if (isChecked) {
                    if (isCorrect) {
                        correctlyMarkedAnswers++;
                    } else {
                        correctlyMarkedAnswers--;
                    }
                }
            }
            float gainedScore = (float) correctlyMarkedAnswers / totalCorrectAnswers * quiz.getQuestions().get(i).getScore();
            if (gainedScore < 0) {
                gainedScore = 0;
            }
            scoredArray[i] = gainedScore;
        }
        return scoredArray;
    }

    /**
     * Gets an array, which contains maximum scores for all questions
      */
    public float[] getMaxScoreArray() {
        float[] maxScoreArray = new float[quiz.getQuestions().size()];
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            maxScoreArray[i] = quiz.getQuestions().get(i).getScore();
        }
        return maxScoreArray;
    }

    private static class AnsweredQuestion {
        boolean[] answered;
        AnsweredQuestion(int answerAmount) {
            answered = new boolean[answerAmount];
        }

        void chooseAnswer(int number) {
            answered[number] = true;
        }

        boolean isChoice(int number) {
            return answered[number];
        }

        int getAnswerCount() {
            return answered.length;
        }
    }
}
