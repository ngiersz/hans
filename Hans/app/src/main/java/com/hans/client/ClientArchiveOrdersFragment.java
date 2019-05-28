package com.hans.client;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.OrderListAdapter;
import com.hans.R;
import com.hans.domain.NDSpinner;
import com.hans.domain.Order;
import com.hans.domain.User;
import com.hans.sort.SortFilterOrders;

import java.util.ArrayList;
import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class ClientArchiveOrdersFragment extends Fragment
{
    ArrayList<Order> archiveOrdersList = new ArrayList<>();
    ArrayList<User> InTransitDelivererList = new ArrayList<>();

    DatabaseFirebase db = new DatabaseFirebase();
    View mainView;
    ListView ordersListView;

    Spinner sortSpinner;
    NDSpinner filterSpinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((MainActivity) getActivity()).setActionBarTitle("Zakończone zlecenia");

        mainView = inflater.inflate(R.layout.content_list_view_orders_with_spinner, container, false);
        ordersListView = mainView.findViewById(R.id.listView);
        orderListInit();

        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                Log.d("LISTPOS", Integer.toString(position));
                Fragment newFragment = new ClientArchiveOrderInfoFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, newFragment);

                Bundle bundle = new Bundle();
                bundle.putString("order", archiveOrdersList.get(position).toJSON());

                User client = getUserForOrder(archiveOrdersList.get(position).getDelivererId());
                Log.d("Client22", client.toString());


                bundle.putString("deliverer", client.toJSON());
                newFragment.setArguments(bundle);

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        sortSpinner = mainView.findViewById(R.id.spinner_sort);
        filterSpinner = mainView.findViewById(R.id.spinner_filter);

        spinnersInit();

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("spinner", Integer.toString(position));
                if (archiveOrdersList.size() > 1)
                {
                    SortFilterOrders sortOrders = new SortFilterOrders(archiveOrdersList, getContext(), getActivity());
                    switch (position)
                    {
                        case 0:
                            archiveOrdersList = sortOrders.sortByOrderTimeAsc();
                            break;
                        case 1:
                            archiveOrdersList = sortOrders.sortByOrderTimeDesc();
                            break;
                        case 2:
                            archiveOrdersList = sortOrders.sortByPriceAsc();
                            break;
                        case 3:
                            archiveOrdersList = sortOrders.sortByPriceDesc();
                            break;
                    }
                    ordersListView.invalidate();
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, archiveOrdersList);
                    ordersListView.setAdapter(orderListAdapter);
                    Log.d("spinner", "Orders list replaced by sorted list");
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
                final SortFilterOrders sortFilterOrders = new SortFilterOrders(archiveOrdersList, getContext(), getActivity());

                Log.d("filter", Integer.toString(position));
                if (position == 1 || position == 2)
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
                                            resultArray = sortFilterOrders.filterByStartPointCity(cityToSearch);
                                            break;
                                        case 2:
                                            resultArray = sortFilterOrders.filterByEndPointCity(cityToSearch);
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
                } else if (position == 3)
                {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                            new DatePickerDialog.OnDateSetListener()
                            {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth)
                                {
                                    String pickedDate = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                                    ArrayList<Order> resultArray = sortFilterOrders.filterByDate(pickedDate);
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
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();

                } else
                {
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, archiveOrdersList);
                    ordersListView.setAdapter(orderListAdapter);

                    // info about empty list
                    if (archiveOrdersList.size() > 0)
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

    private void spinnersInit()
    {
        Spinner dropdown = mainView.findViewById(R.id.spinner_sort);
        String[] items = new String[]{"czasie złożenia zamówienia - rosnąco", "czasie złożenia zamówienia - malejąco", "cenie - rosnąco", "cenie - malejąco"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = mainView.findViewById(R.id.spinner_filter);
        String[] items2 = new String[]{"brak", "miastach początkowych", "miastach docelowych", "dacie"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
    }


    private void orderListInit()
    {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db.getClosedOrdersForClient(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    archiveOrdersList.clear();
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Order orderFromDatabase = document.toObject(Order.class);
                        orderFromDatabase.setId(document.getId());
                        archiveOrdersList.add(orderFromDatabase);
                        Log.d("Order", document.toObject(Order.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());

                    }
                    OrderListAdapter orderListAdapter = new OrderListAdapter(getContext(), R.layout.adapter_view_layout, archiveOrdersList);
                    ordersListView.setAdapter(orderListAdapter);

                    if (archiveOrdersList.size() > 0)
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

        db.getAllUsers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    InTransitDelivererList.clear();
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        User userFromDatabase = document.toObject(User.class);
                        InTransitDelivererList.add(userFromDatabase);
                        Log.d("Client22", document.toObject(User.class).toString());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }

                } else
                {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private User getUserForOrder(String googleID)
    {
        User client = new User();
        Log.d("Client22", googleID);

        for (User e : InTransitDelivererList)
        {
            if (e.getGoogleId().equals(googleID))
            {
                Log.d("Client22", e.toString());

                client = e;
                Log.d("Client22", client.toString());

            }
        }
        return client;
    }
}
