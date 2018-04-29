package com.example.jasim.plateup;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jasim.plateup.adapters.OrderAdapterR;
import com.example.jasim.plateup.bookings.MyRestaurantBookingsActivity;
import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.settings.SettingsAuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PastOrdersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView mBookings;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String name;
    private TextView nav_user;
    private MenuItem openClose;
    private MenuItem waitingTime;
    private DatabaseReference mWaitTime;
    private boolean isClosed = false;
    private DatabaseReference mRef;
    private static RestaurantModel mRestaurantModel;
    private RecyclerView mOrderList;
    private ArrayList<Order> completeOrders;
    private RecyclerView mOrdersList;

    private Button btnPrint;
    private Button mMinus;
    private Button mPlus;
    private TextView mAmount;

    public static void setmRestaurantModel(RestaurantModel mRestaurantModel) {
        PastOrdersActivity.mRestaurantModel = mRestaurantModel;
    }

    public static RestaurantModel getRestaurant() {
        return mRestaurantModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_r);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("orders").child("printed");
        mOrderList = findViewById(R.id.order_list);
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("status");
        mWaitTime = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("waittime");
        completeOrders = new ArrayList<>();
        System.out.println("completeOrders size: " + completeOrders.size());
        final OrderAdapterR orderAdapter = new OrderAdapterR(this, completeOrders);
        orderAdapter.setPastOrder(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mOrderList.setLayoutManager(mLayoutManager);
        mOrderList.setAdapter(orderAdapter);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Order singleOrder = dataSnapshot.getValue(Order.class);
                completeOrders.add(singleOrder);
                findViewById(R.id.no_new_orders).setVisibility(View.GONE);
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                completeOrders.remove(dataSnapshot.getValue(Order.class));
                if(completeOrders.size() == 0) {
                    findViewById(R.id.no_new_orders).setVisibility(View.VISIBLE);
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



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        // find MenuItem you want to change
        final MenuItem openClose = menu.findItem(R.id.open_close);
        waitingTime = menu.findItem(R.id.waiting_time);

        final ArrayList<String> keys = new ArrayList<>();
        final TextView showWaitTime = (TextView)menu.findItem(R.id.waiting_time).getActionView();

        mWaitTime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String test = (String)dataSnapshot.getValue();
                showWaitTime.setText(test + "min");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Hente en verdi i stedenfor hele
                String test = (String)dataSnapshot.getValue();
                if(test.equals("open")) {
                    isClosed = false;
                    openClose.setTitle("Busy? Close the restaurant");
                }
                else{
                    isClosed = true;
                    openClose.setTitle("Quiet? Open the restaurant");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        waitingTime.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(PastOrdersActivity.this, WaitingActivity.class));
                return false;
            }
        });

        openClose.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!isClosed) {
                    openClose.setTitle("Quiet? Open the restaurant");

                    isClosed = true;

                }
                else {
                    openClose.setTitle("Busy? Close the restaurant");
                    isClosed = false;
                }
                if(isClosed) {
                    mRef.setValue("closed");
                }
                else {
                    mRef.setValue("open");
                }
                return false;
            }
        });
        // set new title to the MenuItem
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(PastOrdersActivity.this, MainActivity.class);

                    startActivity(loginIntent);

                }

            }
        };
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_r, menu);
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

        else if(id == R.id.action_add) {
            startActivity(new Intent(PastOrdersActivity.this, PostContent.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {
        }
        else if (id == R.id.nav_profile) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mRestaurantModel = dataSnapshot.getValue(RestaurantModel.class);
                    startActivity(new Intent(PastOrdersActivity.this, ProfileRestaurant.class).putExtra("restaurant", mRestaurantModel.getId()));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    // Logic for failure is here...
                }
            });

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(PastOrdersActivity.this, PastOrdersActivity.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(PastOrdersActivity.this, WaitingActivity.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(PastOrdersActivity.this, SettingsAuthActivity.class));

        } else if (id == R.id.nav_send) {
            mAuth.signOut(); //End user session
            startActivity(new Intent(PastOrdersActivity.this, MainActivity.class)); //Go back to home page
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Eroor", "Error getting bitmap", e);
        }
        return bm;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setOrderId(String id) {
            TextView order_id = (TextView)mView.findViewById(R.id.order_id);
            order_id.setText(id);
        }
        public void setDishname(String dishname) {
            TextView content_dish = (TextView) mView.findViewById(R.id.content_dish);
            content_dish.setText(dishname);
        }

        public void setDescription(String description) {
            TextView content_description = (TextView) mView.findViewById(R.id.content_description);
            content_description.setText(description);
        }

        public void setPrice(String price) {
            TextView content_price = (TextView) mView.findViewById(R.id.content_price);
            content_price.setText(price);
        }

    }
}
