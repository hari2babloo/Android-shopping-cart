package com.example.jasim.plateup;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.adapters.OrderAdapterR;
import com.example.jasim.plateup.bookings.BookingActivity;
import com.example.jasim.plateup.bookings.MyRestaurantBookingsActivity;
import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.settings.SettingsAuthActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class MainRActivity extends AppCompatActivity
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
    private static ArrayList<Order> completeOrders;
    private RecyclerView mOrdersList;

    // Alarm notification
    private Uri notification;
    private Vibrator vibrator;
    private NotificationChannel mChannel;

    private Button btnPrint;
    private Button mMinus;
    private Button mPlus;
    private TextView mAmount;

    public static MediaPlayer mediaPlayer;


    //
    public static String time;
    public static String toDaysDate;
    public static Date day;
    public static Date timeRightNow;

    private ChildEventListener childEventListener;

    private TextView noNewOrder;

    private int notificationCounter;


    public static ArrayList<Order> getCompleteOrders() {
        return completeOrders;
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
            String myString = params[0];
            String TIME_SERVER = myString;
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(TIME_SERVER);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            TimeInfo timeInfo = null;
            try {
                timeInfo = timeClient.getTime(inetAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date date = new Date(returnTime);
            Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Oslo"));
            DateFormat formatTime = new SimpleDateFormat("HHmm");
            DateFormat formatDay = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatDayNTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            long timeZoneChange = TimeZone.getTimeZone("Europe/Oslo").getRawOffset();
            long extraHour = 0;
            if(c.getTimeZone().useDaylightTime()) {
                extraHour = 3600000;
            }
            c.setTimeInMillis(returnTime + timeZoneChange + extraHour);
            timeRightNow = c.getTime();
            formatDayNTime.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            formatTime.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formattedTime;
            String formattedDay;
            day = c.getTime();
            System.out.println("Denne tiden: " + day);
            SimpleDateFormat simpleFormat = new SimpleDateFormat("");
            formatTime.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            formatDay.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            formatDayNTime.setTimeZone(TimeZone.getTimeZone("Europe/Oslo"));
            formattedTime = formatTime.format(date);
            formattedDay = formatDay.format(date);
            time = formattedTime;
            toDaysDate = formattedDay;
            System.out.println("timeRightNow: " + timeRightNow);
            System.out.println("timeRightNow: " + time);
            System.out.println("timeRightNow: " + toDaysDate);
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

    public static void setmRestaurantModel(RestaurantModel mRestaurantModel) {
        MainRActivity.mRestaurantModel = mRestaurantModel;
    }

    public static RestaurantModel getRestaurant() {
        return mRestaurantModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        checkHuawei();


        notificationCounter = 0;
        setContentView(R.layout.activity_main_r);
        MainRActivity.MyTask task = new MainRActivity.MyTask();
        task.execute("pool.ntp.org");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("orders").child("unprinted");
        mOrderList = findViewById(R.id.order_list);
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("status");
        mWaitTime = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("waittime");
        completeOrders = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Users/").child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());

        final LayoutInflater timeInflater = LayoutInflater.from(this);
        final View timeView = timeInflater.inflate(R.layout.rest_feed_row, null);
        final AlertDialog timeDialog = new AlertDialog.Builder(this).create();
        timeDialog.setView(timeView);

        final OrderAdapterR orderAdapter = new OrderAdapterR(getApplicationContext(),completeOrders);
        mOrderList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mOrderList.setAdapter(orderAdapter);

        childEventListener = new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if(notificationCounter == 0) {
//                    long[] pattern = { 0, 200, 0 };
//                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    r.play();
                }
                notificationCounter++;
                Order singleOrder = dataSnapshot.getValue(Order.class);
                completeOrders.add(singleOrder);
                noNewOrder = findViewById(R.id.no_new_orders);
                noNewOrder.setVisibility(View.GONE);
                orderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                notificationCounter--;
                if(notificationCounter == 0) {
                    if(mediaPlayer != null) {
                        mediaPlayer.stop();
                    }
                }
                Order singleOrder = new Order();
                for (Order o: completeOrders) {
                    if(o.getId().equals(dataSnapshot.getValue(Order.class).getId())) {
                        System.out.println("TRUE");
                        singleOrder = o;
                        Toast.makeText(MainRActivity.this, "The order is being printed, make sure you are near the printer", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                mDatabase.getParent().child("printed").child(singleOrder.getId()).setValue(singleOrder);
                completeOrders.remove(singleOrder);
                if(completeOrders.size() == 0) {
                    noNewOrder.setVisibility(View.VISIBLE);
                }
                orderAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addChildEventListener(childEventListener);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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
                final NumberPicker mMinutes;
                Button mConfirm;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainRActivity.this);
                LayoutInflater inflater = MainRActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_waiting, null);
                dialogBuilder.setView(dialogView);

                mMinutes = dialogView.findViewById(R.id.minutes);
                mConfirm = dialogView.findViewById(R.id.next_button);

                final String[] minutes = new String[3];
                minutes[0] = Integer.toString(15);
                minutes[1] = Integer.toString(30);
                minutes[2] = Integer.toString(45);
                mMinutes.setMinValue(0);
                mMinutes.setDisplayedValues(minutes);
                mMinutes.setMaxValue(minutes.length-1);
                final AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                mConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("waittime");
                        mDatabase.setValue(minutes[mMinutes.getValue()]);
                        showWaitTime.setText(minutes[mMinutes.getValue()]+"min");
                        dialog.cancel();
                    }
                });
                //startActivity(new Intent(MainRActivity.this, WaitingActivity.class));
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

                    Intent loginIntent = new Intent(MainRActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
    }

    private void checkHuawei() {
        final SharedPreferences sp= getSharedPreferences("PlateUP", MODE_PRIVATE);
        if("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !sp.getBoolean("protected",false)) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(this);
            builder.setTitle("PlateUp").setMessage("Allow PlateUP to send notification?")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
                            sp.edit().putBoolean("protected",true).commit();
                        }
                    }).create().show();
        }
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    public void onBackPressed() {
        moveTaskToBack(true);
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
            startActivity(new Intent(MainRActivity.this, PostContent.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_orders) {
            startActivity(new Intent(MainRActivity.this, MainRActivity.class));
        }
        else if (id == R.id.nav_gallery) {
            startActivity(new Intent(MainRActivity.this, PastOrdersActivity.class));
        }
        else if (id == R.id.nav_earnings) {
            startActivity(new Intent(MainRActivity.this, EarningsActivity.class));
        }
        else if (id == R.id.nav_profile) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mRestaurantModel = dataSnapshot.getValue(RestaurantModel.class);
                    System.out.println("RestaurantID: " + mRestaurantModel.getId());
                    startActivity(new Intent(MainRActivity.this, ProfileRestaurant.class).putExtra("restaurant", mRestaurantModel.getId()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    // Logic for failure is here...
                }
            });

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(MainRActivity.this, SettingsAuthActivity.class));

        } else if (id == R.id.nav_send) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("device_token").removeValue();
            mDatabase.removeEventListener(childEventListener);
            mAuth.signOut(); //End user session
            startActivity(new Intent(MainRActivity.this, MainActivity.class)); //Go back to home page
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
