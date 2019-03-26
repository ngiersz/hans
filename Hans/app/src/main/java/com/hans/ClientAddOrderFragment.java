package com.hans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hans.domain.Order;

import java.util.HashMap;
import java.util.Map;

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
                if (!checkIfCorrectlyToAddOrder())
                {
                    Toast.makeText(getActivity().getBaseContext(), "Należy obliczyć cenę.", Toast.LENGTH_SHORT).show();
                    return;
                }
                TextView fromCity = view.findViewById(R.id.fromCity);
                TextView fromZipCode = view.findViewById(R.id.fromZipCode);
                TextView fromStreet = view.findViewById(R.id.fromStreet);
                TextView fromNumber = view.findViewById(R.id.fromNumber);

                TextView toCity = view.findViewById(R.id.toCity);
                TextView toZipCode = view.findViewById(R.id.toZipCode);
                TextView toStreet = view.findViewById(R.id.toStreet);
                TextView toNumber = view.findViewById(R.id.toNumber);

                TextView price = view.findViewById(R.id.price);
                TextView description = view.findViewById(R.id.description);
                TextView weight = view.findViewById(R.id.weight);
                TextView width = view.findViewById(R.id.width);
                TextView height = view.findViewById(R.id.height);
                TextView depth = view.findViewById(R.id.depth);
                TextView distance = view.findViewById(R.id.distance);

                Map<String, Object> pickupAddress = new HashMap<>();
                pickupAddress.put("city", fromCity.getText().toString());
                pickupAddress.put("zipCode", fromZipCode.getText().toString());
                pickupAddress.put("street", fromStreet.getText().toString());
                pickupAddress.put("number", fromNumber.getText().toString());

                Map<String, Object> deliveryAddress = new HashMap<>();
                deliveryAddress.put("city", toCity.getText().toString());
                deliveryAddress.put("zipCode", toZipCode.getText().toString());
                deliveryAddress.put("street", toStreet.getText().toString());
                deliveryAddress.put("number", toNumber.getText().toString());

                Map<String, Object> dimensions = new HashMap<>();
                dimensions.put("width", width.getText().toString());
                dimensions.put("height", height.getText().toString());
                dimensions.put("depth", depth.getText().toString());

                Double priceDouble = Double.parseDouble(price.getText().toString());
                Double weightDouble = Double.parseDouble(weight.getText().toString());

                Double distanceDouble = Double.parseDouble(distance.getText().toString());
                distanceDouble = Double.parseDouble(weight.getText().toString());

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                Order order = new Order(pickupAddress, deliveryAddress,
                        distanceDouble, priceDouble, weightDouble, dimensions,
                        description.getText().toString(), firebaseUser.getUid());

                databaseFirebase db = new databaseFirebase();
                db.insertOrderToDatabase(order);
                Toast.makeText(getContext(), "Dodano zlecenie.", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        });

        Button checkPrice = view.findViewById(R.id.checkPriceButton);
        checkPrice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!checkIfCorrectlyToComputePrice())
                {
                    Toast.makeText(getActivity().getBaseContext(), "Błędę dane. Potrzeba punkt początkowy, końcowy oraz wagę", Toast.LENGTH_LONG).show();
                    return;
                }

                TextView fromCity = view.findViewById(R.id.fromCity);
                TextView fromZipCode = view.findViewById(R.id.fromZipCode);
                TextView fromStreet = view.findViewById(R.id.fromStreet);
                TextView fromNumber = view.findViewById(R.id.fromNumber);

                TextView toCity = view.findViewById(R.id.toCity);
                TextView toZipCode = view.findViewById(R.id.toZipCode);
                TextView toStreet = view.findViewById(R.id.toStreet);
                TextView toNumber = view.findViewById(R.id.toNumber);

                String location1 = fromCity.getText().toString() +
                        fromZipCode.getText().toString() +
                        fromStreet.getText().toString() +
                        fromNumber.getText().toString();
                String location2 = toCity.getText().toString() +
                        toZipCode.getText().toString() +
                        toStreet.getText().toString() +
                        toNumber.getText().toString();

                TextView weight = view.findViewById(R.id.weight);
                Double weightDouble = Double.parseDouble(weight.getText().toString());

                Map<String, Object> result = new MapsFragment().GetPriceAndDistance(getContext(), location1, location2, weightDouble);

                TextView price = view.findViewById(R.id.price);
                price.setText(result.get("price").toString());
                TextView distance = view.findViewById(R.id.distance);
                distance.setText(result.get("distance").toString());

            }
        });

//        return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    private boolean checkIfCorrectlyToComputePrice()
    {
        String fromCity = ((TextView)view.findViewById(R.id.fromCity)).getText().toString();
        String toCity = ((TextView)view.findViewById(R.id.toCity)).getText().toString();
        String weight = ((TextView)view.findViewById(R.id.weight)).getText().toString();

        if (!fromCity.isEmpty() & !toCity.isEmpty() & !weight.isEmpty())
            return  true;
        else return  false;
    }

    private boolean checkIfCorrectlyToAddOrder()
    {
        String price = ((TextView)view.findViewById(R.id.price)).getText().toString();

        if (!price.isEmpty())
            return  true;
        else return  false;
    }

}
