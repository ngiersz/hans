package com.hans;

import java.util.ArrayList;

public abstract class User {
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
}
