package com.hans.client;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hans.DatabaseFirebase;
import com.hans.MainActivity;
import com.hans.R;
import com.hans.domain.Order;
import com.hans.map.MapsFragment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientAddOrderFragment extends Fragment
{

    private View view;

    TextView fromCity;
    TextView fromZipCode;
    TextView fromStreet;
    TextView fromNumber;
    TextView toCity;
    TextView toZipCode;
    TextView toStreet;
    TextView toNumber;
    TextView weight;
    TextView price ;
    TextView distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_client_add_order, container, false);

        fromCity = view.findViewById(R.id.fromCity);
        fromZipCode = view.findViewById(R.id.fromZipCode);
        fromStreet = view.findViewById(R.id.fromStreet);
        fromNumber = view.findViewById(R.id.fromNumber);

        toCity = view.findViewById(R.id.toCity);
        toZipCode = view.findViewById(R.id.toZipCode);
        toStreet = view.findViewById(R.id.toStreet);
        toNumber = view.findViewById(R.id.toNumber);

        price = view.findViewById(R.id.price);
        distance = view.findViewById(R.id.distance);
        weight = view.findViewById(R.id.weight);

        Button button = view.findViewById(R.id.addOrderButton);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!checkIfCorrectlyToAddOrder())
                {
                    Snackbar.make(getView(), "Należy obliczyć cenę", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                TextView description = view.findViewById(R.id.description);
                TextView width = view.findViewById(R.id.width);
                TextView height = view.findViewById(R.id.height);
                TextView depth = view.findViewById(R.id.depth);

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

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                Timestamp currentDate = Timestamp.now();
                Log.d("add", "date: " + currentDate);

                Order order = new Order(pickupAddress, deliveryAddress,
                        distanceDouble, priceDouble, weightDouble, dimensions,
                        description.getText().toString(), firebaseUser.getUid(), currentDate);

                DatabaseFirebase db = new DatabaseFirebase();
                db.insertOrderToDatabase(order);
                Snackbar.make(getView(), "Dodano zlecenie.", Snackbar.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        });

        Button checkPrice = view.findViewById(R.id.checkPriceButton);
        checkPrice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.closeKeyboard(getActivity());

                if (!checkIfCorrectlyToComputePrice())
                {
                    Snackbar.make(getView(), "Błędne dane. Potrzeba punkt początkowy, końcowy oraz wagę", Snackbar.LENGTH_LONG).show();
                    return;
                }

                String location1 = fromCity.getText().toString() + " " +
                        fromZipCode.getText().toString() + " " +
                        fromStreet.getText().toString() + " " +
                        fromNumber.getText().toString();
                String location2 = toCity.getText().toString() + " " +
                        toZipCode.getText().toString() + " " +
                        toStreet.getText().toString() + " " +
                        toNumber.getText().toString();

                Double weightDouble = Double.parseDouble(weight.getText().toString());
                Map<String, Object> result = new MapsFragment().GetPriceAndDistance(getContext(), location1, location2, weightDouble);
                Log.d("price", "distance=" + result.get("distance").toString() + " price=" + result.get("price").toString());
                price.setText(result.get("price").toString());
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
