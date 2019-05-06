package com.hans.domain;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;


public class Order {

    private String id;

    private OrderStatus orderStatus;

    private Map<String,Object> pickupAddress=new HashMap<>();

    private Map<String,Object> deliveryAddress=new HashMap<>();

    private Double length;

    private Double price;

    private Double weight;

    private Map<String,Object> dimensions=new HashMap<>();


    private String description;

    private String clientId;

    private String delivererId;

    private Timestamp date;

    private boolean isPaid;

    public Order() {
    }

    public Order(Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double length, Double price, Double weight, Map<String, Object> dimensions, String description, String clientId, Timestamp date) {
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.length = length;
        this.price = price;
        this.weight = weight;
        this.dimensions = dimensions;
        this.description = description;
        this.clientId = clientId;
        this.orderStatus = OrderStatus.WAITING_FOR_DELIVERER;
        this.date = date;
        this.isPaid = false;
    }

//    public Order(String id, OrderStatus orderStatus, Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double length, Double price, Double weight, Map<String, Object> dimensions, String description, String clientId) {
//        this.id = id;
//        this.orderStatus = orderStatus;
//        this.pickupAddress = pickupAddress;
//        this.deliveryAddress = deliveryAddress;
//        this.length = length;
//        this.price = price;
//        this.weight = weight;
//        this.dimensions = dimensions;
//        this.description = description;
//        this.clientId = clientId;
//        this.delivererId = "-";
//
//    }

//    public Order(String id, OrderStatus orderStatus, Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double length, Double price, Double weight, Map<String, Object> dimensions,  String description, String clientId, String delivererId) {
//        this.id = id;
//        this.orderStatus = orderStatus;
//        this.pickupAddress = pickupAddress;
//        this.deliveryAddress = deliveryAddress;
//        this.length = length;
//        this.price = price;
//        this.weight = weight;
//        this.dimensions = dimensions;
//        this.description = description;
//        this.clientId = clientId;
//        this.delivererId = delivererId;
//    }

    public Order(Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double price, Double weight, String description, Timestamp date) {
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.weight = weight;
        this.description = description;
        this.date = date;
        this.isPaid = false;

    }

    public Order(String id, OrderStatus orderStatus, Map<String, Object> pickupAddress, Map<String, Object> deliveryAddress, Double length, Double price, Double weight, Map<String, Object> dimensions, String description, String clientId, String delivererId, Timestamp date) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.length = length;
        this.price = price;
        this.weight = weight;
        this.dimensions = dimensions;
        this.description = description;
        this.clientId = clientId;
        this.delivererId = delivererId;
        this.date = date;
        this.isPaid = false;

    }

    public Double getLength() { return length; }

    public Map<String, Object> getDimensions() { return dimensions; }

    public String getClientId() { return clientId; }

    public String getDelivererId() { return delivererId; }

    public String getId() {
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

    public String getDescription() {
        return description;
    }

    public Timestamp getDate() {
        return date;
    }

    public boolean getIsPaid() { return isPaid; }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) { this.description = description; }

    public void setDelivererId(String delivererId) { this.delivererId = delivererId; }

    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public void setIsPaid(boolean paid) { isPaid = paid; }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", orderStatus=" + orderStatus +
                ", pickupAddress=" + pickupAddress +
                ", deliveryAddress=" + deliveryAddress +
                ", length=" + length +
                ", price=" + price +
                ", weight=" + weight +
                ", dimensions=" + dimensions +
                ", description='" + description + '\'' +
                ", clientId='" + clientId + '\'' +
                ", delivererId='" + delivererId + '\'' +
                ", date=" + date +
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
        userJSON = userJSON.replaceAll(",\"date\":\\{[^}]*\\}", "");
        Log.d("json", "replaced:\n" + userJSON);
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
