package com.hans.domain;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

public class User {

    private String googleEmail;
    private String googleId;
    private String name;
    private String surname;
    private String phoneNumber;

    public User(String googleEmail, String googleId, String name, String surname) {
        this.googleEmail = googleEmail;
        this.googleId = googleId;
        this.name = name;
        this.surname = surname;
    }

    public User() {
    }

    public User(String name, String surname, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }


    public void setGoogleEmail(String newGoogleEmail) {
        this.googleEmail = newGoogleEmail;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setSurname(String newSurname) {
        this.name = newSurname;
    }

    public void setAge(String newAge) {
        this.name = newAge;
    }

    public void setGender(String newGender) {
        this.name = newGender;
    }


    public String getGoogleEmail() {
        return this.googleEmail;
    }

    public String getGoogleId() {
        return this.googleId;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "googleEmail='" + googleEmail + '\'' +
                ", googleId='" + googleId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneNumber=" + phoneNumber +
                '}';
    }

    public String toJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        String userJSON = null;
        try {
            userJSON = objectMapper.writeValueAsString(this);

        } catch (Exception e) {
            Log.d("exception", e.getMessage());
        }
        return userJSON;
    }

    public static User createFromJSON(String userJSON) {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        try {
            user = objectMapper.readValue(userJSON, User.class);

        } catch (Exception e) {
            Log.d("exception", e.getMessage());
        }
        return user;
    }


}
