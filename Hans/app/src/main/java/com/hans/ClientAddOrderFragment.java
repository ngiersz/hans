package com.hans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ClientAddOrderFragment extends Fragment
{

    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_add_order, container, false);
        Button button = view.findViewById(R.id.addOrderButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextView from = view.findViewById(R.id.from);
                TextView to = view.findViewById(R.id.to);
                TextView price = view.findViewById(R.id.price);
                TextView description = view.findViewById(R.id.description);
                TextView weight = view.findViewById(R.id.weight);




            }
        });


//        return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }
}
