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

        Order order = new Order(pickupAddress, deliveryAddress, description);

        final View view;
        ViewHolder viewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.pickupAddress = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.deliveryAddress = (TextView) convertView.findViewById(R.id.textView3);
            viewHolder.description = (TextView) convertView.findViewById(R.id.textView1);

            viewHolder.pickupAddress.setText(order.getPickupAddress());
            viewHolder.deliveryAddress.setText(order.getDeliveryAddress());
            viewHolder.description.setText(order.getDescription());

            view = convertView;
        }
        else {
            view = convertView;
        }



        return view;
    }
}
