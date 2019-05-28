package com.hans.sort;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hans.domain.Order;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortFilterOrders
{

    ArrayList<Order> list;
    Context context;
    Activity activity;

    public SortFilterOrders(ArrayList<Order> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    public ArrayList<Order> sortByPriceDesc()
    {
        Collections.sort(list, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getPrice() < o2.getPrice())
                {
                    return 1;
                }
                return -1;
            }
        });
        return list;
    }

    public ArrayList<Order> sortByPriceAsc()
    {
        Collections.sort(list, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            { if (o1.getPrice() > o2.getPrice())
                {
                    return 1;
                }
                return -1;
            }
        });
        return list;
    }

    public ArrayList<Order> sortByDistanceDesc()
    {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                final Location location1 = location;
                                Collections.sort(list, new Comparator<Order>() {
                                    @Override
                                    public int compare(final Order o1, final Order o2) {
                                        if (getDistanceToPickupAddress(o1, location1) > getDistanceToPickupAddress(o2, location1)) {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });
                            }
//                            else {
//                                Snackbar.make(getView(), "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
//                            }
                        }
                    });
        }
        catch (SecurityException e) {
            Log.d("excepioton", "Permission for getting location denied");
        }
        return list;
    }

    public ArrayList<Order> sortByDistanceAsc()
    {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                final Location location1 = location;
                                Collections.sort(list, new Comparator<Order>() {
                                    @Override
                                    public int compare(final Order o1, final Order o2) {
                                        if (getDistanceToPickupAddress(o1, location1) <= getDistanceToPickupAddress(o2, location1)) {
                                            return 1;
                                        }
                                        return -1;
                                    }
                                });
                            }
//                            else {
//                                Snackbar.make(getView(), "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
//                            }
                        }
                    });
        }
        catch (SecurityException e) {
            Log.d("excepioton", "Permission for getting location denied");
        }
        return list;
    }

    public float getDistanceToPickupAddress(Order order, Location location) {
        Geocoder geocoder = new Geocoder(context);
        String pickupAddress1 = order.getPickupAddress().get("city") + " " +
                order.getPickupAddress().get("zipcode") + " " +
                order.getPickupAddress().get("street") + " " +
                order.getPickupAddress().get("number");
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(pickupAddress1, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        double x1 = address.getLatitude();
        double y1 = address.getLongitude();

        float[] distance = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), x1, y1, distance);

        Log.d("distance", "address: " + pickupAddress1);
        Log.d("distance", Float.toString(distance[0]));
        return distance[0];
    }

    public ArrayList<Order> sortByOrderTimeAsc()
    {
        Collections.sort(list, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getDate() != null && o2.getDate() != null)
                {
                    if (o1.getDate().compareTo(o2.getDate()) > 0)
                    {
                        return 1;
                    }
                }
                return -1;
            }
        });
        return list;
    }

    public ArrayList<Order> sortByOrderTimeDesc()
    {
        Collections.sort(list, new Comparator<Order>()
        {
            @Override
            public int compare(Order o1, Order o2)
            {
                if (o1.getDate() != null && o2.getDate() != null)
                {
                    if (o1.getDate().compareTo(o2.getDate()) < 0)
                    {
                        return 1;
                    }
                }
                return -1;
            }
        });
        return list;
    }

    public ArrayList<Order> filterByStartPointCity(String cityToSearch)
    {
        ArrayList<Order> resultArray = new ArrayList<>();
        cityToSearch = Normalizer.normalize(cityToSearch.toLowerCase(), Normalizer.Form.NFD);
        cityToSearch = cityToSearch.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        cityToSearch = cityToSearch.replaceAll("ł", "l");

        for (Order order : list)
        {
            String orderCity = order.getPickupAddress().get("city").toString().toLowerCase();
            orderCity = Normalizer.normalize(orderCity, Normalizer.Form.NFD);
            orderCity = orderCity.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            orderCity = orderCity.replaceAll("ł", "l");

            if (orderCity.matches(cityToSearch))
                resultArray.add(order);
        }
        return resultArray;
    }

    public ArrayList<Order> filterByEndPointCity(String cityToSearch)
    {
        ArrayList<Order> resultArray = new ArrayList<>();
        cityToSearch = Normalizer.normalize(cityToSearch.toLowerCase(), Normalizer.Form.NFD);
        cityToSearch = cityToSearch.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        cityToSearch = cityToSearch.replaceAll("ł", "l");

        for (Order order : list)
        {
            String orderCity = order.getDeliveryAddress().get("city").toString().toLowerCase();
            orderCity = Normalizer.normalize(orderCity, Normalizer.Form.NFD);
            orderCity = orderCity.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            orderCity = orderCity.replaceAll("ł", "l");
            if (orderCity.matches(cityToSearch))
                resultArray.add(order);
        }
        return resultArray;
    }

    public ArrayList<Order> filterByDate(String pickedDate)
    {
        ArrayList<Order> resultArray = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (Order order : list)
        {
            if (order.getDate() == null)
                continue;
            cal.setTime(order.getDate());
            Log.d("dataaa", order.getDate().toString());
            String orderDate = cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR);
            if (orderDate.equals(pickedDate))
                resultArray.add(order);
        }
        return resultArray;
    }
}
