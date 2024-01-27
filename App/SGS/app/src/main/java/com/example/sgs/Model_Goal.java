package com.example.sgs;

import com.google.firebase.firestore.DocumentReference;

public class Model_Goal {
    private String goal;
    private String date;
    private String description;
    private DocumentReference docRef;

    public Model_Goal() {
    }

    public Model_Goal(String goal, String date, String description) {
        this.goal = goal;
        this.date = date;
        this.description = description;
    }

    public Model_Goal(String goal, String date, String description, DocumentReference docRef) {
        this.goal = goal;
        this.date = date;
        this.description = description;
        this.docRef = docRef;

    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DocumentReference getDocRef() {
        return docRef;
    }

    public void setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
    }
}
