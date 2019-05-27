package com.hans.deliverer;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
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
import com.hans.domain.NDSpinner;
import com.hans.domain.Order;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class DelivererAvailableOrdersFragment extends Fragment
{

    ArrayList<Order> receivedOrderList = new ArrayList<>();
    DatabaseFirebase db = new DatabaseFirebase();
    View view;
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

        // current location for sorting by distance
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        sortSpinner = view.findViewById(R.id.spinner_sort);
        filterSpinner = view.findViewById(R.id.spinner_filter);

        spinnersInit();
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("sortSpinner", Integer.toString(position));
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
                    Log.d("sortSpinner", "Orders list replaced by sorted list");
                }
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
                                            resultArray = filterByStartPointCity(cityToSearch);
                                            break;
                                        case 2:
                                            resultArray = filterByEndPointCity(cityToSearch);
                                            break;

                                    }
                                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, resultArray);
                                    ordersListView.setAdapter(orderListAdapter);

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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d("filter", "nothing");

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

    private void spinnersInit()
    {
        Spinner dropdown = view.findViewById(R.id.spinner_sort);
        String[] items = new String[]{"czasie złożenia zamówienia - rosnąco", "czasie złożenia zamówienia - malejąco", "odległości - rosnąco", "odległości - malejąco", "cenie - rosnąco", "cenie - malejąco"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = view.findViewById(R.id.spinner_filter);
        String[] items2 = new String[]{"brak", "miastach początkowych", "miastach docelowych"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
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
        try
        {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
                    {
                        @Override
                        public void onSuccess(Location location)
                        {
                            if (location != null)
                            {
                                final Location location1 = location;
                                Collections.sort(receivedOrderList, new Comparator<Order>()
                                {
                                    @Override
                                    public int compare(final Order o1, final Order o2)
                                    {
                                        if (getDistanceToPickupAddress(o1, location1) > getDistanceToPickupAddress(o2, location1))
                                        {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });
                            } else
                            {
                                Snackbar.make(getView(), "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
                            }
                            ordersListView.invalidate();
                        }
                    });
        } catch (SecurityException e)
        {
            Log.d("excepioton", "Permission for getting location denied");
        }
    }

    private void sortByDistanceAsc()
    {
        try
        {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
                    {
                        @Override
                        public void onSuccess(Location location)
                        {
                            if (location != null)
                            {
                                final Location location1 = location;
                                Collections.sort(receivedOrderList, new Comparator<Order>()
                                {
                                    @Override
                                    public int compare(final Order o1, final Order o2)
                                    {
                                        if (getDistanceToPickupAddress(o1, location1) <= getDistanceToPickupAddress(o2, location1))
                                        {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });
                            } else
                            {
                                Snackbar.make(getView(), "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
                            }
                            ordersListView.invalidate();
                        }
                    });
        } catch (SecurityException e)
        {
            Log.d("excepioton", "Permission for getting location denied");
        }
    }

    private float getDistanceToPickupAddress(Order order, Location location)
    {
        Geocoder geocoder = new Geocoder(getContext());
        String pickupAddress1 = order.getPickupAddress().get("city") + " " +
                order.getPickupAddress().get("zipcode") + " " +
                order.getPickupAddress().get("street") + " " +
                order.getPickupAddress().get("number");
        List<Address> addressList = null;
        try
        {
            addressList = geocoder.getFromLocationName(pickupAddress1, 1);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        double x1 = address.getLatitude();
        double y1 = address.getLongitude();

        float[] distance = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), x1, y1, distance);

        Log.d("distance", "address: " + pickupAddress1);
        Log.d("distance", Float.toString(distance[0]));
        return distance[0];
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

    private ArrayList<Order> filterByStartPointCity(String cityToSearch)
    {
        ArrayList<Order> resultArray = new ArrayList<>();
        cityToSearch = Normalizer.normalize(cityToSearch.toLowerCase(), Normalizer.Form.NFD);
        cityToSearch = cityToSearch.replaceAll("ł", "l");

        for (Order order : receivedOrderList)
        {
            String orderCity = order.getPickupAddress().get("city").toString().toLowerCase();
            orderCity = Normalizer.normalize(orderCity, Normalizer.Form.NFD);
            orderCity = orderCity.replaceAll("ł", "l");

            if (orderCity.matches(cityToSearch))
                resultArray.add(order);
        }
        return resultArray;
    }

    private ArrayList<Order> filterByEndPointCity(String cityToSearch)
    {
        ArrayList<Order> resultArray = new ArrayList<>();
        cityToSearch = Normalizer.normalize(cityToSearch.toLowerCase(), Normalizer.Form.NFD);
        cityToSearch = cityToSearch.replaceAll("ł", "l");

        for (Order order : receivedOrderList)
        {
            String orderCity = order.getDeliveryAddress().get("city").toString().toLowerCase();
            orderCity = Normalizer.normalize(orderCity, Normalizer.Form.NFD);
            orderCity = orderCity.replaceAll("ł", "l");
            if (orderCity.matches(cityToSearch))
                resultArray.add(order);
        }
        return resultArray;
    }

}
