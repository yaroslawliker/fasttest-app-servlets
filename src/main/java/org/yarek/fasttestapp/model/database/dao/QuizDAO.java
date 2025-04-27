package org.yarek.fasttestapp.model.database.dao;

import org.yarek.fasttestapp.model.database.entities.QuizResultData;
import org.yarek.fasttestapp.model.entities.quiz.Quiz;
import org.yarek.fasttestapp.model.database.entities.QuizPreviewData;

import java.time.LocalDateTime;
import java.util.List;

public interface QuizDAO {
    List<QuizPreviewData> getQuizPreviews();
    List<QuizPreviewData> getQuizPreviews(int amount);
    List<QuizPreviewData> getQuizPreviewsOfAuthor(String authorId, int amount);

    Quiz getQuizById(String quizId);
    String saveNewQuiz(Quiz quiz);

    void startQuizPassing(String userID, String quizID, LocalDateTime startTime);
    void finishQuizPassing(String userID, String quizID, LocalDateTime endTime, float score);
    /**
     * Returns true if quiz started but not finished, false otherwise;
     */
    boolean isUserPassingQuiz(String userID, String quizID);

    List<QuizResultData> getQuizResultsOfUser(String userID);
    List<QuizResultData> getQuizResultsByQuizId(String quizID);

}
