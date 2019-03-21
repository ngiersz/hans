package com.hans;

import java.util.ArrayList;

public class Deliveryman extends User {
    private int _deliverymanId;
    private ArrayList<Order> _Orders;

    public Deliveryman(int clientId){
        super();
        this._deliverymanId=clientId;
        CompleteUserData();

    }
    public Deliveryman(String name,String surName,String gender, int age){
        super(name,surName,gender,age);
        this._deliverymanId=GetNewID();
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

    public int get_deliverymanId(){return this._deliverymanId;}
}
