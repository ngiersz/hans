package com.hans.deliverer;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.OrderListAdapter;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.map.MapsFragment;
import com.hans.sort.SortOrders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class DelivererAvailableOrdersFragment extends Fragment
{

    ArrayList<Order> receivedOrderList = new ArrayList<>();
    DatabaseFirebase db = new DatabaseFirebase();
    View view;
    ListView ordersListView;
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle("Dostępne zlecenia");
        view = inflater.inflate(R.layout.fragment_deliverer_available_orders, container, false);

        orderListInit();
        ordersListView = view.findViewById(R.id.listView);

        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Fragment newFragment = new DelivererAvailableOrderInfoFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);

                Bundle bundle = new Bundle();
                bundle.putString("order", receivedOrderList.get(position).toJSON());
                newFragment.setArguments(bundle);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        spinner = view.findViewById(R.id.spinner);
        spinnerInit();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("spinner", Integer.toString(position));
                if (receivedOrderList.size() > 1)
                {
                    SortOrders sortOrders = new SortOrders(receivedOrderList, getContext(), getActivity());
                    switch (position)
                    {
                        case 0:
                            receivedOrderList = sortOrders.sortByOrderTimeAsc();
                            break;
                        case 1:
                            receivedOrderList = sortOrders.sortByOrderTimeDesc();
                            break;
                        case 2:
                            receivedOrderList = sortOrders.sortByDistanceAsc();
                            break;
                        case 3:
                            receivedOrderList = sortOrders.sortByDistanceDesc();
                            break;
                        case 4:
                            receivedOrderList = sortOrders.sortByPriceAsc();
                            break;
                        case 5:
                            receivedOrderList = sortOrders.sortByPriceDesc();
                            break;
                    }
                    ordersListView.invalidate();
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, receivedOrderList);
                    ordersListView.setAdapter(orderListAdapter);
                    Log.d("spinner", "Orders list replaced by sorted list");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        return view;
    }

    private void orderListInit()
    {
        db.getAllOrdersForDelivererTask().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    receivedOrderList.clear();
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Log.d("Document", document.toString());
                        Order orderFromDatabase = document.toObject(Order.class);
                        orderFromDatabase.setId(document.getId());
                        receivedOrderList.add(orderFromDatabase);
                        Log.d("Order", document.toObject(Order.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, receivedOrderList);
                    ordersListView.setAdapter(orderListAdapter);

                    SortOrders sortOrders = new SortOrders(receivedOrderList, getContext(), getActivity());
                    receivedOrderList = sortOrders.sortByOrderTimeAsc();

                    if (receivedOrderList.size() > 0)
                    {
                        ProgressBar progressBar = view.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else
                    {
                        ProgressBar progressBar = view.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = view.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void spinnerInit()
    {
        Spinner dropdown = view.findViewById(R.id.spinner);
        String[] items = new String[]{"czasie złożenia zamówienia - rosnąco", "czasie złożenia zamówienia - malejąco", "odległości - rosnąco", "odległości - malejąco", "cenie - rosnąco", "cenie - malejąco"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }



}
