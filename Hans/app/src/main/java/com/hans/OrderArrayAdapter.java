package com.hans;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hans.domain.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderArrayAdapter extends ArrayAdapter<Order> {

    private Context context;
    private List<Order> orderList;

    public OrderArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public OrderArrayAdapter(Context context, int resource, ArrayList<Order> objects) {
        super(context, resource);
        this.context = context;
        this.orderList = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Order order = orderList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.order_list_element, null);

//        TextView description = (TextView) view.findViewById(R.id.description);
//        TextView pickupAddress = (TextView) view.findViewById(R.id.pickupAddress);
//        TextView deliveryAddress = (TextView) view.findViewById(R.id.deliveryAddress);
        TextView price = (TextView) view.findViewById(R.id.price);
//        TextView weight = (TextView) view.findViewById(R.id.weight);
//        TextView measurments = (TextView) view.findViewById(R.id.measurments);


//        description.setText(order.getDescription());
//        pickupAddress.setText(order.getPickupAddress());
//        deliveryAddress.setText(order.getDeliveryAddress());
        price.setText(order.getPrice().toString());
//        weight.setText(order.getWeight().toString());
//        measurments.setText(order.getMeasurments());

        // image

        return view;
    }
}
