package com.example.wemeet.classes;

import com.google.firebase.firestore.DocumentReference;

public class Message {

    private DocumentReference senderRef;
    private String content;

    public Message() {
    }

    public DocumentReference getSenderRef() {
        return senderRef;
    }

    public void setSenderRef(DocumentReference senderRef) {
        this.senderRef = senderRef;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
