package com.hans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.hans.domain.Order;

import java.util.ArrayList;

public class DelivererAllOrdersFragment extends Fragment
{

    ArrayList<Order> orderList = new ArrayList<>();
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_deliverer_all_orders, container, false);

        orderListInit();
        listView = v.findViewById(R.id.listView);
        OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, orderList);
        listView.setAdapter(orderListAdapter);

        return v;
    }


    private void orderListInit()
    {
        orderList.add(new Order(1, "Piotrowo 3, 60-101 Poznań", "Piotrowo 3, 60-101 Poznań", 10.5, "1.785m x 2.128m x 5.348m", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"", 20.0));
        orderList.add(new Order(2, "Piotrowo 3, 60-101 Poznań", "Piotrowo 3, 60-101 Poznań", 10.5, "1m x 2m", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"", 20.0));
        orderList.add(new Order(3, "adr5", "adr6", 10.5, "1m x 2m", "description3", 20.0));
        orderList.add(new Order(4, "adr7", "adr8", 10.5, "1m x 2m", "description4", 20.0));
        orderList.add(new Order(5, "adr9", "adr10", 10.5, "1m x 2m", "description5", 20.0));
        orderList.add(new Order(1, "adr1", "adr2", 10.5, "1m x 2m", "description1", 20.0));
        orderList.add(new Order(2, "adr3", "adr4", 10.5, "1m x 2m", "description2", 20.0));
        orderList.add(new Order(3, "adr5", "adr6", 10.5, "1m x 2m", "description3", 20.0));
        orderList.add(new Order(4, "adr7", "adr8", 10.5, "1m x 2m", "description4", 20.0));
        orderList.add(new Order(5, "adr9", "adr10", 10.5, "1m x 2m", "description5", 20.0));


        databaseFirebase db = new databaseFirebase();

        for (Order order : orderList)
        {
            // db.insertOrderToDatabase(order);
        }
        ArrayList<Order> orderListTest = new ArrayList<>();
        orderListTest = db.getAllOrdersToDeliver();
        for (Order order : orderListTest)
        {
            Log.d("Order", "#####################SPAM" + order.toString());
        }
    }
}
