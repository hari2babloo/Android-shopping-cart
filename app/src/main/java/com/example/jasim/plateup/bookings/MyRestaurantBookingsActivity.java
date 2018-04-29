package com.example.jasim.plateup.bookings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jasim.plateup.R;
import com.example.jasim.plateup.registration.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MyRestaurantBookingsActivity extends AppCompatActivity {


    private RecyclerView mBookingList;
    private DatabaseReference mDatabase;
    private DatabaseReference mBookingMover;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mConfirm;
    private Button mDeny;
    private Button mBack;
    private TextView mReservation;
    private TextView mNewBooking;
    private Query test;
    private boolean bookings;
    private LinearLayout mNoBookings;

    private NestedScrollView mReservationActivity;

    // Restaurant reservation
    private CalendarView mCalender;
    FirebaseRecyclerAdapter<BookingInfo, ContentViewHolder2> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<BookingInfo, ContentViewHolder2> firebaseRecyclerAdapter2;
    private RecyclerView mBookingList2;
    private Query mReservedBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_restaurant_bookings);
        mAuth = FirebaseAuth.getInstance();
        mNoBookings = (LinearLayout)findViewById(R.id.no_bookings);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("bookings");
        mCalender = (CalendarView)findViewById(R.id.calendarView);
        mReservedBookings = mDatabase.orderByChild("date").equalTo("2017/3/23");
        mReservation = (TextView) findViewById(R.id.reservation);
        mReservationActivity = (NestedScrollView)findViewById(R.id.activity_rest_reservation);
        mNewBooking = (TextView)findViewById(R.id.new_bookings);
        mReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReservationActivity.setVisibility(View.VISIBLE);

                mNewBooking.setPaintFlags(mNewBooking.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                mNewBooking.setPaintFlags(mNewBooking.getPaintFlags() & (~ Paint.FAKE_BOLD_TEXT_FLAG));


                mReservation.setPaintFlags(mReservation.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                mReservation.setPaintFlags(mReservation.getPaintFlags() |   Paint.FAKE_BOLD_TEXT_FLAG);
            }
        });

        mNewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReservationActivity.setVisibility(View.GONE);
                mReservation.setPaintFlags(mReservation.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                mReservation.setPaintFlags(mReservation.getPaintFlags() & (~ Paint.FAKE_BOLD_TEXT_FLAG));

                mNewBooking.setPaintFlags(mNewBooking.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                mNewBooking.setPaintFlags(mNewBooking.getPaintFlags() |   Paint.FAKE_BOLD_TEXT_FLAG);
            }
        });

        mBack = (Button)findViewById(R.id.back_button);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mConfirm = (Button)findViewById(R.id.confirm);
        mDeny = (Button)findViewById(R.id.deny);
        mNewBooking.setPaintFlags(mNewBooking.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        mNewBooking.setPaintFlags(mNewBooking.getPaintFlags() |   Paint.FAKE_BOLD_TEXT_FLAG);

        mBookingList = (RecyclerView)findViewById(R.id.bookinglist);
        mBookingList2 = (RecyclerView)findViewById(R.id.bookinglist2);
        mBookingList.setHasFixedSize(true);
        mBookingList.setLayoutManager(new LinearLayoutManager(this));


        mBookingList2.setLayoutManager(new LinearLayoutManager(this));
        mBookingList2.setHasFixedSize(false);
        mBookingList2.setNestedScrollingEnabled(false);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MyRestaurantBookingsActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };
        mCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView arg0, int year, int month,
                                            int date) {
                System.out.println("HAR KJORT DENNE NAA");
                storeDate(year, (month+1), date);
            }

        });

    }

    private void storeDate(int year, int month, int day) {
        mReservedBookings = mDatabase.child("accepted").orderByChild("date").equalTo(year + "/" + month + "/" + day);
        firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<BookingInfo, ContentViewHolder2>(
                BookingInfo.class, R.layout.reservation_row, ContentViewHolder2.class, mReservedBookings
        )
        {
            @Override
            protected void populateViewHolder(ContentViewHolder2 viewHolder, BookingInfo model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setPersons(model.getPersons());
                viewHolder.setTelephone(model.getTelephone());
                viewHolder.setTime(model.getTime());

            }
        };
        mBookingList2.setAdapter(firebaseRecyclerAdapter2);

    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookingInfo, ContentViewHolder2>(
                BookingInfo.class, R.layout.reservation_row, ContentViewHolder2.class, mReservedBookings
        ) {
            @Override
            protected void populateViewHolder(ContentViewHolder2 viewHolder, BookingInfo model, int position) {
                viewHolder.setUsername(model.getUsername());

            }
        };
        mBookingList2.setAdapter(firebaseRecyclerAdapter);
        mAuth.addAuthStateListener(mAuthListener);
        final FirebaseRecyclerAdapter<BookingInfo, MyRestaurantBookingsActivity.ContentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BookingInfo, MyRestaurantBookingsActivity.ContentViewHolder>(
                BookingInfo.class, R.layout.restaurant_booking_row, MyRestaurantBookingsActivity.ContentViewHolder.class, mDatabase.child("pending")
        ) {


            @Override
            protected void populateViewHolder(ContentViewHolder viewHolder, final BookingInfo model, int position) {
                final BookingInfo modelUsername = model;
                viewHolder.setDate(model.getDate());
                viewHolder.setMinutes(model.getHour(), model.getMinutes());
                viewHolder.setPersons(model.getPersons());
                viewHolder.setTelephone(model.getTelephone());
                viewHolder.setUsername(model.getUsername());

                viewHolder.mView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("bookid").setValue(model.getBookid());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("date").setValue(model.getDate());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("hour").setValue(model.getHour());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("persons").setValue(model.getPersons());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("minutes").setValue(model.getMinutes());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("telephone").setValue(model.getTelephone());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("username").setValue(model.getUsername());
                                mDatabase.child("accepted").child(modelUsername.getBookid()).child("user_id").setValue(model.getUser_id());

                                mDatabase.getParent().getParent().child(modelUsername.getUser_id()).child("bookings").child(modelUsername.getBookid()).child("status").setValue("accepted");





                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                       mDatabase.child("pending").child(modelUsername.getBookid()).removeValue();
                    }
                });
                viewHolder.mView.findViewById(R.id.deny).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // mBookingMover = FirebaseDatabase.getInstance().getReference().child("Users").child(modelUsername.getUsername()).child("bookings");
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("bookid").setValue(model.getBookid());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("date").setValue(model.getDate());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("hour").setValue(model.getHour());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("persons").setValue(model.getPersons());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("minutes").setValue(model.getMinutes());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("telephone").setValue(model.getTelephone());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("username").setValue(model.getUsername());
                                mDatabase.child("denied").child(modelUsername.getBookid()).child("user_id").setValue(model.getUser_id());


                                mDatabase.getParent().getParent().child(modelUsername.getUser_id()).child("bookings").child(modelUsername.getBookid()).child("status").setValue("denied");

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child("pending").child(modelUsername.getBookid()).removeValue();
                    }
                });
            }
        };


        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(firebaseRecyclerAdapter.getItemCount() > 0) {
                    mNoBookings.setVisibility(View.GONE);
                }
                System.out.println(itemCount);
            }
        });

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if(firebaseRecyclerAdapter.getItemCount() == 0) {
                    mNoBookings.setVisibility(View.VISIBLE);
                }
            }
        });
        mBookingList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
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

    }
    public static class ContentViewHolder2 extends RecyclerView.ViewHolder {

        View mView;

        public ContentViewHolder2 (View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username) {
            TextView booking_username = (TextView) mView.findViewById(R.id.username);
            booking_username.setText(username);
        }

        public void setTime(String time) {
            TextView booking_time = (TextView) mView.findViewById(R.id.time);
            booking_time.setText(time);
        }

        public void setPersons(String persons) {
            TextView booking_persons = (TextView)mView.findViewById(R.id.persons);
            booking_persons.setText(persons);
        }

        public void setTelephone(String telephone) {
            TextView booking_telephone = (TextView) mView.findViewById(R.id.telephone);
            booking_telephone.setText(telephone);
        }



    }
}
