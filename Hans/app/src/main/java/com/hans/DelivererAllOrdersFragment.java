package com.hans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.domain.Order;
import com.hans.domain.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static android.support.constraint.Constraints.TAG;

public class DelivererAllOrdersFragment extends Fragment
{

    ArrayList<Order> orderList = new ArrayList<>();
    ArrayList<Order> receivedOrderList = new ArrayList<>();

    ListView ordersListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_deliverer_all_orders, container, false);

        orderListInit();
        ordersListView = v.findViewById(R.id.listView);
        OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, orderList);
        ordersListView.setAdapter(orderListAdapter);

        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LISTPOS", Integer.toString(position));
                Fragment newFragment = new OrderInfoFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);

                Bundle bundle = new Bundle();
                bundle.putString("order", orderList.get(position).toJSON());
                newFragment.setArguments(bundle);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }

    private void orderListInit() {
        Map<String, Object> pickupAddress = new HashMap();
        Map<String, Object> deliveryAddress = new HashMap();
        Map<String, Object> dimensions = new HashMap();


        //orderList.add(new Order(1, OrderStatus.WAITING_FOR_DELIVERER,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));

        orderList.add(new Order(1, OrderStatus.WAITING_FOR_DELIVERER,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(2, OrderStatus.IN_TRANSIT,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(3, OrderStatus.WAITING_FOR_DELIVERER,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(4, OrderStatus.IN_TRANSIT,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(5, OrderStatus.DELIVERED,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(6, OrderStatus.WAITING_FOR_DELIVERER,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(7, OrderStatus.WAITING_FOR_DELIVERER,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));
        orderList.add(new Order(8, OrderStatus.WAITING_FOR_DELIVERER,pickupAddress,deliveryAddress,10.5,10.5,10.5,dimensions,"asda","asdaaa","1222"));

        //        orderList.add(new Order(2, "Piotrowo 3, 60-101 Poznań", "Piotrowo 3, 60-101 Poznań", 10.5, "1m x 2m", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"", 20.0));
//        orderList.add(new Order(3, "adr5", "adr6", 10.5, "1m x 2m", "description3", 20.0));
//        orderList.add(new Order(4, "adr7", "adr8", 10.5, "1m x 2m", "description4", 20.0));
//        orderList.add(new Order(5, "adr9", "adr10", 10.5, "1m x 2m", "description5", 20.0));
//        orderList.add(new Order(1, "Piotrowo 3, 60-101 Poznań", "Piotrowo 3, 60-101 Poznań", 10.5, "1.785m x 2.128m x 5.348m", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"", 20.0));
//        orderList.add(new Order(1, "adr1", "adr2", 10.5, "1m x 2m", "description1", 20.0));
//        orderList.add(new Order(2, "adr3", "adr4", 10.5, "1m x 2m", "description2", 20.0));
//        orderList.add(new Order(3, "adr5", "adr6", 10.5, "1m x 2m", "description3", 20.0));
//        orderList.add(new Order(4, "adr7", "adr8", 10.5, "1m x 2m", "description4", 20.0));
//        orderList.add(new Order(5, "adr9", "adr10", 10.5, "1m x 2m", "description5", 20.0));


        databaseFirebase db = new databaseFirebase();

        for(Order order : orderList){
            db.insertOrderToDatabase(order);
        }
//        ArrayList<Order> orderListTest = new ArrayList<>();
//        orderListTest = db.getAllOrdersToDeliver();
//        Log.d("Order", "#####################SPAM");
//
//        for(Order order : orderListTest){
//           Log.d("Order", "#####################SPAM"+order.toString());
//        }
        db.getAllOrdersTask().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        receivedOrderList.add(document.toObject(Order.class));
                        Log.d("Order", document.toObject(Order.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    //firestoreCallback.onCallback(orderList);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(this, signInDeliverer.class);
//            startActivity(intent);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
