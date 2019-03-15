package com.hans;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.hans.domain.Order;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initOrderList();
        ListView listView = (ListView) findViewById(R.id.listView);
        OrderListAdapter orderListAdapter = new OrderListAdapter(this, R.layout.adapter_view_layout, orders);
        listView.setAdapter(orderListAdapter);
    }

    private void initOrderList() {
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
        orders.add(new Order("add1", "add2", "desc"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
