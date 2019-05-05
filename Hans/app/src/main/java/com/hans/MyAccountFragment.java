package com.hans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.domain.User;

import static android.support.constraint.Constraints.TAG;

public class MyAccountFragment extends Fragment {

    DatabaseFirebase db = new DatabaseFirebase();
    View v;
    User user = new User();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        v = inflater.inflate(R.layout.fragment_my_account, container, false);

        ((MainActivity)getActivity()).setActionBarTitle("Twoje konto");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getAccount(firebaseUser.getUid());


        return v;
    }

    private void getAccount(String id){
        db.getUser(id).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user =document.toObject(User.class);
                        Log.d("User", document.toObject(User.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());

                    }
                    Fragment newFragment = new MyAccountCompleteFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, newFragment);

                    Bundle bundle = new Bundle();
                    bundle.putString("user", user.toJSON());
                    newFragment.setArguments(bundle);

                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
