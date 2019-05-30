package com.hans.deliverer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.OrderListAdapter;
import com.hans.R;
import com.hans.domain.NDSpinner;
import com.hans.domain.Order;
import com.hans.sort.SortFilterOrders;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class DelivererAvailableOrdersFragment extends Fragment
{

    ArrayList<Order> receivedOrderList = new ArrayList<>();
    DatabaseFirebase db = new DatabaseFirebase();
    View mainView;
    ListView ordersListView;
    Spinner sortSpinner;
    NDSpinner filterSpinner;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle("Dostępne zlecenia");
        mainView = inflater.inflate(R.layout.fragment_deliverer_available_orders, container, false);

        orderListInit();
        ordersListView = mainView.findViewById(R.id.listView);

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

        // current location for sorting by distance
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        sortSpinner = mainView.findViewById(R.id.spinner_sort);
        filterSpinner = mainView.findViewById(R.id.spinner_filter);
        spinnersInit();

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("sortSpinner", Integer.toString(position));
                if (receivedOrderList.size() > 1)
                {
                    SortFilterOrders sortOrders = new SortFilterOrders(receivedOrderList, getContext(), getActivity());
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
                    // info about empty list
                    if (receivedOrderList.size() > 0)
                    {
                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.INVISIBLE);
                    } else
                    {
                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.VISIBLE);
                    }                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id)
            {
                final SortFilterOrders sortOrders = new SortFilterOrders(receivedOrderList, getContext(), getActivity());
                Log.d("filter", Integer.toString(position));
                if (position != 0)
                {
                    LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                    final View alertDialogView = layoutInflater.inflate(R.layout.alert_dialog_search, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(alertDialogView)
                            .setTitle("Podaj nazwę miasta")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    EditText editText = alertDialogView.findViewById(R.id.search_city);
                                    String cityToSearch = editText.getText().toString().toLowerCase();
                                    ArrayList<Order> resultArray = new ArrayList<>();

                                    switch (position)
                                    {
                                        case 1:
                                            resultArray = sortOrders.filterByStartPointCity(cityToSearch);
                                            break;
                                        case 2:
                                            resultArray = sortOrders.filterByEndPointCity(cityToSearch);
                                            break;

                                    }
                                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, resultArray);
                                    ordersListView.setAdapter(orderListAdapter);

                                    // info about empty list
                                    if (resultArray.size() > 0)
                                    {
                                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                                        progressBar.setVisibility(View.INVISIBLE);

                                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                                        emptyList.setVisibility(View.INVISIBLE);
                                    } else
                                    {
                                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                                        progressBar.setVisibility(View.INVISIBLE);

                                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                                        emptyList.setVisibility(View.VISIBLE);
                                    }

                                }
                            })
                            .setNegativeButton("Anuluj", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    return;
                                }
                            })
                            .create().show();
                } else
                {
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, receivedOrderList);
                    ordersListView.setAdapter(orderListAdapter);

                    // info about empty list
                    if (receivedOrderList.size() > 0)
                    {
                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.INVISIBLE);
                    } else
                    {
                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        return mainView;
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

                    SortFilterOrders sortOrders = new SortFilterOrders(receivedOrderList, getContext(), getActivity());
                    receivedOrderList = sortOrders.sortByOrderTimeAsc();

                    // info about empty list
                    if (receivedOrderList.size() > 0)
                    {
                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.INVISIBLE);
                    } else
                    {
                        ProgressBar progressBar = mainView.findViewById(R.id.empty_progress_bar);
                        progressBar.setVisibility(View.INVISIBLE);

                        TextView emptyList = mainView.findViewById(R.id.empty_text_view);
                        emptyList.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void spinnersInit()
    {
        Spinner dropdown = mainView.findViewById(R.id.spinner_sort);
        String[] items = new String[]{"czasie złożenia zamówienia - rosnąco", "czasie złożenia zamówienia - malejąco", "odległości - rosnąco", "odległości - malejąco", "cenie - rosnąco", "cenie - malejąco"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = mainView.findViewById(R.id.spinner_filter);
        String[] items2 = new String[]{"brak", "miastach początkowych", "miastach docelowych"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
    }



}
