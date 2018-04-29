package com.example.jasim.plateup;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.Check.Connection;
import com.example.jasim.plateup.notification.NotificationPublisher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CompleteOrder extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private TextView amount;
    private TextView mDeliveryField;
    private TextView mDeliveryPrice;
    private TextView mDeliveryType;
    private TextView mWaitingTime;
    private Button mConfirm;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView mRestName;
    private RadioButton mPickUp;
    private RadioButton mDelivery;
    private Button mOk;
    private Button mCancel;
    private Button mTimeOptions;
    private LinearLayout mOrdersForConfirmation;
    private String deliveryType;
    private int deliveryPrice = 0;
    private RestaurantModel rModel;
    private Spinner mPickDate;
    private Spinner mPickTime;
    private int totalWaitTime = 30;
    private boolean showNextHour;
    int currentTimeHour = Integer.parseInt(MainUActivity.time.substring(0, 2));
    private Button timeOK;
    private Button timeCancel;
    private Button mBackButton;
    private String whichOpening;
    private int sum;
    private String decidedOpening = "first";
    private ArrayList<String> dates;
    private boolean datesAdded = false;
    private boolean havePickedDate = false;
    private int saveDayPosition = 0;
    private int asapIsCheckedHour = 0;
    private int asapIsCheckedMinutes = 0;
    private boolean hasHours;
    private boolean hasMinutes;
    private int statusC;
    private long delay;
    private String hourSpinner;
    private int nextDayPosition;


    // ASAP
    private LinearLayout mAsapEstimateLayout;
    private TextView mAsapEstimate;

    private ArrayList<SmallContent> orders;

    private boolean hasRemoved = false;

    private int hasChanged = 0;


    private RadioGroup rG;
    private boolean running = true;

    private RadioButton mAsap;
    private RadioButton mLater;


    private LinearLayout dateField;

    private ArrayAdapter<String> timeAdapter;

    private LinearLayout mShowWaitingTime;

    private DatabaseReference closeTime;

    private boolean firstTime = true;


    // Dates for spinner items
    //
    private Date open = null;
    private Date close = null;

    //
    private Date openCopy = null;
    private Date closeCopy = null;

    //
    private Date currentTime = null;

    //
    private Openings opening;

    private Calendar timeRightNow;

    // Restaurant waitTime
    private int waitTime;
    private int deliveryTime = 0;

    private String todayTimeOfArrival;
    private ArrayList timeItems;
    // Calendar currentTime
    private Calendar c;

    // Multithread refreshing
    private final static int INTERVAL = 1000;
    private Handler mHandler = new Handler();
    private Runnable mHandlerTask;

    // Checks before confirm

    private boolean pickDeliveryIsChecked = false;
    private boolean asapLaterIsChecked = false;
    private boolean hasClickedOk = false;

    private String deliveryTypeString;


    private LinearLayout showClosedLater;

    // Delivery

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("orders");
        closeTime = mDatabase.getParent().child("Users").child(MainUActivity.getRest_id());
        // Delivery time dialog
        final LayoutInflater timeInflater = LayoutInflater.from(this);
        final View timeView = timeInflater.inflate(R.layout.delivery_time, null);
        final AlertDialog timeDialog = new AlertDialog.Builder(this).create();
        timeDialog.setView(timeView);
        timeOK = timeView.findViewById(R.id.ok_btn);
        timeCancel = timeView.findViewById(R.id.cancel_btn);

        // Delivery type dialog
        mPickDate = timeView.findViewById(R.id.date_spinner);
        mPickTime = timeView.findViewById(R.id.time_spinner);
        mPickUp = timeView.findViewById(R.id.btnPickUp);
        //mDelivery = timeView.findViewById(R.id.btnDelivery);
        mOk = timeView.findViewById(R.id.ok);
        mCancel = timeView.findViewById(R.id.cancel);
        mDeliveryType = findViewById(R.id.deliveryType);
        mWaitingTime = findViewById(R.id.waiting_time);
        mShowWaitingTime = findViewById(R.id.show_waiting_time);
        rG = timeView.findViewById(R.id.time_group);
        mAsapEstimate = timeView.findViewById(R.id.asap_time);
        mAsapEstimateLayout = timeView.findViewById(R.id.asap_time_layout);
        showClosedLater = timeView.findViewById(R.id.closed_message);

        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        dateField = timeView.findViewById(R.id.datefield);
        mAsap = timeView.findViewById(R.id.asapbtn);
        mLater = timeView.findViewById(R.id.laterbtn);

        mDeliveryField = findViewById(R.id.delivery_type_field);
        mDeliveryPrice = findViewById(R.id.delivery_price);
        mTimeOptions = findViewById(R.id.time_options);
        amount = findViewById(R.id.total_field);
        amount.setText("" + (MainUActivity.getTotalPrice()) + ",-");
        mOrdersForConfirmation = findViewById(R.id.orders_for_confirmation);
        mTimeOptions.setSoundEffectsEnabled(false);

        mHandlerTask = new Runnable()
        {
            @Override
            public void run() {
                resetTime();
                if(dates != null) {
                    if(currentTime.after(close)) {
                        running = false;
                        returnToFeed(1);
                    }
                }
                if(!running) return;
                mHandler.postDelayed(mHandlerTask, INTERVAL);

            }
        };
        closeTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rModel = dataSnapshot.getValue(RestaurantModel.class);
                //legg inn getOpen for at det wskal funke
                //startRepeatingTask();
                System.out.println("rModel: " + rModel.getUsername());
                if(dates != null && rModel.getOpenings().get(whichOpening) != null) {
                    resetTime();
                }
                if(dataSnapshot.getValue(RestaurantModel.class).getStatus().equals("closed")) {
                    returnToFeed(2);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDeliveryIsChecked = true;
                deliveryTypeString = "pickup";
                rG.setVisibility(View.VISIBLE);
                deliveryTime = 0;
                deliveryPrice = 0;
                if(mAsap.isChecked()) {
                    mAsap.performClick();
                }
                else if(mLater.isChecked()) {
                    mLater.performClick();
                }
                mTimeOptions.performClick();

            }
        });
        /*mDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDeliveryIsChecked = true;
                deliveryTime = 30;
                deliveryPrice = 100;
                deliveryTypeString = "delivery";
                rG.setVisibility(View.VISIBLE);
                if(mAsap.isChecked()) {
                    mAsap.performClick();
                }
                else if(mLater.isChecked()) {
                    mLater.performClick();
                }
                mTimeOptions.performClick();

            }
        });*/
        mAsap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWhichOpening();
                showClosedLater.setVisibility(View.GONE);
                
                dateField.setVisibility(View.GONE);
                MainUActivity.MyTask task = new MainUActivity.MyTask();
                task.execute("ntp.uio.no");
                resetTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(MainUActivity.timeRightNow);

                Calendar bajs = Calendar.getInstance();
                bajs.setTime(MainUActivity.timeRightNow);
                System.out.println("Timing: " + cal.getTime());
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.add(Calendar.MINUTE, deliveryTime);
                cal.add(Calendar.MINUTE, waitTime);

                Date orderTime = cal.getTime();
                Calendar closeCopyy = Calendar.getInstance();
                closeCopyy.setTime(close);

                System.out.println("Timing: close: " + closeCopyy.getTime());

                Calendar order = Calendar.getInstance();
                order.setTime(orderTime);
                Calendar openOrder = Calendar.getInstance();
                openOrder.setTime(open);
                System.out.println("openHour: " + openOrder.get(Calendar.HOUR_OF_DAY) + " CurrentHour:" );
                if(open.after(close) && !currentTime.before(close)) {
                    closeCopyy.add(Calendar.DAY_OF_MONTH, 1);

                }
                System.out.println("Timing: after: " + closeCopyy.getTime());

                Date closeCopyyy = closeCopyy.getTime();

                System.out.println("orderTime: " + orderTime + " close: " + close + "closeCopy: " + closeCopyyy + " open " + openCopy + " " + open);
                System.out.println("orderTime: " + orderTime.before(closeCopyyy) + " " + orderTime.after(openCopy));
                if(closeCopyy.before(openOrder)) {
                    openOrder.set(Calendar.DAY_OF_MONTH, openOrder.get(Calendar.DAY_OF_MONTH)-1);
                    openCopy.setTime(openOrder.getTimeInMillis());
                }
                if(orderTime.before(closeCopyyy) && orderTime.after(openCopy)) {
                    mAsapEstimateLayout.setVisibility(View.VISIBLE);
                    todayTimeOfArrival = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                    mAsapEstimate.setText("Estimated " + deliveryTypeString + " " + todayTimeOfArrival);
                }
                else {
                    Toast.makeText(CompleteOrder.this, "The restaurant is closed", Toast.LENGTH_SHORT).show();
                    returnToFeed(1);
                    return;
                }

                closeCopy = close;
            }
        });
        mLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAsapEstimateLayout.setVisibility(View.GONE);
                MainUActivity.MyTask task = new MainUActivity.MyTask();
                task.execute("ntp.uio.no");
                dateField.setVisibility(View.VISIBLE);
                mTimeOptions.performClick();
            }
        });
        mPickTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hourSpinner = mPickTime.getSelectedItem().toString();
                System.out.println("HourSpinner: " + hourSpinner);
                System.out.println("NextDayPosition Current Position: " + i);
                if(i >= nextDayPosition && nextDayPosition != 0) {
                    System.out.println("NextDayPosition: VI ER PAA NESTE DAG!!!!!");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mTimeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new Connection(CompleteOrder.this).getConnected()) {
                    timeDialog.show();
                    datesAdded = false;
                    System.out.println("time: " + MainUActivity.timeRightNow);
                    MainUActivity.MyTask task = new MainUActivity.MyTask();
                    task.execute("ntp.uio.no");
                    Calendar c = Calendar.getInstance();
                    c.setTime(MainUActivity.timeRightNow);
                    waitTime = Integer.parseInt(rModel.getWaittime());
                    dates = new ArrayList<String>();
                    dates.add(MainUActivity.toDaysDate + " (Today)");
                    getOpening(MainUActivity.toDaysDate);
                    System.out.println("WhichOpening: " + whichOpening);
                    if (whichOpening.equals("closed")) {
                        System.out.println("Er resten stengt?");
                        returnToFeed(1);
                        return;
                    }
                    c = Calendar.getInstance();                    c.setTime(MainUActivity.timeRightNow);
                    System.out.println("toDays date" + MainUActivity.toDaysDate);
                    // Filling datespinner
                    setWhichOpening();
                    ArrayAdapter dateAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.time_spinner, dates);
                    dateAdapter.notifyDataSetChanged();
                    mPickDate.setAdapter(dateAdapter);

                    // Fill timeAdapter

                    timeItems = new ArrayList<String>();
                    timeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.time_spinner, timeItems);
                    mPickTime.setAdapter(timeAdapter);
                    mPickDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            timeItems.clear();
                            nextDayPosition = 0;
                            getOpening(mPickDate.getSelectedItem().toString());

                            // Run method that fills open and close date variables
                            if (rModel.getOpenings().get(whichOpening) != null) {
                                resetTime();

                            } else {
                                System.out.println("Gladiator 1");
                                returnToFeed(1);                            }

                           System.out.println("Close: " + close + " open: " + open + " currentTime: " + currentTime);

                            Calendar addTime = Calendar.getInstance();
                            addTime.setTime(openCopy);
                            mPickTime.setSelection(0);
                            if (mPickDate.getSelectedItemPosition() > 0) {
                                openCopy = open;
                                if (close.before(open)) {
                                    System.out.println("Later");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(close);
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    close = calendar.getTime();
                                    closeCopy = close;
                                }
                                /*if (mDelivery.isChecked()) {
                                    Calendar d = Calendar.getInstance();
                                    d.setTime(openCopy);
                                    System.out.println("waitTime: " + waitTime);
                                    System.out.println("dTime: " + d.getTime());
                                    d.add(Calendar.MINUTE, waitTime);
                                    System.out.println("dTime: " + d.getTime());
                                    open = d.getTime();
                                    openCopy = open;
                                    addTime.setTime(openCopy);
                                    System.out.println("OPen: " + open + " close: " + close);
                                }*/
                            } else {
                                if (close.before(open) && !currentTime.before(close)) {
                                    System.out.println("Later");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(close);
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    close = calendar.getTime();
                                    closeCopy = close;
                                }
                                System.out.println("Kommer vi inn hit med en gang?");
                                Calendar c = Calendar.getInstance();
                                c.setTime(MainUActivity.timeRightNow);
                                c.set(Calendar.MILLISECOND, 0);
                                c.set(Calendar.SECOND, 0);
                                System.out.println(c.getTime() + "  " + openCopy);
                                System.out.println("CurrentTime: " + currentTime);
                                if (open.after(close) && currentTime.before(close)) {
                                    System.out.println("Vi kommer inn hit!!?");
                                    Calendar clendar = Calendar.getInstance();
                                    clendar.setTime(openCopy);
                                    clendar.add(Calendar.DAY_OF_MONTH, -1);
                                    openCopy = clendar.getTime();
                                }
                                if (c.getTime().before(openCopy)) {
                                    System.out.println("Gladiator 2");
                                    returnToFeed(1);
                                }
                                System.out.println("Calendar: " + c.getTime());
                                c.add(Calendar.MINUTE, waitTime + deliveryTime);
                                if (c.get(Calendar.MINUTE) < 15) {
                                    c.set(Calendar.MINUTE, 15);
                                } else if (c.get(Calendar.MINUTE) < 30) {
                                    c.set(Calendar.MINUTE, 30);
                                } else if (c.get(Calendar.MINUTE) < 45) {
                                    c.set(Calendar.MINUTE, 45);
                                } else {
                                    c.add(Calendar.HOUR_OF_DAY, 1);
                                    c.set(Calendar.MINUTE, 0);
                                }
                                openCopy = c.getTime();


                                addTime.setTime(openCopy);
                            }
                            Calendar closeCalendar = Calendar.getInstance();
                            closeCalendar.setTime(closeCopy);
                            System.out.println(closeCalendar.get(Calendar.HOUR_OF_DAY) + " " + addTime.get(Calendar.HOUR_OF_DAY));
                            int currentTimeHour = addTime.get(Calendar.HOUR_OF_DAY);
                            int closeTimeHour = closeCalendar.get(Calendar.HOUR_OF_DAY);
                            System.out.println("openCopy foer vi gaar inn i lokken: " + openCopy + " open: " + open);
                            int lastTime = 0;
                            int i = 0;
                            while ((openCopy.before(closeCopy) || openCopy.equals(closeCopy))) {
                                String curTime = "";
                                System.out.println("mPickTimeSelected: " + mPickTime.getSelectedItemPosition());
                                if(nextDayPosition != 0 && i >= nextDayPosition) {
                                    curTime = String.format("%02d:%02d", addTime.get(Calendar.HOUR_OF_DAY), addTime.get(Calendar.MINUTE)) + " (next day)";
                                }
                                else {
                                    curTime = String.format("%02d:%02d", addTime.get(Calendar.HOUR_OF_DAY), addTime.get(Calendar.MINUTE));
                                }
                                timeItems.add(curTime);
                                addTime.add(Calendar.MINUTE, 15);
                                if(addTime.get(Calendar.HOUR_OF_DAY) < lastTime) {
                                    nextDayPosition = i+1;
                                    System.out.println("NextDayPosition: " + nextDayPosition);
                                    }
                                System.out.println("Verdien er for neste dag: " + addTime.getTime());
                                System.out.println(closeCalendar.get(Calendar.HOUR_OF_DAY) + " " + addTime.get(Calendar.HOUR_OF_DAY));
                                openCopy = addTime.getTime();
                                currentTimeHour = addTime.get(Calendar.HOUR_OF_DAY);
                                closeTimeHour = closeCalendar.get(Calendar.HOUR_OF_DAY);
                                lastTime = addTime.get(Calendar.HOUR_OF_DAY);
                                i++;

                            }
                            System.out.println("Kommer vi saa langt?");

                            openCopy = open;
                            timeAdapter.notifyDataSetChanged();
                            if (timeItems.size() == 0) {
                                dateField.setVisibility(View.GONE);
                                showClosedLater.setVisibility(View.VISIBLE);
                            } else {
                                dateField.setVisibility(View.VISIBLE);
                                showClosedLater.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else {
                    Toast.makeText(CompleteOrder.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                }




            }
        });

        timeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAsap.isChecked() || mLater.isChecked()) {

                    mShowWaitingTime.setVisibility(View.VISIBLE);
                    if (mAsap.isChecked()) {
                        MainUActivity.MyTask task = new MainUActivity.MyTask();
                        task.execute("ntp.uio.no");
                        resetTime();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(MainUActivity.timeRightNow);
                        System.out.println("WaitTime: " + waitTime);
                        cal.add(Calendar.MINUTE, deliveryTime);
                        cal.add(Calendar.MINUTE, waitTime);

                        Date orderTime = cal.getTime();
                        orderTime.before(close);
                        System.out.println("orderTime: " + orderTime);
                        todayTimeOfArrival = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        mWaitingTime.setText(deliveryTypeString + " " + todayTimeOfArrival);
                    } else if(mLater.isChecked() && timeItems.size() != 0) {

                        if (mPickDate.getSelectedItemPosition() > 0) {
                            mWaitingTime.setText(deliveryTypeString + " " + mPickTime.getSelectedItem().toString() + " " + mPickDate.getSelectedItem().toString());
                        } else {
                            mWaitingTime.setText(deliveryTypeString + " " + mPickTime.getSelectedItem().toString());
                        }
                    }
                    if(timeItems.size() == 0 && mLater.isChecked()) {
                        mShowWaitingTime.setVisibility(View.GONE);
                        hasClickedOk = false;
                    }
                    else if (mAsap.isChecked() || mLater.isChecked()) {
                        hasClickedOk = true;
                        mDeliveryField.setText(deliveryTypeString);
                        mDeliveryPrice.setText(deliveryPrice + ",-");
                        amount.setText(MainUActivity.getTotalPrice() + deliveryPrice + ",-");
                    }
                    timeDialog.dismiss();
                }
            }

        });

        timeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog.dismiss();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mRestName = findViewById(R.id.rest_name);
        mRestName.setText(MainUActivity.getRest_name());
        mConfirm = findViewById(R.id.confirm_order);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Connection(CompleteOrder.this).getConnected()) {
                    if (hasClickedOk) {

                        // Get current users uid
                        final String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentID = mDatabase.getParent();
                        final Order order = new Order();

                        order.setRestName(MainUActivity.getRest_name());


                        // Store dishes
                        ArrayList<SmallContent> orders = new ArrayList<>();
                        int checkPrice = 0;
                        for (int i = 0; i < MainUActivity.getBasketList().size(); i++) {
                            Content content = MainUActivity.getBasketList().get(i);
                            if (content.getQuantity() == null) {
                                content.setQuantity("0");
                            }
                            SmallContent smallContent = new SmallContent(content.getId(), content.getAntall(), content.getDishname(), Integer.parseInt(content.getPrice()), content.getType(), Integer.parseInt(content.getQuantity()), content.getComment(), content.getExtraInfo());
                            if (content.getType() == null) {
                                smallContent.setType("0");
                            }

                            orders.add(smallContent);
                            checkPrice += smallContent.getPrice() * smallContent.getAntall();
                        }

                        if (checkPrice + deliveryPrice == (MainUActivity.getTotalPrice() + deliveryPrice)) {
                            order.setDishes(orders);
                            c = Calendar.getInstance();
                            // Update current time to the newest
                            MainUActivity.MyTask task = new MainUActivity.MyTask();
                            task.execute("ntp.uio.no");
                            // Setters for the order personal
                            order.setUsername(MainUActivity.getUser().getUsername());
                            order.setR_id(MainUActivity.getRest_id());
                            order.setU_id(user_id);

                            // Prices
                            order.setPrice_w_vat(MainUActivity.getTotalPrice() + "");
                            double pricewithoutVat = (100.0 / 115.0) * MainUActivity.getTotalPrice();
                            pricewithoutVat = Math.round(pricewithoutVat * 100.0) / 100.0;
                            order.setPrice_wo_vat("" + pricewithoutVat);
                            order.setVat("" + (double) Math.round((MainUActivity.getTotalPrice() - pricewithoutVat) * 100) / 100);

                            // Dates
                            String timeOfOrder = MainUActivity.time.substring(0, 2);
                            String timeOfOrder2 = MainUActivity.time.substring(2, 4);
                            order.setTimeOfOrder(timeOfOrder + ":" + timeOfOrder2);
                            order.setDateOfOrder(MainUActivity.toDaysDate);

                            if (mLater.isChecked()) {
                                timeItems.clear();
                                Calendar addTime = Calendar.getInstance();
                                addTime.setTime(openCopy);
                                //mPickTime.setSelection(0);

                                if (close.before(open) && !currentTime.before(close)) {
                                    System.out.println("Later");
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(close);
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    close = calendar.getTime();
                                    closeCopy = close;
                                }
                                Calendar c = Calendar.getInstance();
                                c.setTime(MainUActivity.timeRightNow);
                                c.set(Calendar.MILLISECOND, 0);
                                c.set(Calendar.SECOND, 0);
                                if (open.after(close) && currentTime.before(close)) {
                                    Calendar clendar = Calendar.getInstance();
                                    clendar.setTime(openCopy);
                                    clendar.add(Calendar.DAY_OF_MONTH, -1);
                                    openCopy = clendar.getTime();
                                }
                                // Sjekk før 12
                                Calendar calOpen = Calendar.getInstance();
                                calOpen.setTime(open);
                                Calendar calClose = Calendar.getInstance();
                                calClose.setTime(close);
                                if (c.getTime().before(openCopy) && calOpen.get(Calendar.HOUR_OF_DAY) < calClose.get(Calendar.HOUR_OF_DAY)) {
                                    System.out.println("Gladiator 3: " + " currentTime: " + c.getTime() + " openTime: " + openCopy);
                                    returnToFeed(1);
                                    return;
                                }

                                c.add(Calendar.MINUTE, waitTime + deliveryTime);
                                if (c.get(Calendar.MINUTE) < 15) {
                                    c.set(Calendar.MINUTE, 15);
                                } else if (c.get(Calendar.MINUTE) < 30) {
                                    c.set(Calendar.MINUTE, 30);
                                } else if (c.get(Calendar.MINUTE) < 45) {
                                    c.set(Calendar.MINUTE, 45);
                                } else {
                                    c.add(Calendar.HOUR_OF_DAY, 1);
                                    c.set(Calendar.MINUTE, 0);
                                }
                                openCopy = c.getTime();


                                addTime.setTime(openCopy);

                                Calendar closeCalendar = Calendar.getInstance();
                                closeCalendar.setTime(closeCopy);
                                System.out.println(closeCalendar.get(Calendar.HOUR_OF_DAY) + " " + addTime.get(Calendar.HOUR_OF_DAY));
                                int currentTimeHour = addTime.get(Calendar.HOUR_OF_DAY);
                                int closeTimeHour = closeCalendar.get(Calendar.HOUR_OF_DAY);
                                while ((openCopy.before(closeCopy) || openCopy.equals(closeCopy))) {
                                    String curTime = String.format("%02d:%02d", addTime.get(Calendar.HOUR_OF_DAY), addTime.get(Calendar.MINUTE));
                                    timeItems.add(curTime);
                                    addTime.add(Calendar.MINUTE, 15);
                                    System.out.println(closeCalendar.get(Calendar.HOUR_OF_DAY) + " " + addTime.get(Calendar.HOUR_OF_DAY));
                                    openCopy = addTime.getTime();
                                    currentTimeHour = addTime.get(Calendar.HOUR_OF_DAY);
                                    closeTimeHour = closeCalendar.get(Calendar.HOUR_OF_DAY);
                                }
                                openCopy = open;
                                timeAdapter.notifyDataSetChanged();
                                if (timeItems.size() == 0) {
                                    System.out.println("Gladiator 4");
                                    returnToFeed(1);
                                    return;
                                }
                                System.out.println("HourSpinner: " + hourSpinner);
                                order.setTimeOfArrival(hourSpinner.substring(0,5));
                            }
                            c.setTime(MainUActivity.timeRightNow);
                            order.setDelivery("Pick up");
                            if (mAsap.isChecked()) {
                                setWhichOpening();
                                showClosedLater.setVisibility(View.GONE);

                                dateField.setVisibility(View.GONE);
                                task = new MainUActivity.MyTask();
                                task.execute("ntp.uio.no");
                                resetTime();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(MainUActivity.timeRightNow);

                                Calendar bajs = Calendar.getInstance();
                                bajs.setTime(MainUActivity.timeRightNow);
                                System.out.println("Timing: " + cal.getTime());
                                cal.set(Calendar.MILLISECOND, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.add(Calendar.MINUTE, deliveryTime);
                                cal.add(Calendar.MINUTE, waitTime);

                                Date orderTime = cal.getTime();
                                Calendar closeCopyy = Calendar.getInstance();
                                closeCopyy.setTime(close);

                                System.out.println("Timing: close: " + closeCopyy.getTime());

                                Calendar orderAsap = Calendar.getInstance();
                                orderAsap.setTime(orderTime);
                                Calendar openOrder = Calendar.getInstance();
                                openOrder.setTime(open);
                                System.out.println("openHour: " + openOrder.get(Calendar.HOUR_OF_DAY) + " CurrentHour:");
                                if (open.after(close) && !currentTime.before(close)) {
                                    closeCopyy.add(Calendar.DAY_OF_MONTH, 1);
                                }
                                System.out.println("Timing: after: " + closeCopyy.getTime());

                                Date closeCopyyy = closeCopyy.getTime();

                                System.out.println("orderTime2: " + orderTime + " close: " + close + "closeCopy: " + closeCopyyy + " open " + openCopy + " " + open);
                                if(closeCopyy.before(openOrder)) {
                                    openOrder.set(Calendar.DAY_OF_MONTH, openOrder.get(Calendar.DAY_OF_MONTH)-1);
                                    openCopy.setTime(openOrder.getTimeInMillis());
                                }
                                if (!(orderTime.before(closeCopyyy) && orderTime.after(openCopy))) {
                                    System.out.println("Gladiator 5");
                                    returnToFeed(1);
                                    return;
                                }

                                System.out.println("Kommer vi inn hit?");
                                c.add(Calendar.MINUTE, waitTime + deliveryTime);
                                System.out.println("WaitTime: " + waitTime + " deliverTime: " + deliveryTime);
                                if (c.get(Calendar.HOUR_OF_DAY) < 10) {
                                    if (c.get(Calendar.MINUTE) < 10) {
                                        order.setTimeOfArrival("0" + c.get(Calendar.HOUR_OF_DAY) + ":0" + c.get(Calendar.MINUTE));
                                    } else {
                                        order.setTimeOfArrival("0" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
                                    }

                                } else {
                                    System.out.println("Hour: " + c.get(Calendar.HOUR_OF_DAY) + " minutes: " + c.get(Calendar.MINUTE));
                                    if (c.get(Calendar.MINUTE) < 10) {
                                        order.setTimeOfArrival(c.get(Calendar.HOUR_OF_DAY) + ":0" + c.get(Calendar.MINUTE));
                                    } else {
                                        order.setTimeOfArrival(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
                                    }
                                }
                                c.add(Calendar.MINUTE, -(waitTime + deliveryTime));
                            }
                            System.out.println("timeOfArrival: " + order.getTimeOfArrival());
                            order.setFoodReadyAt(order.getTimeOfArrival());
                            /*if (mDelivery.isChecked()) {

                                order.setDeliveryPrice("" + deliveryPrice);
                                order.setDelivery("Delivery");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                                Calendar cal = Calendar.getInstance();
                                Date date = null;

                                try {
                                    date = simpleDateFormat.parse(order.getTimeOfArrival());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                cal.setTime(date);
                                cal.add(Calendar.MINUTE, -(deliveryTime));
                                String timeForRestaurants = simpleDateFormat.format(cal.getTime());
                                order.setFoodReadyAt(timeForRestaurants);
                            }*/

                            if(nextDayPosition != 0 && nextDayPosition <= mPickTime.getSelectedItemPosition()) {
                                Calendar cal = Calendar.getInstance();
                                String date[] = mPickDate.getSelectedItem().toString().split(" ");
                                System.out.println("Date order: " + date[0]);
                                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0].split("/")[0]));
                                cal.set(Calendar.MONTH, Integer.parseInt(date[0].split("/")[1]));
                                cal.set(Calendar.YEAR, Integer.parseInt(date[0].split("/")[2]));

                                cal.add(Calendar.DAY_OF_MONTH, 1);

                                order.setDateOfArrival(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", cal.get(Calendar.MONTH)) + "/"+ cal.get(Calendar.YEAR));
                                /*if(Integer.parseInt(order.getFoodReadyAt().substring(0,2)) > cal.get(Calendar.HOUR_OF_DAY)) {
                                    Calendar temp = cal;
                                    temp.set(Calendar.DAY_OF_MONTH, temp.get(Calendar.DAY_OF_MONTH) -1);
                                    order.setFoodReadyAtDate(String.format("%02d", temp.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", temp.get(Calendar.MONTH)) + "/"+ temp.get(Calendar.YEAR));
                                }*/

                                Calendar temp = cal;
                                temp.set(Calendar.DAY_OF_MONTH, temp.get(Calendar.DAY_OF_MONTH));
                                order.setFoodReadyAtDate(String.format("%02d", temp.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d", temp.get(Calendar.MONTH)) + "/"+ temp.get(Calendar.YEAR));
                            }
                            else {
                                order.setDateOfArrival(mPickDate.getSelectedItem().toString().split(" ")[0]);
                                order.setFoodReadyAtDate(order.getDateOfArrival());
                            }
                            System.out.println("ordre: " + orders);
                            final String key = mDatabase.push().getKey();
                            if (mPickUp.isChecked()) {
                                order.setDeliveryPrice("0");
                            }
                            order.setId(key);
                            currentID.child("Users").child(MainUActivity.getRest_id()).child("orders").child("unprinted").child(key).setValue(order);
                            DatabaseReference updateQuantity = FirebaseDatabase.getInstance().getReference("offers");
                            for (int i = 0; i < orders.size(); i++) {
                                if (orders.get(i).getType().equals("1")) {
                                    if (orders.get(i).getQuantity() - 1 == 0) {
                                        updateQuantity.child(orders.get(i).getId()).removeValue();
                                    } else {
                                        updateQuantity.child(orders.get(i).getId()).child("quantity").setValue("" + (orders.get(i).getQuantity() - orders.get(i).getAntall()));
                                    }
                                }
                            }

                            final DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                            userData.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    RestaurantModel user = dataSnapshot.getValue(RestaurantModel.class);

                                    System.out.println("UserName: " + user.getUsername());
                                    HashMap<String, Integer> category;
                                    HashMap<String, Integer> fav_restaurant;
                                    HashMap<String, Integer> timeList;
                                    HashMap<String, Integer> dayList;
                                    Analytics analytics = user.getAnalytics();
                                    int pickup = 0;
                                    int delivery = 0;
                                    int time = currentTimeHour;
                                    String minutes = MainUActivity.time;
                                    if (Integer.parseInt(minutes.substring(2, 4)) >= 30) {
                                        time++;
                                    }
                                    String day = DayMonthConverter.numberToDay(MainUActivity.dayNum);
                                    if (user.getAnalytics() == null) {
                                        analytics = new Analytics();
                                        user.setAnalytics(analytics);
                                        analytics.setAverageSpending(Integer.parseInt(order.getDeliveryPrice()) + Integer.parseInt(order.getPrice_w_vat()));
                                        category = new HashMap<>();
                                        category.put(rModel.getCategory(), 1);

                                        fav_restaurant = new HashMap<>();
                                        fav_restaurant.put(rModel.getId(), 1);

                                        timeList = new HashMap<>();
                                        timeList.put("hour-" + Integer.toString(time), 1);

                                        dayList = new HashMap<>();
                                        dayList.put("" + day, 1);
                                        if (order.getDelivery().equals("Pick up")) {
                                            pickup++;
                                            user.getAnalytics().setPickup(pickup);
                                        } else {
                                            delivery++;
                                            user.getAnalytics().setDelivery(delivery);
                                        }


                                    } else {
                                        pickup = user.getAnalytics().getPickup();
                                        delivery = user.getAnalytics().getDelivery();
                                        dayList = user.getAnalytics().getDay();
                                        timeList = user.getAnalytics().getTime();

                                        analytics.setAverageSpending((analytics.getAverageSpending() + Integer.parseInt(order.getDeliveryPrice()) + Integer.parseInt(order.getPrice_w_vat())) / 2);
                                        if (user.getAnalytics().getDay().containsKey(day)) {
                                            dayList.put(day, dayList.get(day) + 1);
                                        } else {
                                            dayList.put(day, 1);
                                        }

                                        if (user.getAnalytics().getTime().containsKey("hour-" + time)) {
                                            timeList.put("hour-" + time, timeList.get("hour-" + time) + 1);
                                        } else {
                                            timeList.put("hour-"+time, 1);
                                        }

                                        if (order.getDelivery().equals("Pick up")) {
                                            pickup++;
                                            user.getAnalytics().setPickup(pickup);
                                        } else {
                                            delivery++;
                                            user.getAnalytics().setDelivery(delivery);
                                        }
                                        category = user.getAnalytics().getCategory();
                                        fav_restaurant = user.getAnalytics().getRestaurants();


                                        if (user.getAnalytics().getCategory().containsKey(rModel.getCategory())) {
                                            category.put(rModel.getCategory(), category.get(rModel.getCategory()) + 1);
                                        } else {
                                            category.put(rModel.getCategory(), 1);
                                        }
                                        if (user.getAnalytics().getRestaurants().containsKey(rModel.getId())) {
                                            fav_restaurant.put(rModel.getId(), fav_restaurant.get(rModel.getId()) + 1);
                                        } else {
                                            fav_restaurant.put(rModel.getId(), 1);
                                        }
                                        System.out.println("Kommer vi hit???");

                                    }

                                    System.out.println("UserName: " + category);


                                    user.getAnalytics().setTotalorders(user.getAnalytics().getTotalorders() + 1);
                                    user.getAnalytics().setTotalspendings(user.getAnalytics().getTotalspendings() + Integer.parseInt(order.getDeliveryPrice()) + Integer.parseInt(order.getPrice_w_vat()));
                                    user.setAnalytics(analytics);
                                    user.getAnalytics().setCategory(category);
                                    user.getAnalytics().setRestaurants(fav_restaurant);
                                    user.getAnalytics().setTime(timeList);
                                    user.getAnalytics().setDay(dayList);

                                    user.getAnalytics().setFavCat();
                                    user.getAnalytics().setFavRest();

                                    // USERS STUFF
                                    // ADD ORDER TO DATABASE
                                    order.setSeen(false);
                                    System.out.println("KeyFire: " + MainUActivity.toDaysDate.substring(0,2));
                                    userData.child("orders").child(key).setValue(order);
                                    // ADD USER ANALYTICS
                                    userData.child("analytics").setValue(analytics);

                                    // RESTAURANT STUFF
                                    // ADD RESTAURANT ANALYTICS
                                    /*HashMap<String, Integer> dish_count = new HashMap<>();
                                    for(SmallContent o : order.getDishes()) {
                                        if(dish_count.containsKey(o.getId())) {
                                            dish_count.put(o.getId(), 1+dish_count.get(o.getId()));
                                        }
                                        else {
                                            dish_count.put(o.getId(), 1);
                                        }
                                    }
                                    System.out.println("Restaurant ID: " + order.getR_id());
                                    FirebaseDatabase.getInstance().getReference("Users/").child(order.getR_id()).child("analytics").child("dish_count").setValue(dish_count);*/


                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
                                    Calendar orderTimeCal = Calendar.getInstance();
                                    orderTimeCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(order.getTimeOfOrder().substring(0,2)));
                                    orderTimeCal.set(Calendar.MINUTE, Integer.parseInt(order.getTimeOfOrder().substring(3,5)));
                                    orderTimeCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(order.getDateOfOrder().substring(0,2)));
                                    orderTimeCal.set(Calendar.MONTH, Integer.parseInt(order.getDateOfOrder().substring(3,5))-1);
                                    orderTimeCal.set(Calendar.YEAR, Integer.parseInt(order.getDateOfOrder().substring(6,10)));

                                    Calendar orderArrivalCal = Calendar.getInstance();
                                    orderArrivalCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(order.getTimeOfArrival().substring(0,2)));
                                    orderArrivalCal.set(Calendar.MINUTE, Integer.parseInt(order.getTimeOfArrival().substring(3,5)));
                                    orderArrivalCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(order.getDateOfArrival().substring(0,2)));
                                    orderArrivalCal.set(Calendar.MONTH, Integer.parseInt(order.getDateOfArrival().substring(3,5))-1);
                                    orderArrivalCal.set(Calendar.YEAR, Integer.parseInt(order.getDateOfArrival().substring(6,10)));

                                    System.out.println("OrderTime: " + orderTimeCal.getTime() + " " + orderArrivalCal.getTime());

                                    delay = orderArrivalCal.getTimeInMillis() - orderTimeCal.getTimeInMillis();


                                    HashMap<String, String> notification = new HashMap<>();

                                    notification.put("from", user_id);
                                    notification.put("type", "request");

                                    FirebaseDatabase.getInstance().getReference().child("notifications").child(rModel.getId()).push().setValue(notification);
                                    returnToFeed(0);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            returnToFeed(3);
                        }
                    } else {
                        Toast.makeText(CompleteOrder.this, "Please fill in all delivery options", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(CompleteOrder.this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.basketList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CompleteOrderAdapter adapter = new CompleteOrderAdapter(CompleteOrder.this);
        recyclerView.setAdapter(adapter);


    }
    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }



    public void setWhichOpening() {
        whichOpening = "first";
        int dayOW = 0;
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date toDays = new Date();
        try {
            toDays = format.parse(MainUActivity.toDaysDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(toDays);
        for (int i = 0; i < 7; i++) {
            whichOpening = "first";
            calendar.add(Calendar.DATE, +1);
            dayOW = calendar.get(Calendar.DAY_OF_WEEK);
            System.out.println("amountOpenings: " + rModel.getAmountOpenings());
            for (int j = 0; j < Integer.parseInt(rModel.getAmountOpenings()) + 1; j++) {
                if (j == 1) {
                    whichOpening = "second";
                } else if (j == 2) {
                    whichOpening = "third";
                }
                if(rModel.getOpenings().get(whichOpening) != null) {
                    int fromD = Integer.parseInt(rModel.getOpenings().get(whichOpening).getFromDay());
                    int toD = Integer.parseInt(rModel.getOpenings().get(whichOpening).getToDay());
                    System.out.println("fromD: " + fromD + " toD: " + toD + " whichOpening: " + whichOpening + " dayOW: " + dayOW);
                    if (fromD < toD) {
                        if (dayOW >= fromD && dayOW <= toD) {
                            break;

                        }
                    } else if (toD < fromD) {
                        if (dayOW >= fromD && dayOW >= toD || dayOW == toD) {
                            break;
                        }
                    } else if (toD == fromD) {
                        if (dayOW == fromD) {
                            break;
                        }
                    }
                }
                else {
                    whichOpening = "closed";
                }
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat simpleDayFormat = new SimpleDateFormat("EEEE");
            if(!datesAdded && !whichOpening.equals("closed")) {
                System.out.println("Vi legger til en dag, og whichOpening er: " + whichOpening + " dayOW: " + dayOW);
                dates.add(simpleDateFormat.format(calendar.getTime()) + " (" + simpleDayFormat.format(calendar.getTime()) + ")");
            }
        }
        if(!datesAdded) {
            datesAdded = !datesAdded;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.complete_order, menu);
        return true;
    }
    // kan fjerne "i" loopen
    public void getOpening(final String date) {
        System.out.println(date);
        whichOpening = "first";
        for (int i = 0; i < 7; i++) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(simpleDateFormat.parse(date));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int dayOW = calendar.get(Calendar.DAY_OF_WEEK);
            System.out.println(dayOW);
            for (int j = 0; j < Integer.parseInt(rModel.getAmountOpenings() + 1); j++) {
                if (j == 1) {
                    whichOpening = "second";
                }
                else if (j == 2) {
                    whichOpening = "third";
                }
                if(!whichOpening.equals("closed")) {
                    if(rModel.getOpenings().get(whichOpening) != null) {
                        int fromD = Integer.parseInt(rModel.getOpenings().get(whichOpening).getFromDay());
                        int toD = Integer.parseInt(rModel.getOpenings().get(whichOpening).getToDay());
                        if (fromD < toD) {
                            if (dayOW >= fromD && dayOW <= toD) {
                                break;
                            }
                        } else if (toD < fromD) {
                            if (dayOW >= fromD && dayOW >= toD || toD == dayOW) {
                                break;
                            }
                        } else if (toD == fromD) {
                            if (dayOW == fromD) {
                                break;
                            }
                        }
                    }
                    else {
                        whichOpening = "closed";
                    }
                }

            }
            SimpleDateFormat simpleDayFormat = new SimpleDateFormat("EEEE");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


    // Sending user back to feed and clearing the cart.
    // Send in 0 if the restaurant is open and the order is completed
    // Send in 1 if the restaurant is closed
    // Send in 2 if the restaurant is busy
    public void returnToFeed(int status) {
        Intent intent = new Intent(CompleteOrder.this, MainUActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(status == 0) {
            Toast.makeText(this, "Thanks for your order", Toast.LENGTH_LONG).show();
            scheduleNotification(getNotification("PlateUP"), delay);
        }
        else if(status == 1) {
            Toast.makeText(this, "The restaurant is closed", Toast.LENGTH_LONG).show();
        }
        else if(status == 2) {
            Toast.makeText(this, "The restaurant is temporarily closed", Toast.LENGTH_SHORT).show();
            System.out.println("Application!!");
        }
        else if(status == 3) {
            Toast.makeText(this, "There was an error when trying to complete the order", Toast.LENGTH_SHORT).show();
        }

        MainUActivity.setRest_id(null);
        MainUActivity.setTotalPrice(0);
        MainUActivity.setRest_name(null);
        MainUActivity.setRest_wait_time(null);
        MainUActivity.getBasketList().clear();
        startActivity(intent);
        finish();
    }

    public void resetTime() {
        System.out.println("Resetter vi time?");
        timeRightNow = Calendar.getInstance();
        timeRightNow.setTime(MainUActivity.timeRightNow);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("HHmm");
        opening = rModel.getOpenings().get(whichOpening);
        try {
            currentTime = MainUActivity.timeRightNow;
            open = simpleFormat.parse(opening.getOpen());
            close = simpleFormat.parse(opening.getClose());
            System.out.println("OpenClose: " + open + " close: " + close);


            Calendar closeCalendar = Calendar.getInstance();
            closeCalendar.setTime(close);
            closeCalendar.add(Calendar.MINUTE, -15);
            closeCalendar.set(Calendar.YEAR, timeRightNow.get(Calendar.YEAR));
            closeCalendar.set(Calendar.MONTH, timeRightNow.get(Calendar.MONTH));
            closeCalendar.set(Calendar.DAY_OF_MONTH, timeRightNow.get(Calendar.DAY_OF_MONTH));

            close = closeCalendar.getTime();

            closeCalendar.setTime(open);
            closeCalendar.set(Calendar.YEAR, timeRightNow.get(Calendar.YEAR));
            closeCalendar.set(Calendar.MONTH, timeRightNow.get(Calendar.MONTH));
            closeCalendar.set(Calendar.DAY_OF_MONTH, timeRightNow.get(Calendar.DAY_OF_MONTH));
            closeCalendar.add(Calendar.MINUTE, 30);

            open = closeCalendar.getTime();
            openCopy = open;
            closeCopy = close;
            System.out.println("closeCopy: " + closeCopy);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void scheduleNotification(Notification notification, long delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay; // + delay
        System.out.println("FutureinMillis: " + futureInMillis);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Your order is ready for pickup!");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_menu_camera);
        builder.setAutoCancel(true);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this,Eatit.class), PendingIntent.FLAG_UPDATE_CURRENT));
        return builder.build();
    }
}
