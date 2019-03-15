package com.hans.domain;

public class Order {
    private String address1;
    private String address2;
    private String description;

    public Order(String address1, String address2, String description) {
        this.address1 = address1;
        this.address2 = address2;
        this.description = description;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
