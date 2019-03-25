package com.hans.domain;

import java.util.ArrayList;

public class ClientNotUsed extends User
{
    private int _clientId;
    private ArrayList<Order> _Orders;

    public ClientNotUsed(int clientId)
    {
        super();
        this._clientId = clientId;
        CompleteUserData();

    }

    public ClientNotUsed(String name, String surName, String gender, int age)
    {
        super(name, surName, gender, age);
        this._clientId = GetNewID();
    }

    private int GetNewID()
    {
        return 2;
    }

    private void CompleteUserData()
    {
        //when we ge database we need to find this user by id and set all values for the class;
    }

    private void AddOrder()
    {
        //adding order to the array and inserting in to the database
    }

    public int get_clientId()
    {
        return this._clientId;
    }
}
