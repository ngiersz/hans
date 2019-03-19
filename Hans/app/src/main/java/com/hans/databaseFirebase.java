package com.hans;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tag;

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

}
