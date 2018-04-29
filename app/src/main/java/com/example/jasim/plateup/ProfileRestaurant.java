package com.example.jasim.plateup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.adapters.MenuAdapter;
import com.example.jasim.plateup.registration.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nshmura.recyclertablayout.RecyclerTabLayout;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileRestaurant extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener{

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference userRef;
    private DatabaseReference menuReference;

    private StorageReference mStorage;

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;

    private static CharSequence[] items;

    private RestaurantModel modelO;
    private TextView mName;
    private TextView mAddress;
    private TextView mCity;
    private TextView mTelephone;
    private TextView mStatus;
    private TextView mStatusTime;
    private TextView mStatus2;
    private TextView mStatus2Time;
    private TextView mStatus3;
    private TextView mStatus3Time;

    private RatingBar mRatingBarShow;
    private RatingBar mRatingBar;

    private Button eat_it;
    private Button mBook;
    private Button mGhostRating;
    private Button mRate;

    private RestaurantModel model;
    private HashMap<String, String> checkRaters;
    private String UserName;
    private boolean hasRated;

    private RecyclerView mMenuList;
    private RecyclerView mContentList;
    private TextView menu;
    private TextView offers;
    private TextView bestsellers;
    private TextView waitingTime;

    private LinearLayout menuContent;
    private LinearLayout offersContent;
    private LinearLayout bestsellerContent;
    private String restID;
    private ImageButton profilePicture;
    private LinearLayout mClosed;
    private LinearLayout mBusy;
    private boolean isClosed = false;

    private static int position = 0;


    private ArrayList<Content> offersList = new ArrayList<>();

    protected RecyclerTabLayout mRecyclerTabLayout;

    private HashMap<Integer, String> categories = new HashMap<>();
    private int i;

    HashMap<Integer, HashMap<String, Content>> dishes = new HashMap<>();

    private static Activity activityz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_restaurant);
        activityz = this;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rating_dialog);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent intent = getIntent();
        restID = intent.getStringExtra("restaurant");
        // Send user back to Login
        MainUActivity.MyTask task = new MainUActivity.MyTask();
        task.execute("ntp.uio.no");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("offers");
        menuReference = FirebaseDatabase.getInstance().getReference().child("Users").child(restID).child("Menu");
        dishes = new HashMap<>();
        i = 1;
        categories.put(0, "Offers");
        menuReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final Query offers = FirebaseDatabase.getInstance().getReference("offers").orderByChild("rest_id").equalTo(restID);

                offers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            dishes.put(i, (HashMap<String, Content>)postSnapshot.getValue());
                            categories.put(i, postSnapshot.getKey().substring(0));
                            i++;
                        }
                        for(DataSnapshot snap: dataSnapshot2.getChildren()) {
                            offersList.add(snap.getValue(Content.class));
                        }

                        MenuAdapter adapter = new MenuAdapter(offersList, dishes, categories, getApplicationContext(), ProfileRestaurant.this, restID);

                        ViewPager viewPager = findViewById(R.id.view_pager);
                        viewPager.setAdapter(adapter);


                        mRecyclerTabLayout = findViewById(R.id.recycler_tab_layout);
                        mRecyclerTabLayout.setUpWithViewPager(viewPager);
                        mRecyclerTabLayout.bringToFront();
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
        mAuth = FirebaseAuth.getInstance();
        // kanskje if test
        mName = (TextView) findViewById(R.id.r_name);
        final String getCurrentProfile = getIntent().getStringExtra("restaurant");
        mName.setText(getCurrentProfile);
        waitingTime = findViewById(R.id.waiting_time);
        mAddress = findViewById(R.id.r_address);
        mCity = findViewById(R.id.r_city);
        mTelephone = findViewById(R.id.r_telephone);
        mStatus = findViewById(R.id.r_status);
        mStatusTime = findViewById(R.id.r_statustime);
        mStatus2 = findViewById(R.id.r_status2);
        mStatus2Time = findViewById(R.id.r_status2time);
        mStatus3 = findViewById(R.id.r_status3);
        mStatus3Time = findViewById(R.id.r_status3time);
        mRate = (Button)dialog.findViewById(R.id.rate);
        mRatingBar = (RatingBar)dialog.findViewById(R.id.rating_bar);
        mRatingBarShow = (RatingBar)findViewById(R.id.rating_bar_ghost);
        mGhostRating = (Button)findViewById(R.id.ghost_rating);
        mRatingBarShow.setStepSize(1);
        //mBook = (Button)findViewById(R.id.booking);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(ProfileRestaurant.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                else {

                    final String getCurrentProfile = getIntent().getStringExtra("restaurant");
                    userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("username");

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserName = (String)dataSnapshot.getValue();
                            mDatabase.getParent().child("Users").child(getCurrentProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot modelSnapshot) {
                                    model = modelSnapshot.getValue(RestaurantModel.class);
                                    restID = model.getId();
                                    profilePicture = findViewById(R.id.profile_picture);
                                    mStorage = FirebaseStorage.getInstance().getReference();
                                    if(model.getImage() != null) {
                                        System.out.println("Vi har et bilde");
                                        Picasso.with(getApplicationContext())
                                                .load(model.getImage())
                                                .resize(200, 150).into(profilePicture);
                                    }
                                    if(model.getId().equals(mAuth.getCurrentUser().getUid())) {
                                        System.out.println("Vi har ikke et bilde");
                                        profilePicture.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                                galleryIntent.setType("image/*");
                                                startActivityForResult(galleryIntent, GALLERY_REQUEST);

                                            }
                                        });
                                    }
                                    waitingTime.setText(model.getWaittime() + "min");
                                    mName.setText("\n" + model.getUsername());
                                    mAddress.setText(model.getAddress());
                                    mCity.setText(model.getCity());
                                    mTelephone.setText(model.getTelephone());
                                    mRatingBarShow.setRating(Float.parseFloat(model.getRating())/Float.parseFloat(model.getAmountraters()));

                                    int amountOpenings = Integer.parseInt(model.getAmountOpenings());

                                    mStatus.setText("\n" + DayMonthConverter.numberToDay(model.getOpenings().get("first").getFromDay()) + " - " + DayMonthConverter.numberToDay(model.getOpenings().get("first").getToDay()));
                                    mStatusTime.setText(model.getOpenings().get("first").getOpen() + " - " + model.getOpenings().get("first").getClose());
                                    if(amountOpenings > 1) {
                                        mStatus2.setVisibility(View.VISIBLE);
                                        mStatus2Time.setVisibility(View.VISIBLE);
                                        mStatus2.setText(DayMonthConverter.numberToDay(model.getOpenings().get("second").getFromDay()) + " - " + DayMonthConverter.numberToDay(model.getOpenings().get("second").getToDay()));
                                        mStatus2Time.setText(model.getOpenings().get("second").getOpen() + " - " + model.getOpenings().get("second").getClose());
                                        if(amountOpenings == 3) {
                                            mStatus3.setVisibility(View.VISIBLE);
                                            mStatus3Time.setVisibility(View.VISIBLE);
                                            mStatus3.setText(DayMonthConverter.numberToDay(model.getOpenings().get("third").getFromDay()) + " - " + DayMonthConverter.numberToDay(model.getOpenings().get("third").getToDay()));
                                            mStatus3Time.setText(model.getOpenings().get("third").getOpen() + " - " + model.getOpenings().get("third").getClose());
                                        }
                                    }

                                    if(model.getType().equals("r")) {
                                        //mBook.setVisibility(View.VISIBLE);
                                    }
                        /*mBook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ProfileRestaurant.this, BookingActivity.class).putExtra("id", model.getId()));
                            }
                        });*/

                                    checkRaters = model.getRaters();

                                    if(checkRaters == null || !checkRaters.containsKey(mAuth.getCurrentUser().getUid())) {
                                        System.out.println("mName: " + model.getUsername() + " UserName: " + UserName);
                                        if(!(model.getUsername().equals(UserName)) && !hasRated) {
                                            mGhostRating.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    System.out.println("Model2: " + getCurrentProfile);
                                                    mDatabase.getParent().child("Users").child(getCurrentProfile).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            RestaurantModel model2 = dataSnapshot.getValue(RestaurantModel.class);
                                                            checkRaters = model2.getRaters();

                                                            if (checkRaters != null) {
                                                                if (!hasRated && !model2.getRaters().containsKey(mAuth.getCurrentUser().getUid())) {
                                                                    dialog.show();
                                                                }
                                                            }
                                                            else if(!hasRated) {
                                                                dialog.show();
                                                            }
                                                            vote();
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        else {
                                            //mBook.setVisibility(View.GONE);
                                        }
                                    }


                                    mRate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String rating = Float.toString(mRatingBar.getRating());
                                            mDatabase.getParent().child("Users").child(model.getId()).child("rating").setValue(Float.toString(Float.parseFloat(model.getRating()) + Float.parseFloat(rating)));
                                            mDatabase.getParent().child("Users").child(model.getId()).child("amountraters").setValue(Integer.toString(Integer.parseInt(model.getAmountraters()) + 1));
                                            mDatabase.getParent().child("Users").child(model.getId()).child("raters").child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getUid());
                                            mRatingBarShow.setRating((Float.parseFloat(model.getRating()) + Float.parseFloat(rating)) / (Integer.parseInt(model.getAmountraters()) + 1));
                                            dialog.dismiss();
                                        }
                                    });
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
        };


        DatabaseReference mContentDatabase = FirebaseDatabase.getInstance().getReference().child("offers");

        mAuth.addAuthStateListener(mAuthListener);



    }

    protected void onStart() {
        super.onStart();
    }




    public static int convertDaytoNumber(String day) {
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
            startActivity(new Intent(ProfileRestaurant.this, Eatit.class));
        } else if (id == R.id.nav_restaurant) {
            startActivity(new Intent(ProfileRestaurant.this, Restaurant.class));
        } else if (id == R.id.nav_category) {
            startActivity(new Intent(ProfileRestaurant.this, Categories.class));
        } else if (id == R.id.nav_payment) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut(); //End user session
            startActivity(new Intent(ProfileRestaurant.this, MainActivity.class)); //Go back to home page
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
        // DishStuff

        public void setDishname(String dishname) {
            TextView content_dish = (TextView) mView.findViewById(R.id.content_dish);
            content_dish.setText(dishname);
        }

        public void setDescription(String description) {
            TextView content_description = (TextView) mView.findViewById(R.id.content_description);
            content_description.setText(description);
        }

        public void setPrice(String price) {
            TextView content_price = (TextView) mView.findViewById(R.id.content_price);
            content_price.setText(price);
        }
    }
    public void vote() {
        hasRated = true;
    }

    public static void updateContent(String offerId) {
        activityz.startActivity(new Intent(activityz, EditContent.class).putExtra("offer_id", offerId));
    }

    public static void showAlert(Content model, Activity activityx) {
        String msg = "";

        AlertDialog alertDialog = new AlertDialog.Builder(activityx).create(); //Read Update
        alertDialog.setTitle("Allergenes");
        int i = model.getAllergies().size();
        if(model.getAllergies().containsKey("Gluten")) {
            msg += "Gluten";
            i--;
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Shellfish")) {
            i--;
            msg += "Shellfish";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Egg")) {
            i--;
            msg += "Egg";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Fish")) {
            i--;
            msg += "Fish";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Peanuts")) {
            i--;
            msg += "Peanuts";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Soy")) {
            i--;
            msg += "Soy";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Milk")) {
            i--;
            msg += "Milk";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Nuts")) {
            i--;
            msg += "Nuts";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Celery")) {
            i--;
            msg += "Celery";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Mustard")) {
            i--;
            msg += "Mustard";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Sesame seeds")) {
            i--;
            msg += "Sesame seeds";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Lupine")) {
            i--;
            msg += "Lupine, ";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Molluscs")) {
            i--;
            msg += "Molluscs";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Sulphur dioxide")) {
            i--;
            msg += "Sulphur dioxide";
            if(i != 0) {
                msg += ", ";
            }
        }
        if(model.getAllergies().containsKey("Sulphites")) {
            i--;
            msg += "Sulphites";
            if(i != 0) {
                msg += ", ";
            }
        }


        alertDialog.setMessage(msg);

        alertDialog.setButton("Back..", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });

        alertDialog.show();
    }
    public static boolean showChoose(final Content model, final RestaurantModel modelO, final String restId, Activity activityx) {
        ChooseMeat chooseMeat = model.getChoose().getChoosemeat();
        ChooseSides chooseSides = model.getChoose().getChoosesides();
        int i = 0;
        if(model.getChoose().getChoosemeat().getMeat() != null) {
            items = new CharSequence[model.getChoose().getChoosemeat().getMeat().size()];
            for (Map.Entry<String, String> entry : model.getChoose().getChoosemeat().getMeat().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                items[i] = key;
                i++;
            }
        }
        else {
            items = new CharSequence[chooseMeat.getMeat().size()];
            for (Map.Entry<String, String> entry : chooseSides.getSides().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                items[i] = key;
                i++;
            }
        }
        System.out.println("AvitivtyZ: " + activityz + " activityX: " + activityx);
        AlertDialog.Builder builder;
        if(activityx != null) {
            System.out.println("har vi en activity? " + activityx);
            builder = new AlertDialog.Builder(activityx);
        }
        else {
            builder = new AlertDialog.Builder(activityz);
        }
        builder.setTitle("Choose between")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                    }
                })

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(model.getExtraInfo().containsKey(items[position].toString())) {
                            model.getExtraInfo().put(items[position].toString(), model.getExtraInfo().get(items[position].toString()) + 1);
                        }
                        else {
                            model.getExtraInfo().put(items[position].toString(), 1);
                        }
                        addToCart(modelO, model, restId);
                        position = 0;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();
        builder.show();
        return true;
    }

    public static void addToCart(final RestaurantModel modelO, final Content model, final String restId) {
        if (MainUActivity.getRest_id() == null || MainUActivity.getRest_id().equals(restId)) {
            boolean alreadyThere = false;
            int where = 0;
            for (int i = 0; i < MainUActivity.getBasketList().size(); i++) {
                if (MainUActivity.getBasketList().get(i).equals(model)) {
                    alreadyThere = true;
                    where = i;
                    break;
                }
            }
            DatabaseReference getRestName = FirebaseDatabase.getInstance().getReference().child("Users").child(restId);
            getRestName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    RestaurantModel rModel = dataSnapshot.getValue(RestaurantModel.class);
                    MainUActivity.setRest_name(rModel.getUsername());
                    MainUActivity.setRest_id(rModel.getId());
                    MainUActivity.setRest_wait_time(rModel.getWaittime());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (alreadyThere) {
                System.out.println("Den er allerede i carten!");
                if(MainUActivity.getBasketList().get(where).getQuantity() == null) {
                    MainUActivity.getBasketList().get(where).setAntall(MainUActivity.getBasketList().get(where).getAntall() + 1);
                    Toast.makeText(MainUActivity.activity, "Item already in cart, added one more", Toast.LENGTH_SHORT).show();
                    MainUActivity.setRest_name(model.getRestaurant_name());
                    MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() + Integer.parseInt(model.getPrice()));
                }
                else if(MainUActivity.getBasketList().get(where).getAntall() < Integer.parseInt(MainUActivity.getBasketList().get(where).getQuantity())) {
                    MainUActivity.getBasketList().get(where).setAntall(MainUActivity.getBasketList().get(where).getAntall() + 1);
                    Toast.makeText(MainUActivity.activity, "Item already in cart, added one more", Toast.LENGTH_SHORT).show();
                    MainUActivity.setRest_name(model.getRestaurant_name());
                    MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() + Integer.parseInt(model.getPrice()));
                }
                else {
                    Toast.makeText(MainUActivity.activity, "There are only " + Integer.parseInt(MainUActivity.getBasketList().get(where).getQuantity()) + " quantities left of this item.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                MainUActivity.getBasketList().add(model);
                System.out.println("RESTiD: " + modelO.getId());
                getRestName = FirebaseDatabase.getInstance().getReference().child("Users").child(modelO.getId());
                getRestName.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RestaurantModel rModel = dataSnapshot.getValue(RestaurantModel.class);
                        MainUActivity.setRest_name(rModel.getUsername());
                        MainUActivity.setRest_id(rModel.getId());
                        MainUActivity.setRest_wait_time(rModel.getWaittime());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                MainUActivity.setTotalPrice(MainUActivity.getTotalPrice() + Integer.parseInt(model.getPrice()));
                Toast.makeText(MainUActivity.activity, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(activityz, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(activityz);
            }
            builder.setTitle("Delete entry")
                    .setMessage("You can't add an item from another restaurant. " +
                            "Do you want to empty your cart and add this new item?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainUActivity.getBasketList().clear();
                            MainUActivity.totalPrice = 0;
                            MainUActivity.totalPrice = Integer.parseInt(model.getPrice());
                            MainUActivity.setTotalPrice(MainUActivity.totalPrice);
                            MainUActivity.getBasketList().add(model);
                            MainUActivity.setRest_id(restId);
                            MainUActivity.setRest_name(modelO.getUsername());
                            MainUActivity.setRest_wait_time(modelO.getWaittime());
                            Toast.makeText(activityz, "Added to cart", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Dont change anything
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {

                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePicture.setImageBitmap(selectedImage);
                final DatabaseReference newPost = mDatabase.push();
                String key = newPost.getKey();
                StorageReference filepath = mStorage.child("Profile_Images").child(key);
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if (downloadUrl != null) {
                            newPost.getParent().getParent().child("Users").child(model.getId()).child("image").setValue(downloadUrl.toString());
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileRestaurant.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ProfileRestaurant.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
