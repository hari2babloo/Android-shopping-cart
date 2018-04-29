package com.example.jasim.plateup;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.TextView;

import com.example.jasim.plateup.registration.MainActivity;
import com.example.jasim.plateup.settings.SettingsUser;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Categories extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mContentList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CardView category;
    private View itemView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(Categories.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Categories");

        mContentList = (RecyclerView) findViewById(R.id.category_list);
        mContentList.setHasFixedSize(true);
        mContentList.setLayoutManager(new GridLayoutManager(this, 2));
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
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Category, Categories.ContentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, Categories.ContentViewHolder>(
                Category.class, R.layout.category_row, Categories.ContentViewHolder.class, mDatabase
        ) {

            @Override
            protected void populateViewHolder(Categories.ContentViewHolder viewHolder, final Category model, int position) {
                viewHolder.mView.findViewById(R.id.category_box).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent specificCategory;
                        String category = model.getName();
                        System.out.println(category);
                        specificCategory = new Intent(Categories.this, Restaurant.class);
                        specificCategory.putExtra("category", category);
                        startActivity(specificCategory);
                    }
                });
                viewHolder.setName(getApplicationContext(), model.getName());
            }
        };

        mContentList.setAdapter(firebaseRecyclerAdapter);
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
        getMenuInflater().inflate(R.menu.categories, menu);
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

        if (id == R.id.nav_feed) {
            startActivity(new Intent(Categories.this, MainUActivity.class));
        } else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_category) {
            startActivity(new Intent(Categories.this, Categories.class));
        } else if (id == R.id.nav_payment) {

        }
        else if (id == R.id.nav_orders) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(1);
            startActivity(new Intent(Categories.this, Eatit.class));
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(Categories.this, SettingsUser.class));
        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut(); //End user session
            startActivity(new Intent(Categories.this, StartActivity.class)); //Go back to home page
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ContentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(Context ctx, String name){
            TextView category_name = (TextView) mView.findViewById(R.id.category_name);
            category_name.setText(name);
        }
    }
}
