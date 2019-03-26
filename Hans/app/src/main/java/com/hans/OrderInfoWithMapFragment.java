package com.hans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hans.domain.Order;

public class OrderInfoWithMapFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_order_info_with_map, container, false);


        Fragment mapsActivity = new MapsFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment, mapsActivity);

        Bundle mapsBundle = new Bundle();
        mapsBundle.putString("origin", "Poznan");
        mapsBundle.putString("destination", "Warszawa");
        mapsActivity.setArguments(mapsBundle);

        transaction.addToBackStack(null);
        transaction.commit();

        return view;
    }
}
