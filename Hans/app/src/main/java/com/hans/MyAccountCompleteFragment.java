package com.hans;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hans.deliverer.DelivererInTransitOrdersFragment;
import com.hans.domain.Order;
import com.hans.domain.User;

public class MyAccountCompleteFragment extends Fragment { Order order;
    User user;
    ListView ordersListView;
    DatabaseFirebase db = new DatabaseFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_account_complete, container, false);
        Log.d("my", "myaccount started");

        Bundle bundle = this.getArguments();
        String userJSON = bundle.getString("user");
        user = User.createFromJSON(userJSON);
        Log.d("my", user.toString());


        TextView userName = view.findViewById(R.id.usertName);
        TextView userSurName = view.findViewById(R.id.userSurName);
        TextView userEmail = view.findViewById(R.id.userEmail);
        TextView userPhone = view.findViewById(R.id.userPhone);

 
        userName.setText(user.getName());
        userSurName.setText(user.getSurname());
        userEmail.setText(user.getGoogleEmail());
        userPhone.setText(user.getPhoneNumber());

        ordersListView = view.findViewById(R.id.listView);

        Button button = view.findViewById(R.id.edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
                buider.setMessage("Czy na pewno chcesz edytować konto?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Snackbar.make(getView(), "Edytuj konto", Snackbar.LENGTH_SHORT).show();

                                //sendNotificationToClient();

                                Fragment newFragment = new MyAccountEditFragment();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment, newFragment);

                                Bundle bundle = new Bundle();
                                bundle.putString("user", user.toJSON());
                                newFragment.setArguments(bundle);

                                transaction.addToBackStack(null);
                                transaction.commit();
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
}
