package com.example.jasim.plateup.bookings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.jasim.plateup.R;
import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.RestaurantModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    CalendarView mCalender;
    NumberPicker mHour;
    NumberPicker mMinutes;
    Button mBook;
    TextView nav_user;
    Button mBack;
    LinearLayout mBook_button;

    boolean goFurther = false;


    TextView mOpensAgain;

    LinearLayout mDisableBook;
    LinearLayout mShowBook;
    private String[] arrayHour = new String[24];
    private String[] arrayMinutes = new String[60];

    int hours;
    int minutes;

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = (1+Calendar.getInstance().get(Calendar.MONTH));
    int date = Calendar.getInstance().get(Calendar.DATE);

    String rest_id;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference mRestModel;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        mRestModel = FirebaseDatabase.getInstance().getReference().child("Users").child(id);

        mCalender = (CalendarView)findViewById(R.id.datepicker);
        mCalender.setMinDate(System.currentTimeMillis());
        mDisableBook = (LinearLayout)findViewById(R.id.disable_book);
        mOpensAgain = (TextView)findViewById(R.id.opens_again);
        mShowBook = (LinearLayout)findViewById(R.id.show_booking);
        mHour = (NumberPicker) findViewById(R.id.hours);
        mRestModel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RestaurantModel restModel = dataSnapshot.getValue(RestaurantModel.class);
                checkBookDate(year, month-1, date, restModel);
                System.out.println("Maaneden i dag er: " + month);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mMinutes = (NumberPicker) findViewById(R.id.minutes);
        mMinutes.setMinValue(0);
        mMinutes.setMaxValue(59);
        mMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        mBook = (Button)findViewById(R.id.book);

        hours = mHour.getValue();
        minutes = mMinutes.getValue();

        mProgress = new ProgressDialog(this);

        Intent i = getIntent();
        rest_id = i.getStringExtra("id");
        mBack = (Button)findViewById(R.id.back_button);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView arg0, final int year, final int month,
                                            final int day) {
                mDisableBook.setVisibility(View.VISIBLE);

                mRestModel.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        RestaurantModel restModel = dataSnapshot.getValue(RestaurantModel.class);
                        checkBookDate(year, month, day, restModel);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                storeDate(year, (month), day);
            }
        });

        mBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> dateData = new ArrayList<String>();
                dateData.add("" + year);
                dateData.add("" + month);
                dateData.add("" + date);
                dateData.add("" + mHour.getValue());
                dateData.add("" + mMinutes.getValue());
                dateData.add("" + rest_id);
                startActivity(new Intent(BookingActivity.this, Booking2Activity.class).putStringArrayListExtra("booking", (ArrayList<String>) dateData));

            }
        });
    }

    public void checkBookDate(int year, int month, int day, RestaurantModel restModel) {
        RestaurantModel model = restModel;
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK);
        String timeStamp = new SimpleDateFormat("HHmm").format(Calendar.getInstance().getTime());
        int currentTime = Integer.parseInt(timeStamp);
        int toDay, fromDay;
        String whichOpening = "first";
        for(int i = 0; i < Integer.parseInt(restModel.getAmountOpenings()); i++) {
            if (i == 1) {
                whichOpening = "second";
            } else if (i == 2) {
                whichOpening = "third";
            }

            fromDay = Integer.parseInt(restModel.getOpenings().get(whichOpening).getFromDay());
            toDay = Integer.parseInt(restModel.getOpenings().get(whichOpening).getToDay());
            System.out.println("TESTING:");
            System.out.println("toDay: " + toDay);
            System.out.println("fromDay: " + fromDay);
            System.out.println("Day of the week: " + day_of_week);
            System.out.println("whichopenings: " + whichOpening);
            if(fromDay < toDay) {
                if (day_of_week >= fromDay && day_of_week <= toDay) {
                    mBook.setVisibility(View.VISIBLE);
                    mShowBook.setVisibility(View.INVISIBLE);
                    int open, close;
                    open = Integer.parseInt(restModel.getOpenings().get(whichOpening).getOpen());
                    close = Integer.parseInt(restModel.getOpenings().get(whichOpening).getClose());
                    if(open > close) {
                        System.out.println((24-(((open-close)) / 100)));
                        String[] values = new String[(24-(((open-close)) / 100))];
                        mHour.setMinValue(0);
                        mHour.setMaxValue(values.length-1);
                        int openTime = Integer.parseInt(Integer.toString(open).substring(0,2));
                        for(int j = 0; j < values.length; j++) {
                            values[j] = Integer.toString(openTime);
                            openTime += 1;
                            if(openTime == 24) {
                                openTime = 0;
                            }
                        }
                        System.out.println("vi gjor dette!!");
                        mHour.setDisplayedValues(values);
                    }
                    else {
                        System.out.println("Vi kommer inn HIT! " + open);
                        mHour.setMinValue(Integer.parseInt(Integer.toString(open).substring(0,2)));
                        mHour.setMaxValue((Integer.parseInt(Integer.toString(close).substring(0,2)))-1);
                    }
                    mHour.setFormatter(new NumberPicker.Formatter() {
                        @Override
                        public String format(int i) {
                            return String.format("%02d", i);
                        }
                    });
                    goFurther = true;
                    break;
                }
            }
            else {
                if (day_of_week >= fromDay && day_of_week >= toDay || day_of_week <= fromDay && day_of_week <= toDay) {
                    mShowBook.setVisibility(View.INVISIBLE);
                    mBook.setVisibility(View.VISIBLE);
                    int maxVal = Integer.parseInt(restModel.getOpenings().get(whichOpening).getClose().substring(0, 2));
                    mHour.setMinValue(Integer.parseInt(restModel.getOpenings().get(whichOpening).getOpen().substring(0, 2)));
                    if(maxVal == 0) {
                        maxVal = 12;
                    }
                    mHour.setMaxValue(maxVal - 1);
                    goFurther = true;
                    break;
                }
            }
            if(i == (Integer.parseInt(restModel.getAmountOpenings())) -1) {
                System.out.println("Butikken er stengt");
                mBook.setVisibility(View.INVISIBLE);
                mShowBook.setVisibility(View.VISIBLE);
                // mOpensAgain.setText(convertNumbertoDay(restModel.getOpenings().get("first").getFromDay()) + " " + restModel.getOpenings().get("first").getOpen().substring(0, 2) + "." + restModel.getOpenings().get("first").getOpen().substring(2, 4));
                mDisableBook.setVisibility(View.GONE);
            }
        }
    }
    public String convertNumbertoDay(String day) {
        if (day.equals("2")) {
            return "Monday";
        } else if (day.equals("3")) {
            return "Tuesday";
        } else if (day.equals("4")) {
            return "Wednesday";
        } else if (day.equals("5")) {
            return "Thursday";
        } else if (day.equals("6")) {
            return "Friday";
        } else if (day.equals("7")) {
            return "Saturday";
        } else if (day.equals("1")) {
            return "Sunday";
        }
        return "";
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.booking, menu);
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


    private void storeDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.date = day;
    }
}
