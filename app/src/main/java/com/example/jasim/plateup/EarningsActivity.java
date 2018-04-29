package com.example.jasim.plateup;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class EarningsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout earningTabs;
    private LinearLayout mDay;
    private LinearLayout mMonth;

    // Firebasereference and auth
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    // Lists for view

    private RecyclerView mDayEarnigsList;
    private RecyclerView mMonthEarningsList;
    private ArrayList<Order> earnings;
    private ArrayList<Order> january_earnings = new ArrayList<>();
    private ArrayList<Order> february_earnings = new ArrayList<>();
    private ArrayList<Order> march_earnings = new ArrayList<>();
    private ArrayList<Order> april_earnings = new ArrayList<>();
    private ArrayList<Order> may_earnings = new ArrayList<>();
    private ArrayList<Order> june_earnings = new ArrayList<>();
    private ArrayList<Order> july_earnings = new ArrayList<>();
    private ArrayList<Order> august_earnings = new ArrayList<>();
    private ArrayList<Order> september_earnings = new ArrayList<>();
    private ArrayList<Order> october_earnings = new ArrayList<>();
    private ArrayList<Order> november_earnings = new ArrayList<>();
    private ArrayList<Order> desember_earnings = new ArrayList<>();

    // Daily TextViews

    private TextView mTotalEarningsDay;
    private int totalEarningsDay;

    // Monthly TextViews

    private TextView mJanuaryEarnings;
    private TextView mFebruaryEarnings;
    private TextView mMarchEarnings;
    private TextView mAprilEarnings;
    private TextView mMayEarnings;
    private TextView mJuneEarnings;
    private TextView mJulyEarnings;
    private TextView mAugustEarnings;
    private TextView mSeptemberEarnings;
    private TextView mOctoberEarnings;
    private TextView mNovemberEarnings;
    private TextView mDesemberEarnings;
    private TextView mTotalEarnings;


    TextView mJanuaryLabel;
    TextView mFebruaryLabel;
    TextView mMarchLabel;
    TextView mAprilLabel;
    TextView mMayLabel;
    TextView mJuneLabel;
    TextView mJulyLabel;
    TextView mAugustLabel;
    TextView mSeptemberLabel;
    TextView mOctoberLabel;
    TextView mNovemberLabel;
    TextView mDesemberLabel;


    // Integer

    private int januaryTotal = 0;
    private int februaryTotal = 0;
    private int marchTotal = 0;
    private int aprilTotal = 0;
    private int mayTotal = 0;
    private int juneTotal = 0;
    private int julyTotal = 0;
    private int augustTotal = 0;
    private int septemberTotal = 0;
    private int octoberTotal = 0;
    private int novemberTotal = 0;
    private int desemberTotal = 0;
    private int totalTotal = 0;

    //
    String day;
    String month;
    String year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("orders").child("printed");
        earningTabs = (TabLayout)findViewById(R.id.earnings_tab);
        mDay = (LinearLayout)findViewById(R.id.day_earnings);
        mMonth = (LinearLayout)findViewById(R.id.month_earnings);
        februaryTotal = 0;

        //
        mTotalEarningsDay = (TextView)findViewById(R.id.total_earnings_day);

        //

        mJanuaryLabel = (TextView)findViewById(R.id.january_label);
        mFebruaryLabel = (TextView)findViewById(R.id.february_label);
        mMarchLabel = (TextView)findViewById(R.id.march_label);
        mAprilLabel = (TextView)findViewById(R.id.april_label);
        mMayLabel = (TextView)findViewById(R.id.may_label);
        mJuneLabel = (TextView)findViewById(R.id.june_label);
        mJulyLabel = (TextView)findViewById(R.id.july_label);
        mAugustLabel = (TextView)findViewById(R.id.august_label);
        mSeptemberLabel = (TextView)findViewById(R.id.september_label);
        mOctoberLabel = (TextView)findViewById(R.id.october_label);
        mNovemberLabel = (TextView)findViewById(R.id.november_label);
        mDesemberLabel = (TextView)findViewById(R.id.desember_label);


        mJanuaryEarnings = (TextView)findViewById(R.id.january_earnings);
        mFebruaryEarnings = (TextView)findViewById(R.id.february_earnings);
        mMarchEarnings = (TextView)findViewById(R.id.march_earnings);
        mAprilEarnings = (TextView)findViewById(R.id.april_earnings);
        mMayEarnings = (TextView)findViewById(R.id.may_earnings);
        mJuneEarnings = (TextView)findViewById(R.id.june_earnings);
        mJulyEarnings = (TextView)findViewById(R.id.july_earnings);
        mAugustEarnings = (TextView)findViewById(R.id.august_earnings);
        mSeptemberEarnings = (TextView)findViewById(R.id.september_earnings);
        mOctoberEarnings = (TextView)findViewById(R.id.october_earnings);
        mNovemberEarnings = (TextView)findViewById(R.id.november_earnings);
        mDesemberEarnings = (TextView)findViewById(R.id.desember_earnings);
        mTotalEarnings = (TextView)findViewById(R.id.total_earnings_year);


        //
        mDayEarnigsList = (RecyclerView)findViewById(R.id.day_earnings_list);
        //mMonthEarningsList = (RecyclerView)findViewById(R.id.month_earnings_list);
        earnings = new ArrayList<>();
        final EarningsAdapter earningsAdapter = new EarningsAdapter(earnings);
        mDayEarnigsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDayEarnigsList.setAdapter(earningsAdapter);
        totalEarningsDay = 0;


        //


        Calendar cal = Calendar.getInstance();
        cal.setTime(MainRActivity.timeRightNow);

        day = "" + cal.get(Calendar.DAY_OF_MONTH);
        month = "" + (cal.get(Calendar.MONTH) + 1);
        year = "" + cal.get(Calendar.YEAR);


        System.out.println("Month: " + month);
        if(Integer.parseInt(month) > 0) {
            mJanuaryEarnings.setText("0,-");
            if(Integer.parseInt(month) == 1) {
                mJanuaryLabel.setTypeface(null, Typeface.BOLD);
                mJanuaryEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 1) {
            mFebruaryEarnings.setText("0,-");
            if(Integer.parseInt(month) == 2) {
                mFebruaryLabel.setTypeface(null, Typeface.BOLD);
                mFebruaryEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 2) {
            mMarchEarnings.setText("0,-");
            if(Integer.parseInt(month) == 3) {
                mMarchLabel.setTypeface(null, Typeface.BOLD);
                mMarchEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 3){
            mAprilEarnings.setText("0,-");
            if(Integer.parseInt(month) == 4) {
                mAprilLabel.setTypeface(null, Typeface.BOLD);
                mAprilEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 4) {
            mMayEarnings.setText("0,-");
            if(Integer.parseInt(month) == 5) {
                mMayLabel.setTypeface(null, Typeface.BOLD);
                mMayEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 5) {
            mJuneEarnings.setText("0,-");
            if(Integer.parseInt(month) == 6) {
                mJuneLabel.setTypeface(null, Typeface.BOLD);
                mJuneEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 6) {
            mJulyEarnings.setText("0,-");
            if(Integer.parseInt(month) == 7) {
                mJulyLabel.setTypeface(null, Typeface.BOLD);
                mJulyEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 7) {
            mAugustEarnings.setText("0,-");
            if(Integer.parseInt(month) == 8) {
                mAugustLabel.setTypeface(null, Typeface.BOLD);
                mAugustEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 8) {
            mSeptemberEarnings.setText("0,-");
            if(Integer.parseInt(month) == 9) {
                mSeptemberLabel.setTypeface(null, Typeface.BOLD);
                mSeptemberEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 9) {
            mOctoberEarnings.setText("0,-");
            if(Integer.parseInt(month) == 10) {
                mOctoberLabel.setTypeface(null, Typeface.BOLD);
                mOctoberEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 10){
            mNovemberEarnings.setText("0,-");
            if(Integer.parseInt(month) == 11) {
                mNovemberLabel.setTypeface(null, Typeface.BOLD);
                mNovemberEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) > 11) {
            mDesemberEarnings.setText("0,-");
            if(Integer.parseInt(month) == 12) {
                mDesemberLabel.setTypeface(null, Typeface.BOLD);
                mDesemberEarnings.setTypeface(null, Typeface.BOLD);
            }
        }
        if(Integer.parseInt(month) < 10) {
            month = "0" + month;
        }
        if(Integer.parseInt(day) < 10) {
            day = "0" + day;
        }
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Order singleEarnings = dataSnapshot.getValue(Order.class);
                if(singleEarnings.getDateOfOrder().startsWith(day) && singleEarnings.getDateOfOrder().endsWith(year)) {
                    System.out.println("Date of order: " + singleEarnings.getDateOfOrder() + " day: " + day);
                    System.out.println("dateOfOrder: " + singleEarnings.getDateOfOrder() + " day: " + day);
                    earnings.add(singleEarnings);
                    totalEarningsDay += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    System.out.println("totalEarningsDay: " + totalEarningsDay);
                    mTotalEarningsDay.setText(totalEarningsDay + " kr");
                    earningsAdapter.notifyDataSetChanged();
                }
                if(singleEarnings.getDateOfOrder().endsWith("01/" + year)) {
                    january_earnings.add(singleEarnings);

                    //
                    januaryTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mJanuaryEarnings.setText("" + januaryTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("02/" + year)) {
                    february_earnings.add(singleEarnings);

                    februaryTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mFebruaryEarnings.setText("" + februaryTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("03/" + year)) {
                    march_earnings.add(singleEarnings);
                    marchTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mMarchEarnings.setText("" + marchTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("04/" + year)) {
                    april_earnings.add(singleEarnings);

                    aprilTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mAprilEarnings.setText("" + aprilTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("05/" + year)) {
                    may_earnings.add(singleEarnings);

                    mayTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mMayEarnings.setText("" + mayTotal + ",-");

                }
                else if(singleEarnings.getDateOfOrder().endsWith("06/" + year)) {
                    june_earnings.add(singleEarnings);

                    juneTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mJuneEarnings.setText("" + juneTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("07/" + year)) {
                    july_earnings.add(singleEarnings);

                    julyTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mJulyEarnings.setText("" + julyTotal + ",-");
                    if(julyTotal == 0) {
                        mJulyEarnings.setText("0,-");
                    }
                }
                else if(singleEarnings.getDateOfOrder().endsWith("08/" + year)) {
                    august_earnings.add(singleEarnings);

                    augustTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mAugustEarnings.setText("" + augustTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("09/" + year)) {
                    september_earnings.add(singleEarnings);

                    septemberTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mSeptemberEarnings.setText("" + septemberTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("10/" + year)) {
                    october_earnings.add(singleEarnings);

                    octoberTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mOctoberEarnings.setText("" + octoberTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("11/" + year)) {
                    november_earnings.add(singleEarnings);

                    novemberTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mNovemberEarnings.setText("" + novemberTotal + ",-");
                }
                else if(singleEarnings.getDateOfOrder().endsWith("12/" + year)) {
                    desember_earnings.add(singleEarnings);

                    desemberTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                    mDesemberEarnings.setText("" + desemberTotal + ",-");
                }
                totalTotal += Integer.parseInt(singleEarnings.getPrice_w_vat());
                mTotalEarnings.setText(totalTotal + ",-");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };mDatabase.addChildEventListener(childEventListener);

        earningTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    mDay.setVisibility(View.VISIBLE);
                    mMonth.setVisibility(View.GONE);
                }
                else if(tab.getPosition() == 1) {
                    mDay.setVisibility(View.GONE);
                    mMonth.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        getMenuInflater().inflate(R.menu.earnings, menu);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
