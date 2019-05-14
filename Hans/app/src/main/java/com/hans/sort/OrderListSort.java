package com.hans.sort;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hans.domain.Order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

public class OrderListSort implements Callable<ArrayList<Order>> {

    Context context;
    Activity activity;
    View view;
    ArrayList<Order> list;
    SortType method;
    FusedLocationProviderClient fusedLocationClient;


    public OrderListSort(Context context, Activity activity, View view, ArrayList<Order> list, SortType method) {
        this.context = context;
        this.activity = activity;
        this.view = view;
        this.list = list;
        this.method = method;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public ArrayList<Order> call() {
        Log.d("sort", "Sorting on: " + Thread.currentThread().getName());

        switch (method) {
            case DISTANCE_ASC:
                sortByDistanceAsc();
                break;
            case DISTANCE_DESC:
                sortByDistanceDesc();
                break;
            case PRICE_ASC:
                sortByPriceAsc();
                break;
            case PRICE_DESC:
                sortByPriceDesc();
                break;
            case TIME_ASC:
                sortByOrderTimeAsc();
                break;
            case TIME_DESC:
                sortByOrderTimeDesc();
                break;
        }
        return list;
    }

    private void sortByPriceDesc() {
        Collections.sort(list, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getPrice() < o2.getPrice()) {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void sortByPriceAsc() {
        Collections.sort(list, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getPrice() > o2.getPrice()) {
                    return 1;
                }
                return -1;
            }
        });
    }

    private void sortByDistanceDesc() {
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
                            } else {
                                Snackbar.make(view, "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
                            }
//                            ordersListView.invalidate();
                        }
                    });
        } catch (SecurityException e) {
            Log.d("excepioton", "Permission for getting location denied");
        }
    }

    private void sortByDistanceAsc() {
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
                            } else {
                                Snackbar.make(view, "Nie znaleziono lokalizacji urządzenia.", Snackbar.LENGTH_LONG).show();
                            }
//                            ordersListView.invalidate();
                        }
                    });
        } catch (SecurityException e) {
            Log.d("excepioton", "Permission for getting location denied");
        }
    }

    private float getDistanceToPickupAddress(Order order, Location location) {
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

//        Log.d("distance", "address: " + pickupAddress1);
//        Log.d("distance", Float.toString(distance[0]));
        return distance[0];
    }

    private void sortByOrderTimeAsc() {
        Collections.sort(list, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getDate() != null && o2.getDate() != null) {
                    if (o1.getDate().compareTo(o2.getDate()) > 0) {
                        return 1;
                    }
                }
                return -1;
            }
        });
    }

    private void sortByOrderTimeDesc() {
        Collections.sort(list, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getDate() != null && o2.getDate() != null) {
                    if (o1.getDate().compareTo(o2.getDate()) < 0) {
                        return 1;
                    }
                }
                return -1;
            }
        });
    }

}
