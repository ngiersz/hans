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
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView address1;
        TextView address2;
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
        String address1 = getItem(position).getAddress1();
        String address2 = getItem(position).getAddress2();
        String description = getItem(position).getDescription();

        Order order = new Order(address1, address2, description);

        final View view;
        ViewHolder viewHolder = null;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.address1 = (TextView) convertView.findViewById(R.id.textView2);
            viewHolder.address2 = (TextView) convertView.findViewById(R.id.textView3);
            viewHolder.description = (TextView) convertView.findViewById(R.id.textView1);

            viewHolder.address1.setText(order.getAddress1());
            viewHolder.address2.setText(order.getAddress2());
            viewHolder.description.setText(order.getDescription());

            view = convertView;
        }
        else {
            view = convertView;
        }



        return view;
    }
}
