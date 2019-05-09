package com.hans;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hans.BroadcastReceivers.CheckInternetConnectionReceiver;
import com.hans.client.ClientAddOrderFragment;
import com.hans.client.ClientWaitingsOrdersFragment;
import com.hans.client.ClientArchiveOrdersFragment;
import com.hans.client.ClientInTransitOrdersFragment;
import com.hans.client.ClientMenuFragment;
import com.hans.deliverer.DelivererAvailableOrdersFragment;
import com.hans.deliverer.DelivererArchiveOrdersFragment;
import com.hans.deliverer.DelivererInTransitOrdersFragment;
import com.hans.pdf.SignDocumentFragment;

public class MainActivity extends AppCompatActivity
{
    private final int RC_SIGN_IN_WITH_GOOGLE = 1;
    private final int MY_PERMISSIONS_REQUEST_WRITE_READ_EXTERNAL_STORAGE = 2;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BroadcastReceiver br = new CheckInternetConnectionReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);

        // Set a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        // Find our drawer view
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.inflateHeaderView(R.layout.menu_header_deliverer);
        navigationView.getHeaderView(1).setVisibility(View.GONE);
        setupDrawerContent(navigationView);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // Check if user is logged.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null)
        {
            Intent signInIntent = new Intent(this, SignInGoogleActivity.class);
            startActivityForResult(signInIntent, RC_SIGN_IN_WITH_GOOGLE);
        } else
        {
            Fragment mapsActivity = new ClientWaitingsOrdersFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, mapsActivity);
            transaction.addToBackStack(null);
            transaction.commit();
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_WRITE_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_WRITE_READ_EXTERNAL_STORAGE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission granted.
                } else
                {
                    // Permission not granted.
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Brak dostepu do pamięci wewnętrznej. Proszę dać uprawnienia aplikacji do poprawnego funkcjonowania.")
                            .setPositiveButton("Ok. Zrobię to!", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Wyjdź", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // User cancelled the dialog
                                    finish();
                                }
                            });
                    builder.create().show();
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == RC_SIGN_IN_WITH_GOOGLE)
            {
//                firebaseUser = (FirebaseUser) data.getExtras().get("userFirebase");
//                String userJSON = data.getStringExtra("userJSON");
//                user = User.createFromJSON(userJSON);

                Fragment mapsActivity = new ClientWaitingsOrdersFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, mapsActivity);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem)
    {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId())
        {
            case R.id.new_order:
                fragmentClass = ClientAddOrderFragment.class;
                Log.d("menu", "nowe zlecenie");
                break;
            case R.id.waiting_orders:
                fragmentClass = ClientWaitingsOrdersFragment.class;
                Log.d("menu", "oczekujące");
                break;
            case R.id.in_process_orders_client:
                fragmentClass = ClientInTransitOrdersFragment.class;
                Log.d("menu", "zlecenia w trakcie");
                break;
            case R.id.archive_orders_client:
                fragmentClass = ClientArchiveOrdersFragment.class;
                Log.d("menu", "historia klienta");
                break;
            case R.id.search_new_orders:
                fragmentClass = DelivererAvailableOrdersFragment.class;
                Log.d("menu", "szukaj zleceń");
                break;
            case R.id.in_process_orders_deliverer:
                fragmentClass = DelivererInTransitOrdersFragment.class;
                Log.d("menu", "zlecenie w trakcie wykonywania");
                break;
            case R.id.archive_orders_deliverer:
                fragmentClass = DelivererArchiveOrdersFragment.class;
                Log.d("menu", "historia dostawcy");
                break;
            case R.id.my_account:
                fragmentClass = MyAccountFragment.class;
                Log.d("menu", "moje konto");
                break;
            case R.id.settings:
                fragmentClass = SignDocumentFragment.class;
                Log.d("menu", "ustawienia");
                break;
            case R.id.change_to_client:
                Log.d("menu", "klient");
                fragmentClass = ClientWaitingsOrdersFragment.class;
                navigationView.getMenu().clear();
                navigationView.removeHeaderView(navigationView.getHeaderView(R.layout.menu_header_deliverer));
                navigationView.getHeaderView(1).setVisibility(View.GONE);
                navigationView.getHeaderView(0).setVisibility(View.VISIBLE);
                navigationView.inflateMenu(R.menu.menu_client);
                break;
            case R.id.change_to_deliverer:
                Log.d("menu", "dostawca");
                fragmentClass = DelivererAvailableOrdersFragment.class;
                navigationView.getMenu().clear();
                navigationView.getHeaderView(0).setVisibility(View.GONE);
                navigationView.getHeaderView(1).setVisibility(View.VISIBLE);
                navigationView.inflateMenu(R.menu.menu_deliverer);
                break;
            default:
                fragmentClass = ClientMenuFragment.class;
        }

        try
        {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    public void setActionBarTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

    public static void closeKeyboard(Activity activity)
    {
        View view = activity.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
