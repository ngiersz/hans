package com.hans.deliverer;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hans.R;
import com.hans.domain.Order;

import com.hans.map.MapsFragment;

public class DelivererOrderInfoFragment extends Fragment {
    Order order;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_deliverer_order_info, container, false);

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        order = Order.createFromJSON(orderJSON);

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

        final String startPoint = fromCity.getText() + " " + fromZipCode.getText() + " " + fromStreet.getText() + " " + fromNumber.getText();
        final String destinationPoint = toCity.getText() + " " + toZipCode.getText() + " " + toStreet.getText() + " " + toNumber.getText();

        Button openNavigationStart = view.findViewById(R.id.openNavigationStart);
        openNavigationStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMapsNavigation(startPoint);
            }
        });

        Button openNavigationDestination = view.findViewById(R.id.openNavigationDestination);
        openNavigationDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMapsNavigation(destinationPoint);
            }
        });

        Fragment mapsActivity = new MapsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.map, mapsActivity);

        Bundle mapsBundle = new Bundle();
        mapsBundle.putString("origin", startPoint);
        mapsBundle.putString("destination", destinationPoint);
        mapsActivity.setArguments(mapsBundle);

        transaction.addToBackStack(null);
        transaction.commit();

        if (getActivity().getSupportFragmentManager() == null) {
            Log.d("button", "getActivity().getSupportFragmentManager() is null");
        } else {
            Log.d("button", "getActivity().getSupportFragmentManager() is not null");

        }
        Button button = view.findViewById(R.id.accept_order_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(), "Przyjęto zlecenie", Snackbar.LENGTH_SHORT).show();
//                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }

    public void openGoogleMapsNavigation(String destination) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Snackbar.make(getView(), "Nie posiadasz Map Google. Zainstaluj ze Sklepu Play.", Snackbar.LENGTH_SHORT).show();
            return;
        }

    }

}
