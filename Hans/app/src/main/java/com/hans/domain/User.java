package com.hans.domain;

import java.util.ArrayList;

public abstract class User {

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

    public String toString(){
        return "Name="+this._name+"::Surname="+this._surname;
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


    public String get_googleEmail(){return this._googleEmail;}
    public String get_googleId(){return  this._googleId;}
    public String get_name(){return this._name;}
    public String get_surname(){return this._surname;}
    public String get_gender(){return this._gender;}
    public int get_age(){return this._age;}

}
