package com.hans.domain;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


public class Order {

    private Integer id;

    private OrderStatus orderStatus;

    private Map<String,Object> pickupAddress=new HashMap<>();

    private Map<String,Object> deliveryAddress=new HashMap<>();

    private Double length;

    private Double price;

    private Double weight;

    private Map<String,Object> dimensions=new HashMap<>();

    private String measurements;

    private String description;

    private String clientId;

    private String delivererId;



    public Order(Integer id, OrderStatus orderStatus, Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double length, Double price, Double weight, Map<String, Object> dimensions, String measurements, String description, String clientId) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.length = length;
        this.price = price;
        this.weight = weight;
        this.dimensions = dimensions;
        this.measurements = measurements;
        this.description = description;
        this.clientId = clientId;
        this.delivererId = "-";

    }

    public Order(Integer id, OrderStatus orderStatus, Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double length, Double price, Double weight, Map<String, Object> dimensions, String measurements, String description, String clientId, String delivererId) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.length = length;
        this.price = price;
        this.weight = weight;
        this.dimensions = dimensions;
        this.measurements = measurements;
        this.description = description;
        this.clientId = clientId;
        this.delivererId = delivererId;
    }

    public Order(Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double price, Double weight, String measurements, String description) {
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.weight = weight;
        this.measurements = measurements;
        this.description = description;
    }

    public Order() {
    }

    public Double getLength() { return length; }

    public Map<String, Object> getDimensions() { return dimensions; }

    public String getClientId() { return clientId; }

    public String getDelivererId() { return delivererId; }

    public Integer getId() {
        return id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Map<String, Object> getPickupAddress() { return pickupAddress; }

    public Map<String, Object> getDeliveryAddress() { return deliveryAddress; }

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
    public void setDelivererId(String delivererId) { this.delivererId = delivererId; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderStatus=" + orderStatus +
                ", pickupAddress='" + pickupAddress + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                ", length=" + length +
                ", price=" + price +
                ", weight=" + weight +
                ", dimensions=" + dimensions +
                ", measurements='" + measurements + '\'' +
                ", description='" + description + '\'' +
                ", clientId='" + clientId + '\'' +
                ", delivererId='" + delivererId + '\'' +
                '}';
    }

    public String toJSON()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String orderJSON = null;
        try
        {
            orderJSON = objectMapper.writeValueAsString(this);

        }
        catch (Exception e)
        {
            Log.d("exception", e.getMessage());
        }
        return orderJSON;
    }

    public static Order createFromJSON(String userJSON)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Order order = null;
        try
        {
            order = objectMapper.readValue(userJSON, Order.class);
        }
        catch (Exception e)
        {
            Log.d("exception", e.getMessage());
        }
        return order;
    }
}
