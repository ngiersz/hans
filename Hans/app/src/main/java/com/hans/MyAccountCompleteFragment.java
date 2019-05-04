package com.hans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

        return view;
    }
}
