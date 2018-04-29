package com.example.jasim.plateup.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jasim.plateup.Check.Connection;
import com.example.jasim.plateup.Content;
import com.example.jasim.plateup.MainUActivity;
import com.example.jasim.plateup.ProfileRestaurant;
import com.example.jasim.plateup.R;
import com.example.jasim.plateup.RestaurantModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by jasim on 27.12.2017.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    boolean showItem;
    private ArrayList<Content> allContent = new ArrayList<>();
    private ArrayList<Content> shownContent = new ArrayList<>();
    Context ctx;
    private static RestaurantModel user;

    private Location location;


    public FeedAdapter(Context ctx, Location location, RestaurantModel user) {
        this.ctx = ctx;
        this.location = location;
        this.user = user;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mRestaurantName;
        public TextView mDishName;
        public TextView mPqField;
        public TextView mDescription;
        public TextView mPrice;
        public ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mRestaurantName = itemView.findViewById(R.id.rest_name);
            mDishName = itemView.findViewById(R.id.content_dish);
            mPqField = itemView.findViewById(R.id.pq_field);
            mDescription = itemView.findViewById(R.id.content_description);
            mPrice = itemView.findViewById(R.id.content_price);
            mImage = itemView.findViewById(R.id.content_image);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Content content = shownContent.get(position);


        // Restaurant name setText + clickListener
        FirebaseDatabase.getInstance().getReference().child("Users").child(content.getRest_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.mRestaurantName.setText(dataSnapshot.getValue(RestaurantModel.class).getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //holder.mRestaurantName.setText(shownContent.get(position).get());
        holder.mRestaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new Connection(ctx).getConnected()) {
                    ctx.startActivity(new Intent(ctx, ProfileRestaurant.class).putExtra("restaurant", content.getRest_id()));
                }
                else {
                    Toast.makeText(ctx, "Please enable internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Closing Restaurant name

        // DishName setText
        holder.mDishName.setText(shownContent.get(position).getDishname());

        // IF SETNING
        int type = Integer.parseInt(content.getType());
        if(type == 1) {
            holder.mPqField.setText("Quantity: " + shownContent.get(position).getQuantity());
        }
        else if(type == 2) {
            holder.mPqField.setText("Every: " + shownContent.get(position).getEvery());
        }
        else if(type == 3) {
            holder.mPqField.setText("Until: " + shownContent.get(position).getPeriod());
        }

        holder.mDescription.setText("" + shownContent.get(position).getDescription());

        holder.itemView.findViewById(R.id.eat_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new Connection(ctx).getConnected()) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(content.getRest_id());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final RestaurantModel modelO = dataSnapshot.getValue(RestaurantModel.class);
                            System.out.println("RestaurantName: " + modelO.getUsername());
                            if (MainUActivity.rest_id == null || MainUActivity.rest_id.equals(content.getRest_id())) {
                                boolean alreadyThere = false;
                                int where = 0;
                                for (int i = 0; i < MainUActivity.getBasketList().size(); i++) {
                                    if (MainUActivity.getBasketList().get(i).equals(content)) {
                                        alreadyThere = true;
                                        where = i;
                                        break;
                                    }
                                }
                                if (alreadyThere) {
                                    if (MainUActivity.getBasketList().get(where).getQuantity() == null) {
                                        MainUActivity.getBasketList().get(where).setAntall(MainUActivity.getBasketList().get(where).getAntall() + 1);
                                        Toast.makeText(ctx, "Item already in cart, added one more", Toast.LENGTH_SHORT).show();
                                        MainUActivity.totalPrice += Integer.parseInt(content.getPrice());
                                    } else if (MainUActivity.getBasketList().get(where).getAntall() < Integer.parseInt(MainUActivity.getBasketList().get(where).getQuantity())) {
                                        MainUActivity.getBasketList().get(where).setAntall(MainUActivity.getBasketList().get(where).getAntall() + 1);
                                        Toast.makeText(ctx, "Item already in cart, added one more", Toast.LENGTH_SHORT).show();
                                        MainUActivity.totalPrice += Integer.parseInt(content.getPrice());
                                    } else {
                                        Toast.makeText(ctx, "There are only " + Integer.parseInt(MainUActivity.getBasketList().get(where).getQuantity()) + " quantities left of this item.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    MainUActivity.getBasketList().add(content);
                                    DatabaseReference getRestName = FirebaseDatabase.getInstance().getReference().child("Users").child(content.getRest_id());
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
                                    Toast.makeText(ctx, "Added to cart", Toast.LENGTH_SHORT).show();
                                    MainUActivity.totalPrice += Integer.parseInt(content.getPrice());
                                }
                                if (MainUActivity.rest_id == null) {
                                    MainUActivity.rest_id = content.getRest_id();
                                    MainUActivity.rest_name = content.getRestaurant_name();
                                    //MainUActivity.rest_wait_time = modelO.getWaittime();
                                }
                            } else {
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(ctx);
                                }
                                builder.setTitle("Delete entry")
                                        .setMessage("You can't add an item from another restaurant. " +
                                                "Do you want to empty your cart and add this new item?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                MainUActivity.getBasketList().clear();
                                                MainUActivity.totalPrice = 0;
                                                MainUActivity.totalPrice = Integer.parseInt(content.getPrice());
                                                MainUActivity.setTotalPrice(MainUActivity.totalPrice);
                                                MainUActivity.getBasketList().add(content);
                                                MainUActivity.rest_id = content.getRest_id();
                                                MainUActivity.rest_name = modelO.getUsername();
                                                System.out.println("RestaurantName: " + MainUActivity.rest_name);
                                                MainUActivity.rest_wait_time = modelO.getWaittime();
                                                Toast.makeText(ctx, "Added to cart", Toast.LENGTH_SHORT).show();
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
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
                else {
                    Toast.makeText(ctx, "Please enable internet connection to add a dish", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Allergies setOnClicklistener

        holder.itemView.findViewById(R.id.allergies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(ctx).create(); //Read Update
                alertDialog.setTitle("Allergenes");

                String allergies = findAllergies(content);

                alertDialog.setMessage(allergies);

                alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                alertDialog.show();  //<-- See This!
            }
        });

        // Closing allergies

        holder.mPrice.setText(shownContent.get(position).getPrice());
        Picasso.with(ctx).load(shownContent.get(position).getImage()).into(holder.mImage);


        // Eat it button listener


    }

    private String findAllergies(Content model) {
        String msg = "";
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
        return msg;
    }

    public void sortAfterRange() {
        shownContent.remove(0);
    }

    public void addItem(final Content content) {
        if(content.getRest_id() != null) {
            System.out.println("Legger til items paa nytt.");
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(content.getRest_id());
            showItem = true;
            final ArrayList<Content> basket = MainUActivity.getBasketList();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final RestaurantModel modelO = dataSnapshot.getValue(RestaurantModel.class);
                    final Content model = content;
                    boolean closed = true;
                    showItem = true;
                    Calendar c = Calendar.getInstance();
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date toDays = new Date();
                    try {
                        toDays = format.parse(MainUActivity.toDaysDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c.setTime(toDays);


                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    int currentTime = Integer.parseInt(MainUActivity.time);
                    System.out.println("Time: " + MainUActivity.time);
                    String whichOpening = "first";
                    for (int i = 0; i < Integer.parseInt(modelO.getAmountOpenings()) + 1; i++) {
                        if (i == 1) {
                            whichOpening = "second";
                        } else if (i == 2) {
                            whichOpening = "third";
                        }
                        if (modelO.getOpenings().get(whichOpening) != null) {
                            int openDay = Integer.parseInt(modelO.getOpenings().get(whichOpening).getFromDay());
                            int closeDay = Integer.parseInt(modelO.getOpenings().get(whichOpening).getToDay());
                            // Det vanlige tilfellet hvor fromDay er mindre enn toDay, da maa vi bare sjekke om vi befinner oss mellom.
                            if (openDay < closeDay) {
                                if (dayOfWeek >= openDay && dayOfWeek <= closeDay) {
                                    break;
                                }
                            }
                            // Ikke vanlige tilfellet hvor fromDay er storre en toDay.
                            else {
                                if (dayOfWeek >= openDay && dayOfWeek >= closeDay || dayOfWeek <= openDay && dayOfWeek <= closeDay) {
                                    break;
                                }
                            }
                        } else {
                            whichOpening = "closed";
                            System.out.println("WhichOpening: Vi er stengt!");
                        }
                    }
                    System.out.println("WhichOpening DEBUG: " + whichOpening + " " + model.getDishname());
                    int openTime = 0;
                    int closeTime = 0;

                    // Tester om lokken har gaatt over 3 ganger hvis den har det, skjuler vi i feed.
                    if (whichOpening.equals("closed")) {
                        System.out.println("WhichOpening: Vi er stengt!");
                        allContent.add(content);
                        showItem = false;
                    }
                    // Hvis ikke, legger vi inn open og close time for den dagen.
                    else {
                        openTime = Integer.parseInt(modelO.getOpenings().get(whichOpening).getOpen());
                        closeTime = Integer.parseInt(modelO.getOpenings().get(whichOpening).getClose());
                    }
                    // Tester bare om den ikke har gaatt 3 ganger
                    if (!whichOpening.equals("closed")) {
                        DateFormat formatter = new SimpleDateFormat("HHmm");
                        Date openDate = new Date();
                        Date closeDate = new Date();
                        try {
                            openDate = formatter.parse(modelO.getOpenings().get(whichOpening).getOpen());
                            closeDate = formatter.parse(modelO.getOpenings().get(whichOpening).getClose());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(openDate);
                        calendar.add(Calendar.MINUTE, +30);
                        String openString = formatter.format(calendar.getTime());
                        calendar.setTime(closeDate);
                        calendar.add(Calendar.MINUTE, -15);
                        String closeString = formatter.format(calendar.getTime());
                        openTime = Integer.parseInt(openString);
                        closeTime = Integer.parseInt(closeString);
                        System.out.println("WhichOpening: " + openTime + " " + closeTime);

                        if (openTime < closeTime && !whichOpening.equals("closed")) {
                            // Restauranten er stengt fordi status er closed
                            if (modelO.getStatus().equals("closed")) {
                                allContent.add(content);
                                showItem = false;
                            }
                            // Restauranten er open.
                            else if (currentTime > openTime && currentTime < closeTime) {

                                if (!setRestSaleType(model, dayOfWeek)) {
                                    allContent.add(content);
                                    showItem = false;
                                }
                            } else {
                                allContent.add(content);
                                showItem = false;
                            }
                        } else if (openTime > closeTime && !whichOpening.equals("closed")) {
                            System.out.println("Vi kommer hit: " + content.getDishname() + currentTime + showItem);
                            System.out.println(modelO.getUsername());
                            if (modelO.getStatus().equals("closed")) {
                                allContent.add(content);
                                showItem = false;
                            }
                            // Tester om restauranten er open. Inni er en if som sjekker et spesialtilfelle.
                            else if ((currentTime > openTime && currentTime > closeTime) || (currentTime < openTime && currentTime < closeTime)) {
                                if (!setRestSaleType(model, dayOfWeek)) {
                                    System.out.println("Vi kommer hit extra");
                                    allContent.add(content);
                                    showItem = false;
                                }
                            } else {
                                allContent.add(content);
                                showItem = false;
                            }
                        }
                    }
                    System.out.println("showItem: " + content);
                    allContent.add(content);
                    if (showItem) {
                        content.setCategory(modelO.getCategory());
                        shownContent.add(content);

                        for (Content con : shownContent) {
                            Location targetLocation = new Location("");
                            targetLocation.setLatitude(modelO.getLatitude());
                            targetLocation.setLongitude(modelO.getLongitude());
                            if (location != null) {
                                content.setDistanceTo(location.distanceTo(targetLocation));
                                System.out.println("Vi har lokasjon!");
                            } else {
                                content.setDistanceTo(0);
                            }
                        }
                        Collections.sort(shownContent);
                        notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return shownContent.size();
    }


    public static boolean setRestSaleType(Content model, int dayOfWeek) {
        switch (model.getType()) {
            case "1":
                if (model.getQuantity().equals("0")) {
                    return false;
                }
                break;
            case "2":

                int day_of_week = MainUActivity.convertDaytoNumber(model.getEvery());
                if (day_of_week != dayOfWeek) {
                    return false;
                }
                break;
            case "3":
                SimpleDateFormat after = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date newDate = after.parse(model.getPeriod());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(newDate);
                    calendar.add(Calendar.HOUR_OF_DAY, 23);
                    calendar.add(Calendar.MINUTE,59);
                    newDate = calendar.getTime();
                    if (MainUActivity.day.after(newDate)) {
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return true;
    }

}
