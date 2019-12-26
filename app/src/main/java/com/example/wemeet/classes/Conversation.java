package com.example.wemeet.classes;

import java.util.ArrayList;

public class Conversation {

    private ArrayList<Message> messages;
    private String conversationID;

    public Conversation() {
        messages = new ArrayList<>();
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }
}
