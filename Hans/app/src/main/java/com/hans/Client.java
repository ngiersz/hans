package com.hans;

import java.util.ArrayList;

public class Client extends User {
    private int _clientId;
    private ArrayList<Order> _Orders;

    public Client(int clientId){
        super();
        this._clientId=clientId;
        CompleteUserData();

    }
    public Client(String name,String surName,String gender, int age){
        super(name,surName,gender,age);
        this._clientId=GetNewID();
    }
    private int GetNewID(){
        return 2;
    }

    private void CompleteUserData(){
        //when we ge database we need to find this user by id and set all values for the class;
    }

    private void AddOrder(){
        //adding order to the array and inserting in to the database
    }
}
