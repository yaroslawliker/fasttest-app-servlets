package org.yarek.fasttestapp.model.entities.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents a test (exam). It consists of   questions with possible answers.
 *
 */

public class Test {

    private String id;
    private String ownerUsername;

    private String name;
    private String description;

    private List<Question> questions;

    public Test() {
        id = null;
        ownerUsername = null;

        name = "Test";
        description = "Test description";

        questions = new ArrayList<Question>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public void addQuestion(Question question) { questions.add(question); }

    /**
     * Checks if test is a representation of real saved in DB instance
     */
    boolean isMappedToDatabase() {
        return id != null && ownerUsername != null;
    }
}
