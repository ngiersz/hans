package com.hans;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hans.client.ClientAddOrderFragment;
import com.hans.client.ClientAllWaitingsOrdersFragment;
import com.hans.client.ClientMenuFragment;
import com.hans.deliverer.DelivererAllOrdersFragment;
import com.hans.deliverer.DelivererMenuFragment;
import com.hans.domain.User;

public class MainActivity extends AppCompatActivity
{
    private final int RC_SIGN_IN_WITH_GOOGLE = 1;


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FirebaseUser firebaseUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Check if user is logged.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null)
        {
            Intent signInIntent = new Intent(getBaseContext(), SignInGoogleActivity.class);
            startActivityForResult(signInIntent, RC_SIGN_IN_WITH_GOOGLE);
        }
        else Log.d("koy", "create" + firebaseUser.getEmail());

        Fragment mapsActivity = new ClientAllWaitingsOrdersFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, mapsActivity);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if (requestCode == RC_SIGN_IN_WITH_GOOGLE)
            {
                firebaseUser = (FirebaseUser) data.getExtras().get("userFirebase");
                String userJSON = data.getStringExtra("userJSON");
                user = User.createFromJSON(userJSON);
            }
        }
        else
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
                fragmentClass = ClientAllWaitingsOrdersFragment.class;
                Log.d("menu", "oczekujące");
                break;
            case R.id.in_process_orders:
                fragmentClass =  ClientMenuFragment.class;
                Log.d("menu", "zlecenia w trakcie");
                break;
            case R.id.search_new_orders:
                fragmentClass = DelivererAllOrdersFragment.class;
                Log.d("menu", "szukaj zleceń");
                break;
            case R.id.in_process_order:
                fragmentClass = DelivererMenuFragment.class;
                Log.d("menu", "zlecenie w trakcie wykonywania");
                break;
            case R.id.my_account:
                fragmentClass = DelivererMenuFragment.class;
                Log.d("menu", "moje konto");
                break;
            case R.id.settings:
                fragmentClass = DelivererMenuFragment.class;
                Log.d("menu", "ustawienia");
                break;
            case R.id.change_to_client:
                Log.d("menu", "klient");
                fragmentClass = ClientAllWaitingsOrdersFragment.class;
                navigationView.getMenu().clear();
                navigationView.removeHeaderView(navigationView.getHeaderView(R.layout.menu_header_deliverer));
                navigationView.getHeaderView(1).setVisibility(View.GONE);
                navigationView.getHeaderView(0).setVisibility(View.VISIBLE);
                navigationView.inflateMenu(R.menu.menu_client);
                break;
            case R.id.change_to_deliverer:
                Log.d("menu", "dostawca");
                fragmentClass = DelivererAllOrdersFragment.class;
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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
