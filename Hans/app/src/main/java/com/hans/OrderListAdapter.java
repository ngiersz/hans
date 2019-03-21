package com.hans;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hans.domain.Order;

import java.util.ArrayList;

class OrderListAdapter extends ArrayAdapter<Order> {

    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView pickupAddress;
        TextView deliveryAddress;
        TextView description;
        TextView price;
        TextView weight;
        TextView measurments;
    }

    public OrderListAdapter(Context context, int resource, ArrayList<Order> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String pickupAddress = getItem(position).getPickupAddress();
        String deliveryAddress = getItem(position).getDeliveryAddress();
        String description = getItem(position).getDescription();
        Double price = getItem(position).getPrice();
        Double weight = getItem(position).getWeight();
        String measurments = getItem(position).getMeasurments();

        Order order = new Order(pickupAddress, deliveryAddress, description, price, weight, measurments);

        final View view;
        ViewHolder viewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.pickupAddress = convertView.findViewById(R.id.pickupAddress);
            viewHolder.deliveryAddress = convertView.findViewById(R.id.deliveryAddress);
            viewHolder.description = convertView.findViewById(R.id.description);
            viewHolder.price = convertView.findViewById(R.id.price);
            viewHolder.weight = convertView.findViewById(R.id.weight);
            viewHolder.measurments = convertView.findViewById(R.id.measurments);

            viewHolder.pickupAddress.setText("Z: " + order.getPickupAddress());
            viewHolder.deliveryAddress.setText("Do: " + order.getDeliveryAddress());
            viewHolder.description.setText(order.getDescription());
            // TODO: get suffixes from values/strings.xml
            viewHolder.price.setText(order.getPrice().toString() + " PLN");
            viewHolder.weight.setText(order.getWeight().toString() + " kg");
            viewHolder.measurments.setText(order.getMeasurments());

            view = convertView;
        }
        else {
            view = convertView;
        }



        return view;
    }
}
