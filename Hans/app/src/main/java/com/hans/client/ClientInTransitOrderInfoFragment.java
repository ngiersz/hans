package com.hans.client;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hans.DatabaseFirebase;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.domain.User;

public class ClientInTransitOrderInfoFragment extends Fragment {
    Order order;
    User deliverer;
    ListView ordersListView;
    DatabaseFirebase db = new DatabaseFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_client_in_transit_order_info, container, false);
        Log.d("orderinfo", "ClientOrderInfoFragment started");

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        order = Order.createFromJSON(orderJSON);

        String delivererJSON = bundle.getString("deliverer");
        deliverer = User.createFromJSON(delivererJSON);


        TextView fromCity = view.findViewById(R.id.fromCity);
        TextView fromZipCode = view.findViewById(R.id.fromZipCode);
        TextView fromStreet = view.findViewById(R.id.fromStreet);
        TextView fromNumber = view.findViewById(R.id.fromNumber);

        TextView toCity = view.findViewById(R.id.toCity);
        TextView toZipCode = view.findViewById(R.id.toZipCode);
        TextView toStreet = view.findViewById(R.id.toStreet);
        TextView toNumber = view.findViewById(R.id.toNumber);

        TextView price = view.findViewById(R.id.price);
        TextView description = view.findViewById(R.id.description);
        TextView weight = view.findViewById(R.id.weight);
        TextView width = view.findViewById(R.id.width);
        TextView height = view.findViewById(R.id.height);
        TextView depth = view.findViewById(R.id.depth);

        TextView delivererName = view.findViewById(R.id.deliverertName);
        TextView delivererSurName = view.findViewById(R.id.delivererSurname);
        TextView delivererEmail = view.findViewById(R.id.delivererEmail);
        TextView delivererPhone = view.findViewById(R.id.delivererPhone);

        TextView status = view.findViewById(R.id.order_status);
        TextView isPaid = view.findViewById(R.id.is_paid);
        TextView isReceived = view.findViewById(R.id.is_received);


        fromCity.setText(order.getPickupAddress().get("city").toString());
        fromZipCode.setText(order.getPickupAddress().get("zipCode").toString());
        fromStreet.setText(order.getPickupAddress().get("street").toString());
        fromNumber.setText(order.getPickupAddress().get("number").toString());

        toCity.setText(order.getDeliveryAddress().get("city").toString());
        toZipCode.setText(order.getDeliveryAddress().get("zipCode").toString());
        toStreet.setText(order.getDeliveryAddress().get("street").toString());
        toNumber.setText(order.getDeliveryAddress().get("number").toString());

        price.setText(order.getPrice().toString());
        description.setText(order.getDescription());
        weight.setText(order.getWeight().toString());
        width.setText(order.getDimensions().get("width").toString());
        height.setText(order.getDimensions().get("height").toString());
        depth.setText(order.getDimensions().get("depth").toString());

        if(order.getIsPaid()){
            isPaid.setText("Tak");
            isPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else{
            isPaid.setText("Nie");
            isPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        if(order.getIsReceived()){
            isReceived.setText("Została odebrana z miejsca początkowego");
            isReceived.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else{
            isReceived.setText("Czeka na odbiór z miejsca początkowego");
            isReceived.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
        delivererName.setText(deliverer.getName());
        delivererSurName.setText(deliverer.getSurname());
        delivererEmail.setText(deliverer.getGoogleEmail());
        delivererPhone.setText(deliverer.getPhoneNumber());

        status.setText(order.getOrderStatus().getPolishName());
        ordersListView = view.findViewById(R.id.listView);



        return view;
    }
}
