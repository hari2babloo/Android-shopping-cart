package com.example.jasim.plateup.registration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.jasim.plateup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class RestReg3 extends AppCompatActivity {



    private Button mNext;
    private Button mBack;
    private Button addOpening;
    private Button removeOpening;

    private CardView secondOpening;
    private CardView thirdOpening;

    ArrayAdapter<CharSequence> adapter;

    Spinner fromDay;
    Spinner toDay;
    Spinner fromHour;
    Spinner fromMin;
    Spinner toHour;
    Spinner toMin;

    Spinner fromDay2;
    Spinner toDay2;
    Spinner fromHour2;
    Spinner fromMin2;
    Spinner toHour2;
    Spinner toMin2;

    Spinner fromDay3;
    Spinner toDay3;
    Spinner fromHour3;
    Spinner fromMin3;
    Spinner toHour3;
    Spinner toMin3;

    public static String time;
    public static String toDaysDate;
    public static Date day;

    private int amountOpenings = 1;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ArrayList<String> info = new ArrayList<>();

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_reg3);

        Bundle extras = getIntent().getExtras();

        info = extras.getStringArrayList("info");
        System.out.println(info.size());

        secondOpening = (CardView)findViewById(R.id.second_opening);
        thirdOpening = (CardView)findViewById(R.id.third_opening);

        addOpening = (Button)findViewById(R.id.add_opening);
        removeOpening = (Button)findViewById(R.id.remove_opening);

        fromDay = (Spinner) findViewById(R.id.from_day);
        toDay = (Spinner) findViewById(R.id.to_day);
        fromHour = (Spinner) findViewById(R.id.open_h);
        fromMin = (Spinner) findViewById(R.id.open_m);
        toHour = (Spinner) findViewById(R.id.close_h);
        toMin = (Spinner) findViewById(R.id.close_m);


        fromDay2 = (Spinner) findViewById(R.id.from_day2);
        toDay2 = (Spinner) findViewById(R.id.to_day2);
        fromHour2 = (Spinner) findViewById(R.id.open_h2);
        fromMin2 = (Spinner) findViewById(R.id.open_m2);
        toHour2 = (Spinner) findViewById(R.id.close_h2);
        toMin2 = (Spinner) findViewById(R.id.close_m2);


        fromDay3 = (Spinner) findViewById(R.id.from_day3);
        toDay3 = (Spinner) findViewById(R.id.to_day3);
        fromHour3 = (Spinner) findViewById(R.id.open_h3);
        fromMin3 = (Spinner) findViewById(R.id.open_m3);
        toHour3 = (Spinner) findViewById(R.id.close_h3);
        toMin3 = (Spinner) findViewById(R.id.close_m3);


        adapter = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMinutes = ArrayAdapter.createFromResource(this,
                R.array.minutes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromDay.setAdapter(adapter);
        toDay.setAdapter(adapter);
        fromHour.setAdapter(adapterHours);
        fromMin.setAdapter(adapterMinutes);
        toHour.setAdapter(adapterHours);
        toMin.setAdapter(adapterMinutes);
        toDay.setSelection(adapter.getPosition("Sunday"));

        fromDay2.setAdapter(adapter);
        toDay2.setAdapter(adapter);
        fromHour2.setAdapter(adapterHours);
        fromMin2.setAdapter(adapterMinutes);
        toHour2.setAdapter(adapterHours);
        toMin2.setAdapter(adapterMinutes);
        toDay2.setSelection(adapter.getPosition("Sunday"));

        fromDay3.setAdapter(adapter);
        toDay3.setAdapter(adapter);
        fromHour3.setAdapter(adapterHours);
        fromMin3.setAdapter(adapterMinutes);
        toHour3.setAdapter(adapterHours);
        toMin3.setAdapter(adapterMinutes);
        toDay3.setSelection(adapter.getPosition("Sunday"));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");


        final Dialog dialog = new Dialog(this);

        mNext = (Button) findViewById(R.id.next_button);
        mProgress = new ProgressDialog(this);
        mBack = (Button) findViewById(R.id.back_button);


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        removeOpening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amountOpenings == 2) {
                    secondOpening.setVisibility(View.GONE);
                    amountOpenings--;
                }
                else if(amountOpenings == 3) {
                    thirdOpening.setVisibility(View.GONE);
                    amountOpenings--;
                }
            }
        });

        addOpening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> test = new ArrayList<String>();
                if(toDay.getSelectedItem().toString().equals("Sunday")) {
                    System.out.println("Cant add more opening days");
                }
                 else if(amountOpenings == 1) {
                    fromDay2.setSelection(1+ adapter.getPosition(toDay.getSelectedItem().toString()));
                    secondOpening.setVisibility(View.VISIBLE);
                    amountOpenings++;
                    System.out.println(amountOpenings);
                }
                else if(amountOpenings == 2) {
                    if(!(toDay2.getSelectedItem().toString().equals("Sunday"))) {
                        fromDay3.setSelection(1+ adapter.getPosition(toDay2.getSelectedItem().toString()));
                        thirdOpening.setVisibility(View.VISIBLE);
                        amountOpenings++;
                        System.out.println(amountOpenings);
                    }
                    else if(toDay2.getSelectedItem().toString().equals("Thursday")) {
                        fromDay3.setSelection(adapter.getPosition("Friday"));
                        secondOpening.setVisibility(View.VISIBLE);
                        System.out.println(amountOpenings);
                    }

                }
            }
        });


    }
    private void startRegister() {

        final String fromDaySelectedItem  = Integer.toString(convertDaytoNumber((String)fromDay.getSelectedItem()));
        final String toDaySelectedItem = Integer.toString(convertDaytoNumber((String)toDay.getSelectedItem()));
        final String fromHourSelectedItem = (String)fromHour.getSelectedItem();
        final String fromMinSelectedItem = (String)fromMin.getSelectedItem();
        final String toHourSelectedItem = (String)toHour.getSelectedItem();
        final String toMinutesSelectedItem = (String)toMin.getSelectedItem();
        final String fromDay2SelectedItem  = Integer.toString(convertDaytoNumber((String)fromDay2.getSelectedItem()));
        final String toDay2SelectedItem = Integer.toString(convertDaytoNumber((String)toDay2.getSelectedItem()));
        final String fromHour2SelectedItem = (String)fromHour2.getSelectedItem();
        final String fromMin2SelectedItem = (String)fromMin2.getSelectedItem();
        final String toHour2SelectedItem = (String)toHour2.getSelectedItem();
        final String toMin2SelectedItem = (String)toMin2.getSelectedItem();

        final String fromDay3SelectedItem  = Integer.toString(convertDaytoNumber((String)fromDay3.getSelectedItem()));
        final String toDay3SelectedItem = Integer.toString(convertDaytoNumber((String)toDay3.getSelectedItem()));
        final String fromHour3SelectedItem = (String)fromHour3.getSelectedItem();
        final String fromMin3SelectedItem = (String)fromMin3.getSelectedItem();
        final String toHour3SelectedItem = (String)toHour3.getSelectedItem();
        final String toMin3SelectedItem = (String)toMin3.getSelectedItem();

        if (Integer.parseInt(fromHourSelectedItem) > 0 && Integer.parseInt(toHourSelectedItem) < 24) {
            System.out.println("Amountopenings er: " + amountOpenings);
            info.add(Integer.toString(amountOpenings));


            info.add(fromDaySelectedItem);
            info.add(toDaySelectedItem);
            info.add(fromHourSelectedItem);
            info.add(fromMinSelectedItem);
            info.add(toHourSelectedItem);
            info.add(toMinutesSelectedItem);

            info.add(fromDay2SelectedItem);
            info.add(toDay2SelectedItem);
            info.add(fromHour2SelectedItem);
            info.add(fromMin2SelectedItem);
            info.add(toHour2SelectedItem);
            info.add(toMin2SelectedItem);


            info.add(fromDay3SelectedItem);
            info.add(toDay3SelectedItem);
            info.add(fromHour3SelectedItem);
            info.add(fromMin3SelectedItem);
            info.add(toHour3SelectedItem);
            info.add(toMin3SelectedItem);

            startActivity(new Intent(RestReg3.this, RestReg4.class).putExtra("info", info));

        }
    }
    public int convertDaytoNumber(String day) {
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
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }
}
