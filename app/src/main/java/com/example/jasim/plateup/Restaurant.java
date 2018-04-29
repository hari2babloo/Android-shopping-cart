package com.example.jasim.plateup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.example.jasim.plateup.registration.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Restaurant extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {


    private RecyclerView mRestaurantList;
    private RecyclerView mMenuList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean isExpanded = false;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
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
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(Restaurant.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mRestaurantList = findViewById(R.id.restaurant_list);
        mRestaurantList.setHasFixedSize(true);
        mRestaurantList.setLayoutManager(new GridLayoutManager(this, 1));

        if(getIntent().getStringExtra("category") != null) {
            this.setTitle(getIntent().getStringExtra("category"));
        }
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("username");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String username = (String) dataSnapshot.getValue();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    // Logic for failure is here...
                }
            });
        }
    }

    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Query categoryRestaurants = mDatabase.orderByChild("type").equalTo("r");

        if(getIntent().hasExtra("category")) {
            categoryRestaurants = mDatabase.orderByChild("type").equalTo("r").getRef().orderByChild("category").equalTo(getIntent().getStringExtra("category"));
        }
        FirebaseRecyclerAdapter<RestaurantModel, ContentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RestaurantModel, ContentViewHolder>(
                RestaurantModel.class, R.layout.restaurant_row, ContentViewHolder.class, categoryRestaurants
        ) {

            @Override
            protected void populateViewHolder(ContentViewHolder viewHolder, final RestaurantModel model, int position) {
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Restaurant.this, ProfileRestaurant.class).putExtra("restaurant", model.getId()));
                    }
                });
                viewHolder.setName(model.getUsername());
                viewHolder.setCategory(model.getCategory());
                viewHolder.setAddress(model.getAddress());
                viewHolder.setCity(model.getCity());
            }
        };
        mRestaurantList.setAdapter(firebaseRecyclerAdapter);
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
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mDatabase.getParent().child("Users").orderByChild("name").equalTo(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mDatabase.getParent().child("Users").orderByChild("name").equalTo(newText);
                return false;
            }
        });
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

        if (id == R.id.nav_eatit) {
            startActivity(new Intent(Restaurant.this, Eatit.class));
        } else if (id == R.id.nav_restaurant) {
            startActivity(new Intent(Restaurant.this, Restaurant.class));
        } else if (id == R.id.nav_category) {
            startActivity(new Intent(Restaurant.this, Categories.class));
        } else if (id == R.id.nav_payment) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut(); //End user session
            startActivity(new Intent(Restaurant.this, MainActivity.class)); //Go back to home page
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        // Restaurant

        public void setName(String name) {
            TextView restaurant_name = (TextView) mView.findViewById(R.id.restaurant_name);
            restaurant_name.setText(name);
        }

        public void setCategory(String category) {
            TextView restaurant_category = (TextView) mView.findViewById(R.id.category_field);
            restaurant_category.setText(category);
        }

        public void setAddress(String address) {
            TextView restaurant_address = (TextView) mView.findViewById(R.id.address_field);
            restaurant_address.setText(address);
        }

        public void setCity(String city) {
            TextView restaurant_city = (TextView) mView.findViewById(R.id.city_field);
            restaurant_city.setText(city);
        }


        public void setDishName(String dishname) {
            TextView dishName = (TextView) mView.findViewById(R.id.content_dish);
            dishName.setText(dishname);
        }

        public void setDescription(String description) {
            TextView dishDescription = (TextView) mView.findViewById(R.id.content_description);
            dishDescription.setText(description);
        }

        public void setPrice(String price) {
            TextView dishPrice = (TextView) mView.findViewById(R.id.content_price);
            dishPrice.setText(price);
        }
    }


    //////// Expand collapse animation

    public static void expand(final View v) {
        v.measure(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? DrawerLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 4);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
