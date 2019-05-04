package com.hans;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hans.domain.Order;
import com.hans.domain.OrderStatus;
import com.hans.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/*
    =/\                 /\=
    / \'._   (\_/)   _.'/ \
   / .''._'--(o.o)--'_.''. \
  /.' _/ |`'=/ " \='`| \_ `.\
 /` .' `\;-,'\___/',-;/` '. '\
/.-'       `\(-V-)/`       `-.\
`            "   "
 */
public class DatabaseFirebase {
    // Write a message to the database

    FirebaseFirestore db;
    private TaskCompletionSource<ArrayList<Order>> dbSource = new TaskCompletionSource<>();
    private Task dbTask = dbSource.getTask();


    public DatabaseFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    public void insertUserToDatabase(User user) {
        Map<String, Object> userInsert = new HashMap<>();
        userInsert.put("googleId", user.getGoogleId());
        userInsert.put("googleEmail", user.getGoogleEmail());
        userInsert.put("name", user.getName());
        userInsert.put("surname", user.getSurname());
        userInsert.put("phoneNumber", user.getPhoneNumber());

        db.collection("Users")
                .add(userInsert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Order documentSnapshood added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Order document", e);
                    }
                });
    }

    public void insertOrderToDatabase(Order order) {
        Map<String, Object> orderInsert = new HashMap<>();
        orderInsert.put("id", order.getId());
        orderInsert.put("orderStatus", order.getOrderStatus());
        orderInsert.put("pickupAddress", order.getPickupAddress());
        orderInsert.put("deliveryAddress", order.getDeliveryAddress());
        orderInsert.put("price", order.getPrice());
        orderInsert.put("weight", order.getWeight());
        orderInsert.put("description", order.getDescription());
        orderInsert.put("clientId", order.getClientId());
        orderInsert.put("delivererId", order.getDelivererId());
        orderInsert.put("length", order.getLength());
        orderInsert.put("dimensions", order.getDimensions());

        Log.d("addOrder", "przed db.collections");
        Log.d("addOrder", orderInsert.toString());

        db.collection("Orders")
                .add(orderInsert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Order documentSnapshood added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Order document", e);
                    }
                });
    }

    public void deleteOrderByID(Order order) {
        db.collection("Orders").document(order.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error deleting document!", e);

                    }
                });
    }

    public void setOrder(Order order) {
        Map<String, Object> orderSet = new HashMap<>();
        orderSet.put("id", order.getId());
        orderSet.put("orderStatus", order.getOrderStatus());
        orderSet.put("pickupAddress", order.getPickupAddress());
        orderSet.put("deliveryAddress", order.getDeliveryAddress());
        orderSet.put("price", order.getPrice());
        orderSet.put("weight", order.getWeight());
        orderSet.put("description", order.getDescription());
        orderSet.put("clientId", order.getClientId());
        orderSet.put("delivererId", order.getDelivererId());
        orderSet.put("length", order.getLength());
        orderSet.put("dimensions", order.getDimensions());

        db.collection("Orders").document(order.getId())
                .set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error deleting document!", e);

                    }
                });
    }

    public Task getAllOrdersTask() {
        return db.collection("Orders")
                .get();
    }

    public Task getAllOrdersForDelivererTask() {
        return db.collection("Orders")
                .whereEqualTo("orderStatus", "WAITING_FOR_DELIVERER")
                .get();
    }
    public Task getInTransitOrdersForDeliverer(String googleId) {
        return db.collection("Orders")
                .whereEqualTo("delivererId", googleId)
                .whereEqualTo("orderStatus","IN_TRANSIT")
                .get();
    }
    public Task getClosedOrdersForDeliverer(String googleID){
        return db.collection("Orders")
                .whereEqualTo("delivererID",googleID)
                .whereEqualTo("orderStatus","CLOSED")
                .get();
    }
    public Task getAllOrdersForClient(String googleId) {
        return db.collection("Orders")
                .whereEqualTo("clientId", googleId)
                .get();
    }

    public Task getAllWaitingOrdersForClient(String googleId) {
        return db.collection("Orders")
                .whereEqualTo("clientId", googleId)
                .whereEqualTo("orderStatus", "WAITING_FOR_DELIVERER")
                .get();
    }

    public Task getInTransitOrdersForClient(String googleId) {
        return db.collection("Orders")
                .whereEqualTo("clientId", googleId)
                .whereEqualTo("orderStatus", "IN_TRANSIT")
                .get();
    }
    public Task getClosedOrdersForClient(String googleID){
        return db.collection("Orders")
                .whereEqualTo("clientId",googleID)
                .whereEqualTo("orderStatus","CLOSED")
                .get();
    }
    public Task getUser(String googleId){
        return db.collection("Users")
                .whereEqualTo("googleId", googleId)
                .get();
    }
    public Task getAllUsers(){
        return db.collection("Users")
                .get();
    }

    public TaskCompletionSource<ArrayList<Order>> getDbSource() {
        return dbSource;
    }

    public Task getDbTask() {
        return dbTask;
    }

}

