package com.hans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hans.domain.Order;

public class OrderClientInfoFragment extends Fragment {
    Order order;
    ListView ordersListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_client_order_info, container, false);
        Log.d("orderinfo", "ClientOrderInfoFragment started");

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        order = Order.createFromJSON(orderJSON);

        TextView from = v.findViewById(R.id.from);
        TextView to = v.findViewById(R.id.to);
        TextView price = v.findViewById(R.id.price);
        TextView title = v.findViewById(R.id.description);
        TextView weight = v.findViewById(R.id.weight);

        from.setText(order.getPickupAddress().toString());
        to.setText(order.getDeliveryAddress().toString());
        price.setText(order.getPrice().toString());
        title.setText(order.getDescription());
        weight.setText(order.getWeight().toString());

        ordersListView = v.findViewById(R.id.listView);

        Button button = ordersListView.findViewById(R.id.cancel_order_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }
}
