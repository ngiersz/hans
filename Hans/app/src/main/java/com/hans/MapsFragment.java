package com.hans;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    public EditText editText;
    public MarkerOptions Start, Destination;
    public Polyline polyline;
    private String origin, destination;



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
    }

    public void FindOnMap(String locationName){
        Geocoder geocoder = new Geocoder(this.getContext());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            Address address = addressList.get(0);
            String locality = address.getLocality();
            double x = address.getLatitude();
            double y = address.getLongitude();
            GoToPoint(x,y,11);
            mMap.addMarker(new MarkerOptions().position(new LatLng(x,y)));
            //FindTheWay(new LatLng(x,y), new LatLng(x+0.01, y));

        }
        catch (IOException e){

            e.printStackTrace();
        }
        catch (Exception e) {
            GoToPoint(0,0,2);
            mMap.addMarker(new MarkerOptions().position((new LatLng(0,0))));
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
