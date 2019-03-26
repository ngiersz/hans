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
import java.util.Map;

class OrderListAdapter extends ArrayAdapter<Order> {

    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView pickupAddress;
        TextView deliveryAddress;
        TextView description;
        TextView price;
        TextView weight;
    }

    public OrderListAdapter(Context context, int resource, ArrayList<Order> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> pickupAddress = getItem(position).getPickupAddress();
        Map<String, Object> deliveryAddress = getItem(position).getDeliveryAddress();
        String description = getItem(position).getDescription();
        Double price = getItem(position).getPrice();
        Double weight = getItem(position).getWeight();

        Order order = new Order(pickupAddress, deliveryAddress, price, weight, description);

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

            viewHolder.pickupAddress.setText("Z: " + order.getPickupAddress().get("city") + " " + order.getPickupAddress().get("street") + " " + order.getPickupAddress().get("number"));
            viewHolder.deliveryAddress.setText("Do: " +order.getDeliveryAddress().get("city") + " " + order.getDeliveryAddress().get("street") + " " + order.getDeliveryAddress().get("number"));
            viewHolder.description.setText(order.getDescription());
            // TODO: get suffixes from values/strings.xml
            viewHolder.price.setText("Cena: " + order.getPrice().toString() + " zł");
            viewHolder.weight.setText("Waga: " + order.getWeight().toString() + " kg");

            view = convertView;
        }
        else {
            view = convertView;
        }

        return view;
    }

}
