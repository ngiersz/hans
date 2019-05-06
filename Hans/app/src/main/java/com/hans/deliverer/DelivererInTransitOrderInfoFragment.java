package com.hans.deliverer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.domain.OrderStatus;
import com.hans.domain.User;
import com.hans.map.MapsFragment;
import com.hans.pdf.GetReceiverNameFragment;

import static android.support.constraint.Constraints.TAG;

public class DelivererInTransitOrderInfoFragment extends Fragment {
    Order order;
    User client, deliverer;
    View view;
    DatabaseFirebase db = new DatabaseFirebase();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_deliverer_in_transit_order_info, container, false);
        getActivity().setTitle("Szczegóły zlecenia");

        Bundle bundle = this.getArguments();
        final String orderJSON = bundle.getString("order");
        String clientJSON = bundle.getString("client");
        String delivererJSON = bundle.getString("client");
        order = Order.createFromJSON(orderJSON);
        client = User.createFromJSON(clientJSON);
        deliverer = User.createFromJSON(delivererJSON);
        Log.d("Client22", client.toString());


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

        TextView clientPhone = view.findViewById(R.id.clientPhone);
        TextView clientName = view.findViewById(R.id.clientName);
        TextView clientSurname = view.findViewById(R.id.clientSurname);
        TextView clientEmail = view.findViewById(R.id.clientEmail);

        TextView isPaid = view.findViewById(R.id.is_paid);

        clientEmail.setText(client.getGoogleEmail());
        clientName.setText(client.getName());
        clientSurname.setText(client.getSurname());
        clientPhone.setText(client.getPhoneNumber());



        fromCity.setText(order.getPickupAddress().get("city").toString());
        fromZipCode.setText(order.getPickupAddress().get("zipCode").toString());
        fromStreet.setText(order.getPickupAddress().get("street").toString());
        fromNumber.setText(order.getPickupAddress().get("number").toString());

        toCity.setText(order.getDeliveryAddress().get("city").toString());
        toZipCode.setText(order.getDeliveryAddress().get("zipCode").toString());
        toStreet.setText(order.getDeliveryAddress().get("street").toString());
        toNumber.setText(order.getDeliveryAddress().get("number").toString());

        if(order.getisPaid()){
            isPaid.setText("Tak");
            isPaid.setTextColor(Color.GREEN);
        }else{
            isPaid.setText("Nie");
            isPaid.setTextColor(Color.RED);
        }
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

        Button button = view.findViewById(R.id.finish_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
                buider.setMessage("Czy na pewno zakończyc zlecenie?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                Bundle bundle = new Bundle();
                                bundle.putString("order", order.toJSON());
                                bundle.putString("client", client.toJSON());
                                bundle.putString("deliverer", deliverer.toJSON());
                                Log.d("finish", order.toJSON());
                                Fragment newFragment = new GetReceiverNameFragment();
                                newFragment.setArguments(bundle);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

//                                Snackbar.make(getView(), "Zakończono zlecenie", Snackbar.LENGTH_SHORT).show();
//
//                                finishOrder();
//                                sendNotificationToClient();

//                                Fragment newFragment = new DelivererInTransitOrdersFragment();
//                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                                transaction.replace(R.id.fragment, newFragment);
//                                transaction.addToBackStack(null);
//                                transaction.commit();
                            }
                        })
                        .setNegativeButton("ANULUJ", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                return;
                            }
                        });
                buider.create().show();
            }
        });
        return view;
    }
    private void finishOrder(){
        order.setOrderStatus(OrderStatus.CLOSED);
        db.setOrder(order);
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

    public void getUserInfo(String googleId){
        db.getUser(googleId).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        client = document.toObject(User.class);
                        Log.d("Client", document.toObject(User.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });

    }

}
