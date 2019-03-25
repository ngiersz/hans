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
import com.hans.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class databaseFirebase {
    // Write a message to the database

    FirebaseFirestore db;
    private TaskCompletionSource<ArrayList<Order>> dbSource = new TaskCompletionSource<>();
    private Task dbTask = dbSource.getTask();




    public databaseFirebase(){
        db = FirebaseFirestore.getInstance();

    }


    public void insertUserToDatabase(User user){
        Map<String,Object> userInsert = new HashMap<>();
        userInsert.put("googleId",user.getGoogleId());
        userInsert.put("googleEmail",user.getGoogleEmail());
        userInsert.put("name",user.getName());
        userInsert.put("surname",user.getSurname());
        userInsert.put("phoneNumber",user.getPhoneNumber());

        db.collection("Users")
                .add(userInsert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"Order documentSnapshood added with ID: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Order document",e);
                    }
                });

    }

    public void insertOrderToDatabase(Order order){
        Map<String,Object> orderInsert = new HashMap<>();
        orderInsert.put("id",order.getId());
        orderInsert.put("orderStatus",order.getOrderStatus());
        orderInsert.put("pickupAddress",order.getPickupAddress());
        orderInsert.put("deliveryAddress",order.getDeliveryAddress());
        orderInsert.put("price",order.getWeight());
        orderInsert.put("weight",order.getWeight());
        orderInsert.put("description",order.getDescription());
        orderInsert.put("clientId",order.getClientId());
        orderInsert.put("delivererId",order.getDelivererId());
        orderInsert.put("length",order.getLength());
        orderInsert.put("dimensions",order.getDimensions());

        Log.d("addOrder", "przed db.collections");
        Log.d("addOrder", orderInsert.toString());

        db.collection("Orders")
                .add(orderInsert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"Order documentSnapshood added with ID: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Order document",e);
                    }
                });
    }

    public void deleteOrderByID(Order order){
        db.collection("Orders").document(order.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Error deleting document!",e);

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

    public Task getAllOrdersForClient(String googleId) {
        return db.collection("Orders")
                .whereEqualTo("clientId", googleId)
                .get();
    }

    public TaskCompletionSource<ArrayList<Order>> getDbSource() {
        return dbSource;
    }

    public Task getDbTask() {
        return dbTask;
    }

}

