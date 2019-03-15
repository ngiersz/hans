package com.hans.domain;

public class Order {

    Integer id;

    OrderStatus orderStatus;

    String pickupAddress;

    String deliveryAddress;

    Double price;

    Double weight;

    String measurments;

    String description;

    public Order(int id, String pickupAddress, String destinationAddress, Double weight, String measurments, String description, Double price) {
        this.id = id;
        this.orderStatus = OrderStatus.WAITING_FOR_DELIVERER;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = destinationAddress;
        this.weight = weight;
        this.measurments = measurments;
        this.description = description;
        this.price = price;
    }

    public Order(String pickupAddress, String deliveryAddress, String description) {
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Double getPrice() {
        return price;
    }

    public Double getWeight() {
        return weight;
    }

    public String getMeasurments() {
        return measurments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
