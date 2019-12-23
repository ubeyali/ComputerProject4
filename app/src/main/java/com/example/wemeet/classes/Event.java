package com.example.wemeet.classes;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Event {
    private String title;
    private String location;
    private String eventID;
    private List<Date> dateTimeList;
    private HashMap<String, List<String>> registrations;
    private DocumentReference owner;
    private int invitedCount;
    private int messageCount;


    public Event(){}

    public HashMap<String, List<String>> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(HashMap<String, List<String>> registrations) {
        this.registrations = registrations;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    public void setOwner(DocumentReference owner) {
        this.owner = owner;
    }

    public int getInvitedCount() {
        return invitedCount;
    }

    public void setInvitedCount(int invitedCount) {
        this.invitedCount = invitedCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Date> getDateTimeList() {
        return dateTimeList;
    }

    public void setDateTimeList(List<Date> dateTimeList) {
        this.dateTimeList = dateTimeList;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}
