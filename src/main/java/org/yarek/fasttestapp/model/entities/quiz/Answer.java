package org.yarek.fasttestapp.model.entities.quiz;

public class Answer {
    private boolean isCorrect;
    private String content;

    public Answer() {
        isCorrect = false;
        content = "Answer";
    }

    public Answer(String content, boolean isCorrect) {
        setContent(content);
        setCorrect(isCorrect);
    }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
