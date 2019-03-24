package com.hans;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

    public void inserUserToDatabase(User user){
        Map<String,Object> userInsert = new HashMap<>();
        userInsert.put("_googleId",user.getGoogleId());
        userInsert.put("_googleEmail",user.getGoogleEmail());
        userInsert.put("_name",user.getName());
        userInsert.put("_surname",user.getSurname());
        userInsert.put("_age",user.getAge());
        userInsert.put("_gender",user.getGender());




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
        orderInsert.put("measurements",order.getMeasurements());
        orderInsert.put("description",order.getDescription());
        orderInsert.put("clientId",order.getClientId());
        orderInsert.put("delivererId",order.getDelivererId());
        orderInsert.put("length",order.getLength());
        orderInsert.put("dimensions",order.getDimensions());





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


public ArrayList<Order> getAllOrdersToDeliver(){
    final ArrayList<Order> orderList= new ArrayList<>();
    readOrdersAll(new FirestoreCallback() {
        @Override
        public  ArrayList<Order> onCallback(ArrayList<Order> orderList) {
            for (Order order:orderList
                 ) {  Log.d("############Order", order.toString());


            }
            return orderList;

        }
    });
    return orderList;
    }
private void readOrdersAll(final FirestoreCallback firestoreCallback){
    final ArrayList<Order> orderList= new ArrayList<>();

    db.collection("Orders")
            .whereEqualTo("orderStatus", "IN_TRANSIT")
            .get();
            /*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orderList.add(document.toObject(Order.class));
                            Log.d("Order", document.toObject(Order.class).toString());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        firestoreCallback.onCallback(orderList);

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });*/
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

    private interface  FirestoreCallback{
    ArrayList<Order> onCallback( ArrayList<Order> orderList );

}
}

