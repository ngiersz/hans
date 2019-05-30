package com.hans.client;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.R;
import com.hans.deliverer.DelivererAvailableOrdersFragment;
import com.hans.domain.Order;
import com.hans.domain.OrderStatus;

import java.util.Calendar;

public class ClientWaitingsOrderInfoFragment extends Fragment {
    private Order order;
    private ListView ordersListView;
    private DatabaseFirebase db = new DatabaseFirebase();
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_client_waiting_order_info, container, false);
        NavigationView nav = getActivity().findViewById(R.id.nav_view);
        // navigation -> menu -> item (zlecenia) -> menu
        menu = nav.getMenu().getItem(0).getSubMenu();

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        order = Order.createFromJSON(orderJSON);

        TextView status = view.findViewById(R.id.order_status);
        TextView isPaid = view.findViewById(R.id.is_paid);

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

        status.setText(order.getOrderStatus().getPolishName());
        if(order.getIsPaid()){
            isPaid.setText("Tak");
            isPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else{
            isPaid.setText("Nie");
            isPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        price.setText(order.getPrice().toString());
        description.setText(order.getDescription());
        weight.setText(order.getWeight().toString());
        width.setText(order.getDimensions().get("width").toString());
        height.setText(order.getDimensions().get("height").toString());
        depth.setText(order.getDimensions().get("depth").toString());

        ordersListView = view.findViewById(R.id.listView);


        Button cancelOrderbutton = view.findViewById(R.id.cancel_order_button);
        cancelOrderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
                buider.setMessage("Czy na pewno chcesz usunąć te zlecenie?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                cancelOrder();
//                                getActivity().getSupportFragmentManager().popBackStackImmediate();
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

    private void cancelOrder()
    {

        db.getOrder(order).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Order orderr;
                    DocumentSnapshot document = task.getResult();
                    orderr = document.toObject(Order.class);

                    if((orderr.getDelivererId() == null ) && (orderr.getOrderStatus() == OrderStatus.WAITING_FOR_DELIVERER)){
                        db.deleteOrderByID(order);
                        Snackbar.make(getView(), "Anulowano zlecenie.", Snackbar.LENGTH_SHORT).show();


                        menu.getItem(1).setChecked(true);
                        Fragment newFragment = new ClientWaitingsOrdersFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else{
                        Snackbar.make(getView(), "Przepraszamy, zlecenie zostało przyjęte.", Snackbar.LENGTH_SHORT).show();
                        Fragment newFragment = new ClientWaitingsOrdersFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                else {
                    Snackbar.make(getView(), "Anulowanie się nie powiodło", Snackbar.LENGTH_SHORT).show();
                    Fragment newFragment = new ClientWaitingsOrdersFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

    }
}
