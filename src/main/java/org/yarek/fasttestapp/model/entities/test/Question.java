package org.yarek.fasttestapp.model.entities.test;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String content;
    private int score;
    private List<Answer> answers;

    public Question() {
        content = "Question";
        score = 1;
        answers = new ArrayList<Answer>();
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
    public void addAnswer(Answer answer) { answers.add(answer); }
}
