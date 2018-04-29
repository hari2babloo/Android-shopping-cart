package com.example.jasim.plateup.settings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.jasim.plateup.MainRActivity;
import com.example.jasim.plateup.Openings;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.RestaurantModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingsRestOpening extends AppCompatActivity {



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

    private int amountOpenings = 1;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private ArrayList<String> info = new ArrayList<>();

    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_rest_opening);

        amountOpenings = Integer.parseInt(MainRActivity.getRestaurant().getAmountOpenings());

        secondOpening = (CardView)findViewById(R.id.second_opening);
        thirdOpening = (CardView)findViewById(R.id.third_opening);

        addOpening = (Button)findViewById(R.id.add_opening);
        removeOpening = (Button)findViewById(R.id.remove_opening);
        if(amountOpenings == 1) {
            removeOpening.setVisibility(View.GONE);
        }

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


        Openings model = MainRActivity.getRestaurant().getOpenings().get("first");
        Openings model2 = MainRActivity.getRestaurant().getOpenings().get("second");
        Openings model3 = MainRActivity.getRestaurant().getOpenings().get("third");

        System.out.println(model.getOpen());
        System.out.println("minutter: " + model.getOpen().substring(2,4));

        fromDay.setAdapter(adapter);
        toDay.setAdapter(adapter);
        fromDay.setSelection(adapter.getPosition(convertNumbertoDay(Integer.parseInt(model.getFromDay()))));
        toDay.setSelection(adapter.getPosition(convertNumbertoDay(Integer.parseInt(model.getToDay()))));
        fromHour.setAdapter(adapterHours);
        fromHour.setSelection(adapterHours.getPosition(model.getOpen().substring(0,2)));
        fromMin.setAdapter(adapterMinutes);
        fromMin.setSelection(adapterMinutes.getPosition(model.getOpen().substring(2,4)));
        toHour.setAdapter(adapterHours);
        toHour.setSelection(adapterHours.getPosition(model.getClose().substring(0,2)));
        toMin.setAdapter(adapterMinutes);
        toMin.setSelection(adapterMinutes.getPosition(model.getClose().substring(2,4)));


        fromDay2.setAdapter(adapter);
        toDay2.setAdapter(adapter);
        fromHour2.setAdapter(adapterHours);
        fromMin2.setAdapter(adapterMinutes);
        toHour2.setAdapter(adapterHours);
        toMin2.setAdapter(adapterMinutes);
        toDay2.setSelection(adapter.getPosition("Sunday"));

        if(amountOpenings >= 2) {
            fromDay2.setSelection(adapter.getPosition(convertNumbertoDay(Integer.parseInt(model2.getFromDay()))));
            toDay2.setSelection(adapter.getPosition(convertNumbertoDay(Integer.parseInt(model2.getToDay()))));

            secondOpening.setVisibility(View.VISIBLE);
            fromHour2.setSelection(adapterHours.getPosition(model2.getOpen().substring(0, 2)));
            fromMin2.setSelection(adapterMinutes.getPosition(model2.getOpen().substring(2, 4)));

            toHour2.setSelection(adapterHours.getPosition(model2.getClose().substring(0, 2)));
            toMin2.setSelection(adapterMinutes.getPosition(model2.getClose().substring(2, 4)));
        }


        fromDay3.setAdapter(adapter);
        toDay3.setAdapter(adapter);
        fromHour3.setAdapter(adapterHours);
        fromMin3.setAdapter(adapterMinutes);
        toHour3.setAdapter(adapterHours);
        toMin3.setAdapter(adapterMinutes);
        toDay3.setSelection(adapter.getPosition("Sunday"));

        if(amountOpenings == 3) {
            fromDay3.setSelection(adapter.getPosition(convertNumbertoDay(Integer.parseInt(model3.getFromDay()))));
            toDay3.setSelection(adapter.getPosition(convertNumbertoDay(Integer.parseInt(model3.getToDay()))));

            thirdOpening.setVisibility(View.VISIBLE);
            fromHour3.setSelection(adapterHours.getPosition(model3.getOpen().substring(0, 2)));
            fromMin3.setSelection(adapterMinutes.getPosition(model3.getOpen().substring(2, 4)));

            toHour3.setSelection(adapterHours.getPosition(model3.getClose().substring(0, 2)));
            toMin3.setSelection(adapterMinutes.getPosition(model3.getClose().substring(2, 4)));
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        //


        // Kall removeOpenings om amountopenings er større enn 2 og første toDay er søndag.
        toDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(toDay.getSelectedItem().toString().equals("Sunday")) {
                    secondOpening.setVisibility(View.GONE);
                    thirdOpening.setVisibility(View.GONE);
                    amountOpenings = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toDay2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(toDay2.getSelectedItem().toString().equals("Sunday")) {
                    thirdOpening.setVisibility(View.GONE);
                    amountOpenings = 2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                    removeOpening.setVisibility(View.GONE);
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
                System.out.println(amountOpenings);
                ArrayList<String> test = new ArrayList<String>();
                if(toDay.getSelectedItem().toString().equals("Sunday")) {
                    System.out.println("Cant add more opening days");
                }
                else if(amountOpenings == 1) {
                    fromDay2.setSelection(1+ adapter.getPosition(toDay.getSelectedItem().toString()));
                    secondOpening.setVisibility(View.VISIBLE);
                    removeOpening.setVisibility(View.VISIBLE);
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

            mDatabase.child("openings").child("first").child("fromDay").setValue(fromDaySelectedItem);
            mDatabase.child("openings").child("first").child("toDay").setValue(toDaySelectedItem);
            mDatabase.child("openings").child("first").child("open").setValue(fromHourSelectedItem + "" + fromMinSelectedItem);
            mDatabase.child("openings").child("first").child("close").setValue(toHourSelectedItem + "" + toMinutesSelectedItem);

            if(amountOpenings >= 2) {
                mDatabase.child("openings").child("second").child("fromDay").setValue(fromDay2SelectedItem);
                mDatabase.child("openings").child("second").child("toDay").setValue(toDay2SelectedItem);
                mDatabase.child("openings").child("second").child("open").setValue(fromHour2SelectedItem + "" + fromMin2SelectedItem);
                mDatabase.child("openings").child("second").child("close").setValue(toHour2SelectedItem + "" + toMin2SelectedItem);
                mDatabase.child("amountopenings").setValue("2");
            }
            if(amountOpenings == 3) {
                mDatabase.child("openings").child("third").child("fromDay").setValue(fromDay3SelectedItem);
                mDatabase.child("openings").child("third").child("toDay").setValue(toDay3SelectedItem);
                mDatabase.child("openings").child("third").child("open").setValue(fromHour3SelectedItem + "" + fromMin3SelectedItem);
                mDatabase.child("openings").child("third").child("close").setValue(toHour3SelectedItem + "" + toMin3SelectedItem);
                mDatabase.child("amountopenings").setValue("3");
            }
            if(amountOpenings < 3) {
                mDatabase.child("openings").child("third").removeValue();
                mDatabase.child("amountopenings").setValue("2");
                if (amountOpenings < 2) {
                    mDatabase.child("amountopenings").setValue("1");
                    mDatabase.child("openings").child("second").removeValue();
                }
            }
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    RestaurantModel model = dataSnapshot.getValue(RestaurantModel.class);
                    model.setAmountOpenings(Integer.toString(amountOpenings));
                    MainRActivity.setmRestaurantModel(dataSnapshot.getValue(RestaurantModel.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            startActivity(new Intent(SettingsRestOpening.this, SettingsRest.class));

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

    public String convertNumbertoDay(int num) {
        if(num == 2) {
            return "Monday";
        }
        else if(num == 3) {
            return "Tuesday";
        }
        else if(num == 4) {
            return "Wednesday";
        }
        else if(num == 5) {
            return "Thursday";
        }
        else if(num == 6) {
            return "Friday";
        }
        else if(num == 7) {
            return "Saturday";
        }
        else if(num == 1) {
            return "Sunday";
        }
        return "asap rocky";
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }
}
