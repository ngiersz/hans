package com.hans.deliverer;

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
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.OrderListAdapter;
import com.hans.R;
import com.hans.domain.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        view = inflater.inflate(R.layout.fragment_deliverer_all_orders, container, false);

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
                    switch (position)
                    {
                        case 0:
                            sortByOrderTimeAsc();
                            break;
                        case 1:
                            sortByOrderTimeDesc();
                            break;
                        case 2:
                            sortByDistanceAsc();
                            break;
                        case 3:
                            sortByDistanceDesc();
                            break;
                        case 4:
                            sortByPriceAsc();
                            break;
                        case 5:
                            sortByPriceDesc();
                            break;
                    }
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
                    sortByOrderTimeAsc();
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

    private void sortByPriceDesc()
    {
        Collections.sort(receivedOrderList, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getPrice() < o2.getPrice())
                {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void sortByPriceAsc()
    {
        Collections.sort(receivedOrderList, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getPrice() > o2.getPrice())
                {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void sortByDistanceDesc()
    {
        Collections.sort(receivedOrderList, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
//                MapsFragment.GetPriceAndDistance(getContext(), o1.getPickupAddress(), )

                if (o1.getLength() < o2.getLength())
                {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void sortByDistanceAsc()
    {
        Collections.sort(receivedOrderList, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getLength() > o2.getLength())
                {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void sortByOrderTimeAsc()
    {
        Collections.sort(receivedOrderList, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getDate() != null && o2.getDate() != null)
                {
                    if (o1.getDate().compareTo(o2.getDate()) > 0)
                    {
                        return 1;
                    }
                }
                return -1;
            }
        });
    }

    private void sortByOrderTimeDesc()
    {
        Collections.sort(receivedOrderList, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getDate() != null && o2.getDate() != null)
                {
                    if (o1.getDate().compareTo(o2.getDate()) < 0)
                    {
                        return 1;
                    }
                }
                return -1;
            }
        });
    }

}