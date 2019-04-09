package com.hans.client;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.OrderListAdapter;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.domain.User;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ClientInTransitOrdersFragment extends Fragment {

    ArrayList<Order> InTransitOrderList = new ArrayList<>();
    ArrayList<User> InTransitDelivererList = new ArrayList<>();

    DatabaseFirebase db = new DatabaseFirebase();
    View v;
    ListView ordersListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity)getActivity()).setActionBarTitle("Twoje zlecenia w trakcie wykonywania");

        v = inflater.inflate(R.layout.fragment_client_all_orders, container, false);
        orderListInit();
        ordersListView = v.findViewById(R.id.listView);

        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("LISTPOS", Integer.toString(position));
                Fragment newFragment = new ClientOrderInTransitInfoFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);

                Bundle bundle = new Bundle();
                bundle.putString("order", InTransitOrderList.get(position).toJSON());

                User client = getUserForOrder(InTransitOrderList.get(position).getDelivererId());
                Log.d("Client22", client.toString());


                bundle.putString("deliverer", getUserForOrder(InTransitOrderList.get(position).getClientId()).toJSON());
                newFragment.setArguments(bundle);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }


    private void  orderListInit(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.getInTransitOrdersForClient(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    InTransitOrderList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Order orderFromDatabase =document.toObject(Order.class);
                        orderFromDatabase.setId(document.getId());
                        InTransitOrderList.add(orderFromDatabase);
                        Log.d("Order", document.toObject(Order.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());

                    }
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, InTransitOrderList);
                    ordersListView.setAdapter(orderListAdapter);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        db.getAllUsers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    InTransitDelivererList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User userFromDatabase = document.toObject(User.class);
                        InTransitDelivererList.add(userFromDatabase);
                        Log.d("Client22", document.toObject(User.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private User getUserForOrder(String googleID){
        User client = new User();
        Log.d("Client22", googleID);

        for(User e: InTransitDelivererList){
            if(e.getGoogleId().equals(googleID)){
                Log.d("Client22", e.toString());

                client = e;
                Log.d("Client22", client.toString());

            }
        }
        return client;
    }
}
