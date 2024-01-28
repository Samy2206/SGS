package com.example.sgs;

import android.net.Uri;

import com.google.firebase.firestore.DocumentReference;

public class ModelPhoto {

    private Uri imgUri;
    private String imgName;
    private DocumentReference docRef;
    private String userId;

    public ModelPhoto() {
    }

    public ModelPhoto(Uri imgUri, String imgName) {
        this.imgUri = imgUri;
        this.imgName = imgName;
    }

    public ModelPhoto(Uri imgUri, String imgName, DocumentReference docRef, String userId) {
        this.imgUri = imgUri;
        this.imgName = imgName;
        this.docRef = docRef;
        this.userId = userId;
    }

    public DocumentReference getDocRef() {
        return docRef;
    }

    public void setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
