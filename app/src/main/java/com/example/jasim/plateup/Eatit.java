package com.example.jasim.plateup;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jasim.plateup.adapters.OrderAdapterU;
import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.settings.SettingsUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Eatit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView mOrdersList;
    private RecyclerView mPastOrdersList;
    private TabItem mActive;
    private TabItem mPast;
    private TabLayout mTabLayout;
    private TextView noNewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eatit);
        MainUActivity.MyTask task = new MainUActivity.MyTask();
        task.execute("pool.ntp.org");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("orders");
            mOrdersList = findViewById(R.id.order_list);
            mPastOrdersList = findViewById(R.id.pastorder_list);
            mTabLayout = findViewById(R.id.tab_host);
            noNewOrders = findViewById(R.id.no_new_orders);

            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getPosition() == 0) {
                        mPastOrdersList.setVisibility(View.GONE);
                        mOrdersList.setVisibility(View.VISIBLE);
                    }
                    else {
                        mPastOrdersList.setVisibility(View.VISIBLE);
                        mOrdersList.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });



            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            final ArrayList<Order> order = new ArrayList<>();
            final ArrayList<Order> pastOrders = new ArrayList<>();
            final OrderAdapterU orderAdapter = new OrderAdapterU(Eatit.this, order);
            final OrderAdapterU pastOrderAdapter = new OrderAdapterU(Eatit.this, pastOrders);
            pastOrderAdapter.setPastOrders(true);
            mOrdersList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mOrdersList.setAdapter(orderAdapter);
            LinearLayoutManager m = new LinearLayoutManager(getApplicationContext());
            m.setReverseLayout(true);
            m.setStackFromEnd(true);
            mPastOrdersList.setLayoutManager(m);
            mPastOrdersList.setAdapter(pastOrderAdapter);
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Order singleOrder = dataSnapshot.getValue(Order.class);
                    System.out.println("How many orders? time or arrival: " + singleOrder.getTimeOfArrival() + " getDateofArrival " + singleOrder.getDateOfArrival());
                    SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

                    try {
                        Date newD = simpleFormat.parse(singleOrder.getTimeOfArrival() + " " + singleOrder.getDateOfArrival());
                        String currentTime = MainUActivity.time.substring(0,2) + ":" + MainUActivity.time.substring(2,4) + " " + MainUActivity.toDaysDate;
                        Date currentD = simpleFormat.parse(currentTime);
                        System.out.println("Exact time comparison: current: " + MainUActivity.time.substring(0,2) + ":" + MainUActivity.time.substring(2,4) + " " + MainUActivity.toDaysDate + " arrival: " + newD);
                        if(currentD.before(newD)) {
                            noNewOrders.setVisibility(View.GONE);
                            order.add(singleOrder);
                        }
                        else {
                            pastOrders.add(singleOrder);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    orderAdapter.notifyDataSetChanged();
                    pastOrderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Order singleOrder;
                    for (Order o: order) {
                        if(o.getId().equals(dataSnapshot.getValue(Order.class).getId())) {
                            order.remove(o);
                            orderAdapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mDatabase.addChildEventListener(childEventListener);
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feed) {
            startActivity(new Intent(Eatit.this, MainUActivity.class));
        } else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_category) {
            startActivity(new Intent(Eatit.this, Categories.class));
        } else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_orders) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(1);
            startActivity(new Intent(Eatit.this, Eatit.class));
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(Eatit.this, SettingsUser.class));
        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut(); //End user session
            startActivity(new Intent(Eatit.this, StartActivity.class)); //Go back to home page
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
