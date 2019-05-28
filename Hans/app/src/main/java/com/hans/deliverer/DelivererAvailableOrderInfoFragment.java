package com.hans.deliverer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MyAccountCompleteFragment;
import com.hans.OrderListAdapter;
import com.hans.R;
import com.hans.domain.Order;

import com.hans.domain.OrderStatus;
import com.hans.domain.User;
import com.hans.mail.MailSender;
import com.hans.map.MapsFragment;

import static android.support.constraint.Constraints.TAG;


public class DelivererAvailableOrderInfoFragment extends Fragment
{
    Order order;

    DatabaseFirebase db = new DatabaseFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_deliverer_available_order_info, container, false);
        getActivity().setTitle("Szczegóły zlecenia");

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        order = Order.createFromJSON(orderJSON);

        EditText fromCity = view.findViewById(R.id.fromCity);
        EditText fromZipCode = view.findViewById(R.id.fromZipCode);
        EditText fromStreet = view.findViewById(R.id.fromStreet);
        EditText fromNumber = view.findViewById(R.id.fromNumber);

        EditText toCity = view.findViewById(R.id.toCity);
        EditText toZipCode = view.findViewById(R.id.toZipCode);
        EditText toStreet = view.findViewById(R.id.toStreet);
        EditText toNumber = view.findViewById(R.id.toNumber);

        EditText isPaid = view.findViewById(R.id.is_paid);
        EditText price = view.findViewById(R.id.price);
        EditText description = view.findViewById(R.id.description);
        EditText weight = view.findViewById(R.id.weight);
        EditText width = view.findViewById(R.id.width);
        EditText height = view.findViewById(R.id.height);
        EditText depth = view.findViewById(R.id.depth);

        fromCity.setText(order.getPickupAddress().get("city").toString());
        fromZipCode.setText(order.getPickupAddress().get("zipCode").toString());
        fromStreet.setText(order.getPickupAddress().get("street").toString());
        fromNumber.setText(order.getPickupAddress().get("number").toString());

        toCity.setText(order.getDeliveryAddress().get("city").toString());
        toZipCode.setText(order.getDeliveryAddress().get("zipCode").toString());
        toStreet.setText(order.getDeliveryAddress().get("street").toString());
        toNumber.setText(order.getDeliveryAddress().get("number").toString());

        if (order.getIsPaid())
        {
            isPaid.setText("Tak");
            isPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        } else
        {
            isPaid.setText("Nie");
            isPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
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
        openNavigationStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGoogleMapsNavigation(startPoint);
            }
        });

        Button openNavigationDestination = view.findViewById(R.id.openNavigationDestination);
        openNavigationDestination.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
        transaction.commit();

        Button acceptOrderButton = view.findViewById(R.id.accept_order_button);
        acceptOrderButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Czy na pewno chcesz przyjąć te zlecenie?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                acceptOrder();


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
                builder.create().show();
            }
        });

        return view;
    }

    public void openGoogleMapsNavigation(String destination)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivity(mapIntent);
        } else
        {
            Snackbar.make(getView(), "Nie posiadasz Map Google. Zainstaluj ze Sklepu Play.", Snackbar.LENGTH_SHORT).show();
            return;
        }
    }

    private void acceptOrder()
    {

        Log.d("Order", "Weszlo");
        Log.d("Order", order.toString());
        db.getOrder(order).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Order orderr;
                    DocumentSnapshot document = task.getResult();
                    orderr = document.toObject(Order.class);

                    if((orderr.getDelivererId() == null ) && (orderr.getOrderStatus()==OrderStatus.WAITING_FOR_DELIVERER)){
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        order.setOrderStatus(OrderStatus.IN_TRANSIT);
                        order.setDelivererId(firebaseUser.getUid());
                        db.setOrder(order);
                        //                                sendNotificationToClient();
                        Snackbar.make(getView(), "Przyjęto zlecenie", Snackbar.LENGTH_SHORT).show();
                        Fragment newFragment = new DelivererAvailableOrdersFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, newFragment);

                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else{
                        Snackbar.make(getView(), "Przepraszamy, zlecenie nieaktualne", Snackbar.LENGTH_SHORT).show();
                        Fragment newFragment = new DelivererAvailableOrdersFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, newFragment);

                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }


                else {
                    Snackbar.make(getView(), "Przyjęcie się nie powiodło", Snackbar.LENGTH_SHORT).show();
                    Fragment newFragment = new DelivererAvailableOrdersFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

    }


    private void sendNotificationToClient()
    {
        final MailSender mailSender = new MailSender();
        db.getUser(order.getClientId()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        User userFromDatabase = document.toObject(User.class);
                        mailSender.execute(createNotificationEmail(userFromDatabase.getGoogleEmail()));
                    }
                } else
                {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private String[] createNotificationEmail(String emailTo)
    {
        String subject = "Aktualizacja statusu zlecenia";

        String pickupAddress = order.getPickupAddress().get("city").toString() + ", ul. " +
                order.getPickupAddress().get("street").toString() + " " +
                order.getPickupAddress().get("number").toString();
        String deliveryAddress = order.getDeliveryAddress().get("city").toString() + ", ul. " +
                order.getDeliveryAddress().get("street").toString() + " " +
                order.getDeliveryAddress().get("number").toString();

        String msg = "Status zlecenia \nz: " + pickupAddress + "\ndo: " + deliveryAddress + "\nzostał zmieniony na: " + order.getOrderStatus().getPolishName();
        return new String[]{emailTo, subject, msg};
    }
}
