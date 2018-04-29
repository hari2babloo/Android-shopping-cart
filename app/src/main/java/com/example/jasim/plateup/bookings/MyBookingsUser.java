package com.example.jasim.plateup.bookings;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jasim.plateup.R;
import com.example.jasim.plateup.RestaurantModel;
import com.example.jasim.plateup.registration.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.view.View.VISIBLE;

public class MyBookingsUser extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button mBack;
    private RecyclerView mBookingsList;
    private LinearLayout mNoBookings;



    // Restaurant reservation


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings_user);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("bookings");

        mBookingsList = (RecyclerView)findViewById(R.id.bookinglist);
        mBookingsList.setHasFixedSize(true);
        mBookingsList.setLayoutManager(new LinearLayoutManager(this));
        mBack = (Button)findViewById(R.id.back_button);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MyBookingsUser.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<BookingInfo, MyBookingsUser.ContentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookingInfo, MyBookingsUser.ContentViewHolder>(
                BookingInfo.class, R.layout.user_booking_row, MyBookingsUser.ContentViewHolder.class, mDatabase
        ) {


            @Override
            protected void populateViewHolder(final MyBookingsUser.ContentViewHolder viewHolder, final BookingInfo model, int position) {
                final BookingInfo modelUsername = model;
                System.out.println(modelUsername.getStatus());
                System.out.println(modelUsername.getRest_id());
                mDatabase.getParent().getParent().child(modelUsername.getRest_id()).child("bookings").child(modelUsername.getStatus()).child(modelUsername.getBookid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        BookingInfo modelR = dataSnapshot.getValue(BookingInfo.class);
                        viewHolder.setStatus(model.getStatus());
                        viewHolder.setDate(modelR.getDate());
                        viewHolder.setMinutes(modelR.getHour(), modelR.getMinutes());
                        viewHolder.setPersons(modelR.getPersons());
                        viewHolder.setTelephone(modelR.getTelephone());
                        viewHolder.setUsername(modelR.getUsername());
                        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(model.getStatus().equals("pending")) {
                                    mDatabase.child(model.getBookid()).removeValue();
                                    mDatabase.getParent().getParent().child(model.getRest_id()).child("bookings").child("pending").child(model.getBookid()).removeValue();
                                }
                                else if (model.getStatus().equals("accepted")) {
                                    mDatabase.child(model.getBookid()).removeValue();
                                    mDatabase.getParent().getParent().child(model.getRest_id()).child("bookings").child("accepted").child(model.getBookid()).removeValue();
                                }
                                else {
                                    viewHolder.mDeleteButton.setVisibility(View.GONE);
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mBookingsList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        Button mDeleteButton;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDeleteButton = (Button)mView.findViewById(R.id.delete_booking);
        }

        public void setUsername(String username) {
            TextView booking_username = (TextView) mView.findViewById(R.id.username);
            booking_username.setText(username);
        }

        public void setDate(String date) {
            TextView booking_date = (TextView) mView.findViewById(R.id.date);
            booking_date.setText(date);
        }

/*        public void setDay(String date) {
            TextView booking_day = (TextView) mView.findViewById(R.id.day);
            booking_day.setText("" + date);
            Calendar c = Calendar.getInstance();

            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        }*/

        public void setHour(String hour) {
            TextView booking_minutes = (TextView) mView.findViewById(R.id.time);
            booking_minutes.setText("" + hour);
        }

        public void setMinutes(String hour, String minutes) {
            TextView booking_minutes = (TextView) mView.findViewById(R.id.time);
            booking_minutes.setText(hour + ":" + minutes);
        }

        public void setPersons(String persons) {
            TextView booking_persons = (TextView) mView.findViewById(R.id.persons);
            booking_persons.setText("" + persons);
        }

        public void setTelephone(String telephone) {
            TextView booking_telephone = (TextView) mView.findViewById(R.id.telephone);
            booking_telephone.setText("" + telephone);
        }

        public void setStatus(String status) {
            TextView booking_status = (TextView) mView.findViewById(R.id.status);
            booking_status.setText("" + status);
        }

    }
}
