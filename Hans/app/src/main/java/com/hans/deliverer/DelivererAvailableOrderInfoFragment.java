package com.hans.deliverer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
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
    private Order order;
    private Menu menu;
    private DatabaseFirebase db = new DatabaseFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_deliverer_available_order_info, container, false);
        NavigationView nav = getActivity().findViewById(R.id.nav_view);
        // navigation -> menu -> item (zlecenia) -> menu
        menu = nav.getMenu().getItem(0).getSubMenu();
        getActivity().setTitle("Szczegóły zlecenia");

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        order = Order.createFromJSON(orderJSON);

        TextView status = view.findViewById(R.id.order_status);
        TextView fromCity = view.findViewById(R.id.fromCity);
        TextView fromZipCode = view.findViewById(R.id.fromZipCode);
        TextView fromStreet = view.findViewById(R.id.fromStreet);
        TextView fromNumber = view.findViewById(R.id.fromNumber);

        TextView toCity = view.findViewById(R.id.toCity);
        TextView toZipCode = view.findViewById(R.id.toZipCode);
        TextView toStreet = view.findViewById(R.id.toStreet);
        TextView toNumber = view.findViewById(R.id.toNumber);

        TextView isPaid = view.findViewById(R.id.is_paid);
        TextView price = view.findViewById(R.id.price);
        TextView description = view.findViewById(R.id.description);
        TextView weight = view.findViewById(R.id.weight);
        TextView width = view.findViewById(R.id.width);
        TextView height = view.findViewById(R.id.height);
        TextView depth = view.findViewById(R.id.depth);

        status.setText(order.getOrderStatus().getPolishName());
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
        db.getOrder(order).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    Order orderr;
                    DocumentSnapshot document = task.getResult();

                    orderr = document.toObject(Order.class);
                    if (orderr != null)
                    {
                        if ((orderr.getDelivererId() == null) && (orderr.getOrderStatus() == OrderStatus.WAITING_FOR_DELIVERER))
                        {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            order.setOrderStatus(OrderStatus.IN_TRANSIT);
                            order.setDelivererId(firebaseUser.getUid());
                            db.setOrder(order);
                            MainActivity.sendNotificationToClient(order);
                            Snackbar.make(getView(), "Przyjęto zlecenie", Snackbar.LENGTH_SHORT).show();

                            menu.getItem(0).setChecked(true);
                            Fragment newFragment = new DelivererAvailableOrdersFragment();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment, newFragment);
                            transaction.commit();
                        }
                    }
                }

                Snackbar.make(getView(), "Przepraszamy, zlecenie nieaktualne", Snackbar.LENGTH_SHORT).show();
                Fragment newFragment = new DelivererAvailableOrdersFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);
                transaction.commit();
            }
        });

    }

}
