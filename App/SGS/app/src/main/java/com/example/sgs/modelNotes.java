package com.example.sgs;

import com.google.firebase.firestore.DocumentReference;

public class modelNotes {

    private String name;
    private String containt;
    private DocumentReference docRef;
    private String userId;

    public modelNotes(String name, String containt, DocumentReference docRef, String userId) {
        this.name = name;
        this.containt = containt;
        this.docRef = docRef;
        this.userId = userId;
    }

    public modelNotes() {
    }

    public modelNotes(String name, String containt) {
        this.name = name;
        this.containt = containt;
    }

    public modelNotes(String name, String containt, DocumentReference docRef) {
        this.name = name;
        this.containt = containt;
        this.docRef = docRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public DocumentReference getDocRef() {
        return docRef;
    }

    public void setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContaint() {
        return containt;
    }

    public void setContaint(String containt) {
        this.containt = containt;
    }
}
