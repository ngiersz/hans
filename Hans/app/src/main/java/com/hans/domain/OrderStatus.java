package com.hans.domain;

public enum OrderStatus {
    WAITING_FOR_DELIVERER ("Oczekiwanie na przyjęcie przez dostawcę"),
    IN_TRANSIT ("W drodze"),
    DELIVERED ("Dostarczone");

    private final String polishName;

    OrderStatus(String polishName) {
        this.polishName = polishName;
    }

    public String getPolishName(){
        return this.polishName;
    }
}
