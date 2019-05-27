package com.hans.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hans.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.round;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    public EditText editText;
    public MarkerOptions Start, Destination;
    public Polyline polyline;
    private String origin, destination;
    private  Activity activity;
    private List<LatLng> markers = new ArrayList<>();
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.activity = activity;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
               .findFragmentById(R.id.map);

       mapFragment.getMapAsync(this);


       Bundle bundle = this.getArguments();
       if (bundle != null) {
           origin = bundle.get("origin").toString();
           destination = bundle.get("destination").toString();
       }
       return v;
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//        editText = (EditText)findViewById(R.id.editText);
//    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SetTwoPoints(origin, destination);
    }

    public void GoToPoint(double x, double y, float zoom) {
        LatLng latLng =  new LatLng(x,y);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    public void SetTwoPoints(String location1, String location2) {
        FindOnMap(location1);
        FindOnMap(location2);
        LatLngBounds.Builder bounds = LatLngBounds.builder();
        try
        {
            bounds.include(markers.get(0));
            bounds.include(markers.get(1));
        }
        catch (IndexOutOfBoundsException e)
        {
            Log.d("exception:", e.getMessage());
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Błąd!")
                    .setMessage("Wystąpił nieoczekiwany błąd. Przepraszamy ;(")
                    .setCancelable(false)
                    .setNeutralButton("Ok, zamknij", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getActivity().finish();
                        }
                    });

        }

        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150));
        }
        catch (Exception e){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0),15));
        }
    }

    public static Map<String, Object> GetPriceAndDistance(Context context, String location1, String location2, Double weight) {

        Map<String, Object> result = new HashMap<String, Object>();
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addressList = geocoder.getFromLocationName(location1, 1);
            Address address = addressList.get(0);
            double x1 = address.getLatitude();
            double y1 = address.getLongitude();

            addressList = geocoder.getFromLocationName(location2,1);
            address = addressList.get(0);
            double x2= address.getLatitude();
            double y2 = address.getLongitude();

            float[] distance = new float[1];
            Location.distanceBetween(x1,y1,x2,y2,distance);

            double price = Math.log(((distance[0]/1000)+30)/30)*55;

            if (price < 10)
                price = 10;

            double priceForWeight = weight/20;
            if (weight > 50)
                priceForWeight = weight / 15;
            if (weight > 100)
                priceForWeight = weight / 12;
            if (weight > 500)
                priceForWeight = weight / 10;

            price += priceForWeight;

            result.put("distance", Math.round((distance[0]/1000) * 100.0)/100.0);
            result.put("price", Math.round(price * 100.0)/100.0);
        }
        catch (Exception e){
            result.put("distance", 0);
            result.put("price", 0);
            return result;
        }
        return result;
    }

    public void FindOnMap(String locationName){
        Geocoder geocoder = new Geocoder(this.getContext());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            Address address = addressList.get(0);
            String locality = address.getLocality();
            double x = address.getLatitude();
            double y = address.getLongitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(x,y)).title(locationName));
            markers.add(new LatLng(x,y));
            //FindTheWay(new LatLng(x,y), new LatLng(x+0.01, y));

        }
        catch (IOException e){

            e.printStackTrace();
        }
        catch (Exception e) {
            GoToPoint(0,0,2);
            mMap.addMarker(new MarkerOptions().position((new LatLng(0,0))));
            markers.add(new LatLng(0,0));
        }
    }

    public void FindTheWay(LatLng start, LatLng destination) {
        this.Start = new MarkerOptions().position(start);
        this.Destination = new MarkerOptions().position(destination);

        String url = getUrl(Start.getPosition(), Destination.getPosition(), "driving");
        new FetchURL(MapsFragment.this.getContext()).execute(url,"drivinig");
        mMap.addMarker(Start);
        mMap.addMarker(Destination);
        polyline = mMap.addPolyline(new PolylineOptions().add(Start.getPosition(), Destination.getPosition()).width(10).color(Color.RED));
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=" + directionMode;
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
}
