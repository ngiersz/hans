package com.hans.pdf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hans.MainActivity;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.domain.User;

public class GetReceiverNameFragment extends Fragment
{
    private View view;
    private Order order;
    private  User client, deliverer;
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_get_receiver_name, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Dane odbiorcy");

        Bundle bundle = this.getArguments();
        String orderJSON = bundle.getString("order");
        String clientJSON = bundle.getString("client");
        String delivererJSON = bundle.getString("deliverer");
        order = Order.createFromJSON(orderJSON);
        client = User.createFromJSON(clientJSON);
        deliverer = User.createFromJSON(delivererJSON);

        final TextView firstname = view.findViewById(R.id.receiver_firstname);
        final TextView lastname = view.findViewById(R.id.receiver_lastname);



        Button nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.closeKeyboard(getActivity());
                String receiver_firstname = firstname.getText().toString();
                String receiver_lastname = lastname.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("receiver_firstname", receiver_firstname);
                bundle.putString("receiver_lastname", receiver_lastname);
                bundle.putString("order", order.toJSON());
                bundle.putString("client", client.toJSON());
                bundle.putString("deliverer", deliverer.toJSON());

                Intent newIntent = new Intent(getContext(), SignDocumentActivity.class);
                newIntent.putExtra("receiver_firstname", receiver_firstname);
                newIntent.putExtra("receiver_lastname", receiver_lastname);
                newIntent.putExtra("order", order.toJSON());
                newIntent.putExtra("client", client.toJSON());
                newIntent.putExtra("deliverer", deliverer.toJSON());
                startActivity(newIntent);

//                Fragment newFragment = new SignDocumentFragment();
//                newFragment.setArguments(bundle);
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });

        return view;
    }

}
