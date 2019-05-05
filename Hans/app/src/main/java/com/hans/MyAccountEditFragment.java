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
import android.widget.EditText;
import android.widget.TextView;

import com.hans.domain.User;

public class MyAccountEditFragment extends Fragment {
    User user;
    DatabaseFirebase db = new DatabaseFirebase();
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_account_edit, container, false);
        Bundle bundle = this.getArguments();
        String userJSON = bundle.getString("user");
        user = User.createFromJSON(userJSON);
        Log.d("my_acc", user.toString());


        EditText userName = view.findViewById(R.id.usertName);
        EditText userSurName = view.findViewById(R.id.userSurName);
        EditText userEmail = view.findViewById(R.id.userEmail);
        EditText userPhone = view.findViewById(R.id.userPhone);


        userName.setText(user.getName());
        userSurName.setText(user.getSurname());
        userEmail.setText(user.getGoogleEmail());
        userPhone.setText(user.getPhoneNumber());
        Button button = view.findViewById(R.id.accept_changes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
                buider.setMessage("Czy na pewno chcesz zapisć zmiany?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Snackbar.make(getView(), "Zapisz zmiany", Snackbar.LENGTH_SHORT).show();

                                updateUser();


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
    private void updateUser(){
        if(checkIfAllCompletedCorrectly()){

            EditText userName = view.findViewById(R.id.usertName);
            EditText userSurName = view.findViewById(R.id.userSurName);
            EditText userEmail = view.findViewById(R.id.userEmail);
            EditText userPhone = view.findViewById(R.id.userPhone);

            user.setName(userName.getText().toString());
            user.setSurname(userSurName.getText().toString());
            user.setPhoneNumber(userPhone.getText().toString());
            user.setGoogleEmail(userEmail.getText().toString());

            Log.d("my_acc", user.toString());

            db.insertUserToDatabase(user);
            Fragment newFragment = new MyAccountFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, newFragment);

            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    private boolean checkIfAllCompletedCorrectly()
    {

        EditText phoneNumber = view.findViewById(R.id.userPhone);


        String phoneText = phoneNumber.getText().toString();


        if (!phoneText.matches("([+][0-9]{2})?[0-9]{9}"))
        {
            Snackbar.make(view.findViewById(android.R.id.content), "Zły format numeru telefonu (+48123456789 LUB 123456789).", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
        }
