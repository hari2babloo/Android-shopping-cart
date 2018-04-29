package com.example.jasim.plateup;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.adapters.MenuAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nshmura.recyclertablayout.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class YourOrders extends AppCompatActivity implements ActivityChanger {

    private RecyclerView recyclerView;
    private RecyclerView mContentList;

    private String whichOpening;
    private Button next;
    private TextView total_price_field;
    private static int total_Price;
    private TextView mRestName;
    private Button mAddMore;
    private ContentAdapter adapter;
    private CoordinatorLayout showDishes;
    private RelativeLayout showOrders;
    private TextView menu;
    private TextView offers;
    private TextView bestsellers;
    private RestaurantModel rModel;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private DatabaseReference menuReference;

    private LinearLayout menuContent;
    private LinearLayout offersContent;
    private LinearLayout bestsellerContent;

    private Button eat_it;
    private Button mBack;
    private Button mBackButton;

    private RestaurantModel modelO;

    private LinearLayout mClosed;
    private LinearLayout mBusy;
    private DatabaseReference closeTime;

    protected RecyclerTabLayout mRecyclerTabLayout;
    private HashMap<Integer, String> categories = new HashMap<>();
    private int iterator;

    HashMap<Integer, HashMap<String, Content>> dishes = new HashMap<>();

    private ArrayList<Content> offersList = new ArrayList<>();

    private static Activity activity;
    private FirebaseRecyclerAdapter<Content, MainUActivity.ContentViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = YourOrders.this;
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        setContentView(R.layout.activity_your_orders);
        Intent i = getIntent();

        mBack = findViewById(R.id.back_button);
        mBackButton = findViewById(R.id.back_button2);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        menuContent = findViewById(R.id.menu);


        mRestName = findViewById(R.id.rest_name_cart);
        mRestName.setText(MainUActivity.getRest_name());
        total_price_field = findViewById(R.id.total_price);
        total_price_field.setText("" + MainUActivity.getTotalPrice() + " kr");
        recyclerView = findViewById(R.id.basketList);
        mAddMore = findViewById(R.id.add_more_cart);
        showOrders = findViewById(R.id.show_orders);
        showDishes = findViewById(R.id.show_dishes);
        if(MainUActivity.getRest_id() != null) {
            closeTime = FirebaseDatabase.getInstance().getReference().child("Users").child(MainUActivity.getRest_id());
            closeTime.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(RestaurantModel.class).getStatus().equals("closed")) {
                        returnToFeed(2);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("offers");
        if(MainUActivity.getRest_id() != null) {
            menuReference = FirebaseDatabase.getInstance().getReference().child("Users").child(MainUActivity.getRest_id()).child("Menu");
            dishes = new HashMap<>();
            iterator = 1;
            categories.put(0, "Offers");
            menuReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final Query offers = FirebaseDatabase.getInstance().getReference("offers").orderByChild("rest_id").equalTo(MainUActivity.getRest_id());

                    offers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                System.out.println("hashMap: " + postSnapshot.getValue());
                                dishes.put(iterator, (HashMap<String, Content>)postSnapshot.getValue());
                                categories.put(iterator, postSnapshot.getKey());
                                iterator++;
                            }
                            for(DataSnapshot snap: dataSnapshot2.getChildren()) {
                                offersList.add(snap.getValue(Content.class));
                            }

                            System.out.println("Categories: " + categories);
                            MenuAdapter adapter = new MenuAdapter(offersList, dishes, categories, getApplicationContext(), YourOrders.this, MainUActivity.getRest_id());

                            ViewPager viewPager = findViewById(R.id.view_pager);
                            viewPager.setAdapter(adapter);

                            mRecyclerTabLayout = findViewById(R.id.recycler_tab_layout);
                            mRecyclerTabLayout.setUpWithViewPager(viewPager);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    // ...
                }
            });
        }

        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrders.startAnimation(slide_down);
                showOrders.setVisibility(View.GONE);
                showDishes.setVisibility(View.VISIBLE);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrders.startAnimation(slide_up);
                showDishes.setVisibility(View.VISIBLE);
                startActivity(new Intent(YourOrders.this, YourOrders.class));
            }
        });

        if(MainUActivity.getRest_id() != null) {
            mAddMore.setVisibility(View.VISIBLE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentAdapter(YourOrders.this);
        recyclerView.setAdapter(adapter);
        next = findViewById(R.id.order_button);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainUActivity.getRest_id() != null && MainUActivity.getRest_name() != null) {
                    DatabaseReference closeTime = mDatabase.getParent().child("Users").child(MainUActivity.getRest_id());

                    closeTime.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                Intent intent = new Intent(YourOrders.this, CompleteOrder.class);
                                startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });



    }

    public void setRestName(String restName) {
        mRestName.setText(restName);
    }

    public void setTotal_price(int totalPrice) {
        total_Price += totalPrice;
        total_price_field.setText("" + total_Price + ",-");
    }

    public void minusTotal_price(int totalPrice) {
        total_Price -= totalPrice;
        total_price_field.setText("" + total_Price + ",-");
    }


    public static String getTotalField() {
        return Integer.toString(total_Price);
    }

    @Override
    public void UpdateRestName(String restName) {
        mRestName = findViewById(R.id.rest_name_cart);
        mRestName.setText(restName);
    }
    public void UpdateTotalPrice(String totalPrice) {
        TextView txtView = findViewById(R.id.total_price);
        txtView.setText(totalPrice);
        if(totalPrice.equals("0 kr")) {
            mRestName.setVisibility(View.GONE);
            mAddMore.setVisibility(View.GONE);
        }
    }


    public void returnToFeed(int status) {
        Intent intent = new Intent(YourOrders.this, MainUActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(status == 0) {
            Toast.makeText(this, "Thanks for your order", Toast.LENGTH_LONG).show();
        }
        else if(status == 1) {
            Toast.makeText(this, "The restaurant is closed", Toast.LENGTH_LONG).show();
        }
        else if(status == 2) {
            Toast.makeText(this, "The restaurant is temporarily closed", Toast.LENGTH_LONG).show();
        }

        startActivity(intent);
        MainUActivity.setRest_id(null);
        MainUActivity.setTotalPrice(0);
        MainUActivity.setRest_name(null);
        MainUActivity.setRest_wait_time(null);
        MainUActivity.getBasketList().clear();
    }

    public static void makeToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }
}
