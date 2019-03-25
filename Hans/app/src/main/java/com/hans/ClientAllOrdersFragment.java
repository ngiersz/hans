package com.hans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hans.domain.Order;

import java.util.ArrayList;

public class ClientAllOrdersFragment extends Fragment {

    ArrayList<Order> orderList = new ArrayList<>();
    ArrayList<Order> receivedOrderList = new ArrayList<>();

    ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_client_all_orders, container, false);

        orderListInit();
        listView = v.findViewById(R.id.listView);
        OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, orderList);
        listView.setAdapter(orderListAdapter);
        return v;
    }

    private void  orderListInit(){

    }
}
