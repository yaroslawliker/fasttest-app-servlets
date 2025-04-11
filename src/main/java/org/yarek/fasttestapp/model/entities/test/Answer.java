package org.yarek.fasttestapp.model.entities.test;

public class Answer {
    private boolean isCorrect;
    private String content;

    public Answer() {
        isCorrect = false;
        content = "Answer";
    }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
