package com.example.wemeet.classes;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class User {
    private String userID;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String profilePictureURL;
    private String token;
    private List<DocumentReference> friendList;
    private HashMap<String, List<Date>> registrations;

    public User() {
    }

    public HashMap<String, List<Date>> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(HashMap<String, List<Date>> registrations) {
        this.registrations = registrations;
    }

    public List<DocumentReference> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<DocumentReference> friendList) {
        this.friendList = friendList;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
