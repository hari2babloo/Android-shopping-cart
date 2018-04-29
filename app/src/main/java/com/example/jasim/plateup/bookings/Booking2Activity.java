package com.example.jasim.plateup.bookings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.DayMonthConverter;
import com.example.jasim.plateup.ProfileRestaurant;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.RestaurantModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Booking2Activity extends AppCompatActivity {

    Button mBook;
    Button mBack;
    Button mMinus;
    Button mPlus;

    TextView mTelephone;
    TextView mPersons;
    TextView mDate;
    TextView mTime;

    RestaurantModel username;

    int year = Calendar.getInstance().get(Calendar.YEAR);
    int month = (1+Calendar.getInstance().get(Calendar.MONTH));
    int date = Calendar.getInstance().get(Calendar.DATE);

    int persons = 1;

    String rest_id;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference mRestModel;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking2);

        Intent intent=getIntent();
        final ArrayList<String> dateData = intent.getStringArrayListExtra("booking");

        mDate = (TextView)findViewById(R.id.date);
        mTime = (TextView)findViewById(R.id.time);
        String date = dateData.get(2) + "/" + DayMonthConverter.numberToMonth((Integer.parseInt(dateData.get(1)))) + "/" + dateData.get(0);
        String time = String.format("%02d", (Integer.parseInt(dateData.get(3)))) + "." + String.format("%02d", (Integer.parseInt(dateData.get(4))));
        mDate.setText(date);
        mTime.setText(time);

        mPersons = (TextView)findViewById(R.id.amount_field);
        mMinus = (Button)findViewById(R.id.minus_field);
        mPlus = (Button)findViewById(R.id.plus_field);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mBook = (Button)findViewById(R.id.book);

        mTelephone = (TextView)findViewById(R.id.telephone);
        mProgress = new ProgressDialog(this);


        rest_id = dateData.get(5);
        mBack = (Button)findViewById(R.id.back_button);

        mPersons.setText("" + persons);
        mPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persons++;
                mPersons.setText("" + persons);
            }
        });

        mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(persons > 1) {
                    persons--;
                    mPersons.setText("" + persons);
                }
            }
        });


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mTelephone.getText().toString().isEmpty()) {
                    mTelephone.setError("Please specify a telephone number");
                }
                else {
                    bookTable(dateData, mTelephone.getText().toString().trim(), mPersons.getText().toString().trim());
                }
            }
        });
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

    private void bookTable(final ArrayList<String> dateData, String telephone, String persons) {

        mProgress.setMessage("Booking table..");
        mProgress.show();
        Toast.makeText(this, "Thank you for your booking. Please wait for the restaurants confirmation", Toast.LENGTH_LONG).show();
        final String user_id = mAuth.getCurrentUser().getUid();

        mDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(RestaurantModel.class);

                DatabaseReference restaurantDb = mDatabase.child(rest_id).child("bookings").child("pending");
                DatabaseReference userDb = mDatabase.child(mAuth.getCurrentUser().getUid()).child("bookings");
                HashMap<String, String> book = new HashMap<>();

                book.put("user_id", user_id);
                book.put("username", username.getUsername());
                book.put("date", dateData.get(0) + "/" + (Integer.parseInt(dateData.get(1))) + "/" + dateData.get(2));
                book.put("hour", String.format("%02d", Integer.parseInt(dateData.get(3))));
                book.put("minutes", String.format("%02d", Integer.parseInt(dateData.get(4))));
                book.put("telephone", mTelephone.getText().toString());
                book.put("persons", mPersons.getText().toString());
                String id = restaurantDb.push().getKey();
                book.put("bookid", id);
                restaurantDb.child(id).setValue(book);

                userDb.child(id).child("bookid").setValue(id);
                userDb.child(id).child("rest_id").setValue(rest_id);
                userDb.child(id).child("status").setValue("pending");

                mProgress.dismiss();

                DatabaseReference restName = mDatabase.child(rest_id);

                restName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RestaurantModel rModel = dataSnapshot.getValue(RestaurantModel.class);
                        Intent mainIntent = new Intent(Booking2Activity.this, ProfileRestaurant.class).putExtra("restaurant", rModel.getId());
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
