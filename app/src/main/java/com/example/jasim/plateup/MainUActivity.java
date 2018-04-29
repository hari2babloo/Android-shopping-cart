package com.example.jasim.plateup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.Check.Connection;
import com.example.jasim.plateup.adapters.FeedAdapter;
import com.example.jasim.plateup.adapters.RestaurantAdapter;
import com.example.jasim.plateup.bookings.MyBookingsUser;
import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.settings.SettingsUser;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainUActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean hasReloadeded = false;
    private static ArrayList<Content> basketList = new ArrayList<>();
    private RecyclerView mContentList;
    private RecyclerView mRestaurantList;
    public static int totalPrice;
    private DatabaseReference mDatabase;
    private Button eat_it;
    private TextView mShowOpen;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mShowUsername;
    private FloatingActionButton fab;
    private String type = "";
    private RestaurantModel modelO;
    private String status;
    private Openings openingb;
    private HashMap<String, Openings> openingss;
    private static Date dayNtime;
    private SwipeRefreshLayout swipeRefreshLayout;

    private SwipeRefreshLayout mFeed;
    private SwipeRefreshLayout mResturant;


    private LinearLayout mClosed;
    private LinearLayout mBusy;

    private FirebaseRecyclerAdapter<Content, ContentViewHolder> firebaseRecyclerAdapter;

    private static RestaurantModel user;

    public static String rest_id;
    public static String rest_name;
    public static String rest_wait_time;

    private Query categoryRestaurants;
    ChildEventListener childEventListener2;

    public static String time;
    public static String toDaysDate;
    public static Date day;
    public static Date timeRightNow;
    public static Activity activity;
    public static String dayNum;


    private ArrayList<Content> content;
    private FeedAdapter feedAdapter;

    private TabLayout tabHost;

    String provider;
    LocationManager locationManager;
    LocationListener locationListener;
    Location location;

    private RestaurantAdapter restaurantAdapter;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public MainUActivity() {
    }

    public static RestaurantModel getUser() {
        return user;
    }

    public static void setUser(RestaurantModel user) {
        MainUActivity.user = user;
    }

    public static String getRest_name() {
        return rest_name;
    }

    public static void setRest_name(String rest_name) {
        MainUActivity.rest_name = rest_name;
    }

    public static class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            // get the string from params, which is an array
            System.out.println("Startes den?");
            String myString = params[0];
            String TIME_SERVER = myString;
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(TIME_SERVER);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            System.out.println("Startes den?");
            TimeInfo timeInfo = null;
            try {
                timeInfo = timeClient.getTime(inetAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Startes den?");
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date date = new Date(returnTime);
            Calendar c = Calendar.getInstance();
            System.out.println("real!!" +c.getTime());
            DateFormat formatTime = new SimpleDateFormat("HHmm");
            DateFormat formatDay = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatDayNTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            System.out.println("Startes den?");
            long timeZoneChange = TimeZone.getTimeZone("Europe/Oslo").getRawOffset();
            long extraHour = 0;

            c.setTimeInMillis(returnTime + extraHour);
            timeRightNow = c.getTime();
            formatDayNTime.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            formatTime.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formattedTime;
            String formattedDay;
            day = c.getTime();
            formatTime.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            formatDay.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            formatDayNTime.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            formattedTime = formatTime.format(date);
            formattedDay = formatDay.format(date);
            time = formattedTime;
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(formattedTime.substring(0,2)));
            c.set(Calendar.DAY_OF_YEAR, Integer.parseInt(formattedDay.substring(0,2)));
            toDaysDate = formattedDay;
            timeRightNow = c.getTime();
            System.out.println("timeRightNow formattedTime: " + formattedTime.substring(0,2) + " " + formattedTime.substring(2,4));
            System.out.println("timeRightNow 1: " + timeRightNow);
            System.out.println("timeRightNow 2: " + time);
            System.out.println("timeRightNow 3: " + toDaysDate);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            try {
                cal.setTime(sdf.parse(toDaysDate));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dayNum = Integer.toString(cal.get(Calendar.DAY_OF_WEEK));
            return date.toString();
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Do things like hide the progress bar or change a TextView
        }
    }



    public static String getRest_wait_time() {
        return rest_wait_time;
    }

    public static void setRest_wait_time(String rest_wait_time) {
        rest_wait_time = rest_wait_time;
    }

    public static String getRest_id() {
        return rest_id;
    }

    public static void setRest_id(String rest_id) {
        MainUActivity.rest_id = rest_id;
    }

    public static ArrayList<Content> getBasketList() {
        return basketList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        checkLocationPermission();

        locationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                location = loc;

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar);
        // Floating shopping cart
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                Intent intent = new Intent(MainUActivity.this, YourOrders.class);
                startActivity(intent);
            }
        }); //closing the setOnClickListener method

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainUActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        mResturant = findViewById(R.id.restaurant_refresh);
        mFeed = findViewById(R.id.swipeRefreshLayout);
        tabHost = findViewById(R.id.tab_host);
        tabHost.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mFeed.setVisibility(View.VISIBLE);
                        mResturant.setVisibility(View.GONE);
                        break;
                    case 1:
                        mFeed.setVisibility(View.GONE);
                        mResturant.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mContentList = findViewById(R.id.content_list);
        mRestaurantList = findViewById(R.id.restaurant_list);
        mRestaurantList.setHasFixedSize(true);
        mRestaurantList.setLayoutManager(new GridLayoutManager(this, 1));
        mContentList.setHasFixedSize(false);
        mContentList.setLayoutManager(new LinearLayoutManager(this));
        mFeed.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mFeed.setRefreshing(true);
                        startActivity(new Intent(MainUActivity.this, MainUActivity.class).putExtra("tab", "0"));
                        mFeed.setRefreshing(false);
                    }
                }
        );
        mResturant.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mResturant.setRefreshing(true);
                        startActivity(new Intent(MainUActivity.this, MainUActivity.class).putExtra("tab", "1"));
                        mResturant.setRefreshing(false);
                    }
                }
        );

    }

    protected void onStart() {
        super.onStart();
        if(new Connection(this).getConnected()) {
            MyTask task = new MyTask();
            task.execute("pool.ntp.org");
        }

        Intent intent = getIntent();
        String value = intent.getStringExtra("tab");
        if(value != null) {
            System.out.println("ValueTab: " + value);
            if(value.equals("1")) {
                tabHost.getTabAt(1).select();
            }
        }
        mAuth.addAuthStateListener(mAuthListener);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("offers");

        FirebaseDatabase.getInstance().getReference().child("Users/").child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());

        DatabaseReference feedDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        feedDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(RestaurantModel.class);

                if (ContextCompat.checkSelfPermission(MainUActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    System.out.println("Location onCreate: " + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                }

                content = new ArrayList<Content>();
                feedAdapter = new FeedAdapter(MainUActivity.this, location, user);
                mContentList.setAdapter(feedAdapter);
                restaurantAdapter = new RestaurantAdapter(MainUActivity.this, location);
                mRestaurantList.setAdapter(restaurantAdapter);

                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override

                    public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
                        Content contentItem = dataSnapshot.getValue(Content.class);
                        feedAdapter.addItem(contentItem);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                        Content content = dataSnapshot.getValue(Content.class);
                        String commentKey = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        // A comment has changed, use the key to determine if we are displaying this
                        // comment and if so remove it.
                        String content = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                        // A comment has changed position, use the key to determine if we are
                        // displaying this comment and if so move it.
                        Content movedComment = dataSnapshot.getValue(Content.class);
                        String content = dataSnapshot.getKey();

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Toast.makeText(MainUActivity.this, "Error loading data. Refresh!", Toast.LENGTH_SHORT).show();
                    }
                };

                mDatabase.addChildEventListener(childEventListener);

                categoryRestaurants = mDatabase.getParent().child("Users").orderByChild("type").equalTo("r");
                childEventListener2 = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if(restaurantAdapter != null) {
                            restaurantAdapter.addItem(dataSnapshot.getValue(RestaurantModel.class));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                categoryRestaurants.addChildEventListener(childEventListener2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(MainUActivity.this, MainUActivity.class));
        } else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_category) {
            startActivity(new Intent(MainUActivity.this, Categories.class));
        } else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_orders) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(1);
            startActivity(new Intent(MainUActivity.this, Eatit.class));
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainUActivity.this, SettingsUser.class));
        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut(); //End user session
            startActivity(new Intent(MainUActivity.this, StartActivity.class)); //Go back to home page
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setEdit(String id) {
            final String key = id;
            LinearLayout editContent = mView.findViewById(R.id.content_row);
            editContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(key);
                }
            });
        }

        public void setPeriod(String pq) {
            TextView pqField = mView.findViewById(R.id.pq_field);
            pqField.setText(pq);
        }

        public void setQuantity(String quantity) {
            TextView pqField = mView.findViewById(R.id.pq_field);
            pqField.setText(quantity);
        }

        public void setEvery(String every) {
            TextView pqField = mView.findViewById(R.id.pq_field);
            pqField.setText(every);
        }

        public void showRow() {
            LinearLayout contentRow = mView.findViewById(R.id.content_row);
            contentRow.setVisibility(View.VISIBLE);
        }


        public void hideRow() {
            LinearLayout contentRow = mView.findViewById(R.id.content_row);
            contentRow.setVisibility(View.GONE);
        }

        public void setRestName(String restName) {
            TextView content_restname = mView.findViewById(R.id.rest_name);
            content_restname.setVisibility(View.VISIBLE);
            content_restname.setText(restName);
        }

        public void setImage(Context ctx, String image){
            ImageView content_image = mView.findViewById(R.id.content_image);
            Picasso.with(ctx).load(image).into(content_image);
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


    public static int getTotalPrice() {
        return totalPrice;
    }

    public static void setTotalPrice(int totalPrice) {
        MainUActivity.totalPrice = totalPrice;
    }


    public static int convertDaytoNumber(String day) {
        if(day.equals("Monday")) {
            return 2;
        }
        else if(day.equals("Tuesday")) {
            return 3;
        }
        else if(day.equals("Wednesday")) {
            return 4;
        }
        else if(day.equals("Thursday")) {
            return 5;
        }
        else if(day.equals("Friday")) {
            return 6;
        }
        else if(day.equals("Saturday")) {
            return 7;
        }
        else if(day.equals("Sunday")) {
            return 1;
        }
        return 0;
    }



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission location")
                        .setMessage("Allow plateup to use location")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("Clicked cancel!");
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("Clicked ok!");
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainUActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        if(provider != null) {
                            locationManager.requestLocationUpdates(provider, 400, 1, locationListener);


                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
