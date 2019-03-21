package com.hans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.hans.domain.Order;

import java.util.ArrayList;

public class DelivererAllOrdersActivity extends AppCompatActivity {

    ArrayList<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        orderListInit();
        ListView listView = findViewById(R.id.listView);
        OrderListAdapter orderListAdapter = new OrderListAdapter(this, R.layout.adapter_view_layout, orderList);
        listView.setAdapter(orderListAdapter);
    }

    public void showOrderInfo(View view) {
        Log.d("button", "button clicked, function showOrderInfo");
        Intent intent = new Intent(DelivererAllOrdersActivity.this, OrderInfoActivity.class);
        startActivity(intent);
    }

    private void orderListInit() {
        orderList.add(new Order(1, "Piotrowo 3, 60-101 Poznań", "Piotrowo 3, 60-101 Poznań", 10.5, "1.785m x 2.128m x 5.348m", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"", 20.0));
        orderList.add(new Order(2, "Piotrowo 3, 60-101 Poznań", "Piotrowo 3, 60-101 Poznań", 10.5, "1m x 2m", "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"", 20.0));
        orderList.add(new Order(3, "adr5", "adr6", 10.5, "1m x 2m", "description3", 20.0));
        orderList.add(new Order(4, "adr7", "adr8", 10.5, "1m x 2m", "description4", 20.0));
        orderList.add(new Order(5, "adr9", "adr10", 10.5, "1m x 2m", "description5", 20.0));
        orderList.add(new Order(1, "adr1", "adr2", 10.5, "1m x 2m", "description1", 20.0));
        orderList.add(new Order(2, "adr3", "adr4", 10.5, "1m x 2m", "description2", 20.0));
        orderList.add(new Order(3, "adr5", "adr6", 10.5, "1m x 2m", "description3", 20.0));
        orderList.add(new Order(4, "adr7", "adr8", 10.5, "1m x 2m", "description4", 20.0));
        orderList.add(new Order(5, "adr9", "adr10", 10.5, "1m x 2m", "description5", 20.0));


        databaseFirebase db = new databaseFirebase();

        for(Order order : orderList){
           // db.insertOrderToDatabase(order);
        }
        ArrayList<Order> orderListTest = new ArrayList<>();
        orderListTest = db.getAllOrdersToDeliver();
        for(Order order : orderListTest){
           Log.d("Order", "#####################SPAM"+order.toString());
        }
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
            Intent intent = new Intent(this, signInDeliverer.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
