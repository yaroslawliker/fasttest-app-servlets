package org.yarek.fasttestapp.model.database.entities;

import java.time.LocalDateTime;

public class QuizResultData {
    private String Id;
    private float score;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String userId;
    private String quizId;

    public String getId() { return Id; }
    public void setId(String id) { Id = id; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }
}
