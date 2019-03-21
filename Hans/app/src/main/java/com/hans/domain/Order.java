package com.hans.domain;

public class Order {

    Integer id;

    OrderStatus orderStatus;

    String pickupAddress;

    String deliveryAddress;

    Double price;

    Double weight;

    String measurements;

    String description;

    public Order(int id, String pickupAddress, String destinationAddress, Double weight, String measurements, String description, Double price) {
        this.id = id;
        this.orderStatus = OrderStatus.WAITING_FOR_DELIVERER;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = destinationAddress;
        this.weight = weight;
        this.measurements = measurements;
        this.description = description;
        this.price = price;
    }

    public Order(String pickupAddress, String deliveryAddress, String description, Double price, Double weight, String measurements) {
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.measurements = measurements;
    }

    public Order() {
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

    public String getMeasurements() {
        return measurements;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderStatus=" + orderStatus +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                ", measurements='" + measurements + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
