package com.hans.domain;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.util.ArrayList;

public class User {

    private String _googleEmail;
    private String _googleId;
    private String _name;
    private String _surname;
    private String _gender;
    private int _age;


    public User(){}
    public User(String name,String surname, String gender, int age){
        this._name=name;
        this._surname=surname;
        this._gender=gender;
        this._age=age;
    }


    public void changeGoogleEmail(String newGoogleEmail){
        this._googleEmail=newGoogleEmail;
    }
    public void changeGoogleID(String newGoogleID){
        this._googleId=newGoogleID;
    }
    public void changeName(String newName){
        this._name=newName;
    }
    public void changeSurName(String newSurName){
        this._name=newSurName;
    }
    public void changeAge(String newAge){
        this._name=newAge;
    }
    public void changeGender(String newGender){
        this._name=newGender;
    }


    public String getGoogleEmail(){return this._googleEmail;}
    public String getGoogleId(){return  this._googleId;}
    public String getName(){return this._name;}
    public String getSurname(){return this._surname;}
    public String getGender(){return this._gender;}
    public int getAge(){return this._age;}

    @Override
    public String toString() {
        return "User{" +
                "_googleEmail='" + _googleEmail + '\'' +
                ", _googleId='" + _googleId + '\'' +
                ", _name='" + _name + '\'' +
                ", _surname='" + _surname + '\'' +
                ", _gender='" + _gender + '\'' +
                ", _age=" + _age +
                '}';
    }
    public String toJSON()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String userJSON = null;
        try
        {
            userJSON = objectMapper.writeValueAsString(this);

        }
        catch (Exception e)
        {
            Log.d("exception", e.getMessage());
        }
        return userJSON;
    }

    public static User createFromJSON(String userJSON)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        try
        {
            user = objectMapper.readValue(userJSON, User.class);
        }
        catch (Exception e)
        {
            Log.d("exception", e.getMessage());
        }
        return user;
    }



}
