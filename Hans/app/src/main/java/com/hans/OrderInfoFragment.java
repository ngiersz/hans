package com.hans;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.hans.domain.Order;

public class OrderInfoFragment extends Fragment
{
    public OrderInfoFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_order_info, container, false);
        Fragment mapsActivity = new MapsActivity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, mapsActivity);

        Bundle bundle = new Bundle();
        bundle.putString("origin", "Opalenicka 51");
        bundle.putString("destination", "św. michała 100");
        mapsActivity.setArguments(bundle);

        transaction.addToBackStack(null);
        transaction.commit();

        Log.d("orderinfo", "OrderInfoFragment started");
        return v;
    }
}
