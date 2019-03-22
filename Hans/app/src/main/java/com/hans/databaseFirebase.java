package com.hans;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.domain.ClientNotUsed;
import com.hans.domain.DeliverymanNotUsed;
import com.hans.domain.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class databaseFirebase {
    // Write a message to the database

    FirebaseFirestore db;
    public databaseFirebase(){
        db = FirebaseFirestore.getInstance();

    }
    public void insertToDatabase(){
        Map<String,Object> user = new HashMap<>();
        user.put("first","Ada");
        user.put("last","Lovelace");
        user.put("born",1815);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"DocumentSnapshood added with ID: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document",e);
                    }
                });

    }
    public void insertClientToDatabase(ClientNotUsed client){
        Map<String,Object> clientInsert = new HashMap<>();
        clientInsert.put("googleID",client.get_googleId());
        clientInsert.put("googleEmail",client.get_googleEmail());
        clientInsert.put("clientID",client.get_clientId());
        clientInsert.put("name",client.get_name());
        clientInsert.put("surname",client.get_surname());
        clientInsert.put("gender",client.get_gender());
        clientInsert.put("age",client.get_age());

        db.collection("clients")
                .add(clientInsert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"Client documentSnapshood  added with ID: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Client document",e);
                    }
                });

    }
    public void insertDeliverymanToDatabase(DeliverymanNotUsed deliveryman){
        Map<String,Object> deliverymanInsert = new HashMap<>();
        deliverymanInsert.put("googleID",deliveryman.get_googleId());
        deliverymanInsert.put("googleEmail",deliveryman.get_googleEmail());
        deliverymanInsert.put("deliverymanID",deliveryman.get_deliverymanId());
        deliverymanInsert.put("name",deliveryman.get_name());
        deliverymanInsert.put("surname",deliveryman.get_surname());
        deliverymanInsert.put("gender",deliveryman.get_gender());
        deliverymanInsert.put("age",deliveryman.get_age());

        db.collection("Deliverymen")
                .add(deliverymanInsert)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"Deliveryman documentSnapshood added with ID: "+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding deliveryman document",e);
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
        orderInsert.put("measurements",order.getMeasurements());
        orderInsert.put("description",order.getDescription());




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

//    public Deliveryman getDeliveryAccount(String googleID){
//     return ;}
//    public Deliveryman getClientAccount(String googleID){
//     return ;}
public ArrayList<Order> getAllOrdersToDeliver(){
    final ArrayList<Order> orderList= new ArrayList<>();
    db.collection("Orders")
            .whereEqualTo("orderStatus", "WAITING_FOR_DELIVERER")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orderList.add(document.toObject(Order.class));
                            Log.d("Order", document.toObject(Order.class).toString());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
     return orderList;
    }
}
