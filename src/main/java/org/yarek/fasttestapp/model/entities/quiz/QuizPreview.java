package org.yarek.fasttestapp.model.entities.quiz;

import java.util.Date;

public class QuizPreview {

    private final String id;
    private final String ownerID;
    private final String name;
    private final String description;
    private final Date creationDate;

    public QuizPreview(String id, String ownerID, String name, String description, Date creationDate) {
        this.id = id;
        this.ownerID = ownerID;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }
    public String getOwnerID() {
        return ownerID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Date getCreationDate() {
        return creationDate;
    }
}
